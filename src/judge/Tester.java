package judge;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import entities.Batch;
import entities.ExecutionStatus;
import entities.Testcase;
import entities.TestcaseRun;
import judge.launcher.SourceLauncher;

/**
 * [description]
 * <p>
 * Created on 2021.01.08.
 *
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class Tester {

  public static CompletableFuture<TestcaseRun[]> testBatch(
    Batch batch,
    SourceLauncher launcher,
    ExecutorService pool,
    int timeLimitMillis,
    int memoryLimitKb,
    int outputLimitKb
  ) {
    CompletableFuture<TestcaseRun[]> f = new CompletableFuture<>();
    pool.submit(
      new BatchRunner(
        f,
        batch,
        launcher,
        timeLimitMillis,
        memoryLimitKb,
        outputLimitKb
      )
    );
    return f;
  }

  private static class BatchRunner implements Runnable {
    private final Batch batch;
    private final SourceLauncher launcher;
    private final int timeLimitMillis;
    private final int memoryLimitKb;
    private final int outputLimitKb;

    private CompletableFuture<TestcaseRun[]> f;

    public BatchRunner(
      CompletableFuture<TestcaseRun[]> f,
      Batch batch,
      SourceLauncher launcher,
      int timeLimitMillis,
      int memoryLimitKb,
      int outputLimitKb
    ) {
      this.f = f;
      this.batch = batch;
      this.launcher = launcher;
      this.timeLimitMillis = timeLimitMillis;
      this.memoryLimitKb = memoryLimitKb;
      this.outputLimitKb = outputLimitKb;
    }

    @Override
    public void run() {
      Testcase[] testcases = batch.getTestcases();
      TestcaseRun[] testcaseRuns = new TestcaseRun[testcases.length];
      boolean batchPassed = true;
      for (int i = 0; i < testcases.length; i++) {
        Testcase t = testcases[i];
        TestcaseRun testcaseRun = new TestcaseRun(t);
        if (!batchPassed) {
          testcaseRun.setStatus(ExecutionStatus.SKIPPED);
        } else {
          testcaseRun = this.test(t);
        }
        testcaseRuns[i] = testcaseRun;
      }
      this.f.complete(testcaseRuns);
    }

    private TestcaseRun test(Testcase testcase) {
      TestcaseRun runToReturn = new TestcaseRun(testcase);
      String input = testcase.getInput();
      if (!input.endsWith("\n")) { // make sure the input ends with a newline character
        input += "\n";
      }
      ChildProcess childProcess;
      Process program;
      OutputStream stdin;
      InputStream stdout;
      // launch program
      try {
        childProcess = ChildProcesses.launchChildProcess(
          this.launcher,
          this.timeLimitMillis,
          this.memoryLimitKb
        );
        program = childProcess.getProcess();
        stdin = program.getOutputStream();
        stdout = program.getInputStream();

      } catch (InternalErrorException e) {
        return this.fail(ExecutionStatus.INTERNAL_ERROR, e, true, runToReturn);
      } catch (ProcessNotFoundException e) {
        return this.fail(ExecutionStatus.WRONG_ANSWER, e, false, runToReturn);
      }

      int runDurationMillis = 0;
      // write input, wait for output, and read output
      try {
        BufferedOutputStream bufferedStdin = new BufferedOutputStream(stdin);
        bufferedStdin.write(input.getBytes());
        bufferedStdin.flush();
        long start = System.currentTimeMillis();
        program.waitFor(this.timeLimitMillis, TimeUnit.MILLISECONDS);
        long end = System.currentTimeMillis();
        runDurationMillis = (int)(end - start);
      } catch (IOException e) {
        return this.fail(ExecutionStatus.WRONG_ANSWER, e, false, runToReturn);
      } catch (InterruptedException e) {
        return this.fail(ExecutionStatus.INTERNAL_ERROR, e, true, runToReturn);
      }
      runToReturn.setRunDurationMillis(runDurationMillis);
      runToReturn.setMemoryUsedBytes(childProcess.getMemoryUsedBytes());

      // timed out
      if (program.isAlive()) { 
        runToReturn.setStatus(ExecutionStatus.TIME_LIMIT_EXCEEDED);
        program.destroyForcibly();
      // memory limit exceeded
      } else if (runToReturn.getMemoryUsedBytes() > this.memoryLimitKb*1024) {
        runToReturn.setStatus(ExecutionStatus.MEMORY_LIMIT_EXCEEDED);
      // invalid return code
      } else if (program.exitValue() != 0) {
        runToReturn.setStatus(ExecutionStatus.INVALID_RETURN);
      }

      runToReturn = this.judgeOutput(stdin, stdout, testcase, runToReturn);
      
      // end of testing
      this.closeResources(program, stdin, stdout);
      return runToReturn;
    }

    private TestcaseRun judgeOutput(
      OutputStream stdin,
      InputStream stdout,
      Testcase testcase,
      TestcaseRun testcaseRun
    ) {
      String expectedOutput = testcase.getOutput();
      StringBuilder sb = new StringBuilder();
      // read output
      try {
        InputStreamReader reader = new InputStreamReader(stdout);
        char[] buf = new char[8192];
        int read = reader.read(buf, 0, 8192);
        int byteCount = 0;
        long outputLimitBytes = this.outputLimitKb*1024;
        while (read != -1) {
          byteCount += read;
          if (byteCount > outputLimitBytes) {
            testcaseRun.setStatus(ExecutionStatus.OUTPUT_LIMIT_EXCEEDED);
            break;
          }
          sb.append(buf, 0, 8192);
          read = reader.read(buf, 0, 8192);
        }
      } catch (IOException e) {
        if (testcaseRun.getStatus() == ExecutionStatus.PENDING) {
          testcaseRun.setStatus(ExecutionStatus.WRONG_ANSWER);
        }
      }
      // TODO: Don't forget to document somewhere that
      // leading and trailing whitespaces are not significant, but whitespace between is.
      String programOutput = sb.toString().trim(); 
      
      // compare with expected output if submission hasn't received a status
      if (testcaseRun.getStatus() == ExecutionStatus.PENDING) { 
        if (programOutput.equals(expectedOutput.trim())) {
          testcaseRun.setStatus(ExecutionStatus.ALL_CLEAR);
        } else {
          testcaseRun.setStatus(ExecutionStatus.WRONG_ANSWER);
        }
      }
      testcaseRun.setOutput(programOutput);
      return testcaseRun;
    }

    private TestcaseRun fail(
      ExecutionStatus status,
      Exception exception,
      boolean printStackTrace,
      TestcaseRun run
    ) {
      run.setStatus(status);
      if (printStackTrace) {
        exception.printStackTrace();
      }
      return run;
    }
    
    private void closeResources(
      Process program,
      OutputStream stdin,
      InputStream stdout
    ) {
      program.destroyForcibly();
      try {
        stdin.close();
        stdout.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}