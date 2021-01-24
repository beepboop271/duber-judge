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
 * Contains a static method that tests a batch of testcases and returns a
 * {@code CompletableFuture} of the testcase runs.
 * <p>
 * Created on 2021.01.08.
 *
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */
public class Tester {
  /**
   * Tests a batch of testcases and returns a {@code CompletableFuture} of the
   * testcase runs.
   *
   * @param batch           The {@code Batch} to be tested.
   * @param launcher        The {@code SourceLauncher} to launch the program.
   * @param pool            The {@code ExecutorService} to submit the task.
   * @param timeLimitMillis The time limit of each testcase, in milliseconds.
   * @param memoryLimitKb   The memory limit of each testcase, in kilobytes.
   * @param outputLimitKb   The output limit of each testcase, in kilobytes.
   * @return                A {@code CompletableFuture} of the testcase runs.
   */
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

  /**
   * A {@Runnable} object that tests a batch of testcases and completes the given
   * {@code CompletableFuture} with testcase runs.
   */
  private static class BatchRunner implements Runnable {
    private static final int BUF_SIZE = 8192;

    /** The {@code Batch} to be tested. */
    private final Batch batch;
    /** The {@code SourceLauncher} to launch the program. */
    private final SourceLauncher launcher;
    /** The time limit of each testcase, in milliseconds. */
    private final int timeLimitMillis;
    /** The memory limit of each testcase, in kilobytes. */
    private final int memoryLimitKb;
    /** The output limit of each testcase, in kilobytes. */
    private final int outputLimitKb;

    /** The {@code CompletableFuture} to be completed with testcase runs. */
    private CompletableFuture<TestcaseRun[]> f;

    /**
     * Creates a new {@code BatchRunner} instance with time, memory and output
     * restrictions that apply to each testcase.
     *
     * @param f               The {@code CompletableFuture} to be completed with
     *                        testcase runs.
     * @param batch           The {@code Batch} to be tested.
     * @param launcher        The {@code SourceLauncher} to launch the program.
     * @param timeLimitMillis The time limit of each testcase, in milliseconds.
     * @param memoryLimitKb   The memory limit of each testcase, in kilobytes.
     * @param outputLimitKb   The output limit of each testcase, in kilobytes.
     */
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

    /**
     * Tests each of the testcase in the batch, and completes the given
     * {@code CompletableFuture} with testcase runs.
     */
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
          if (testcaseRun.getStatus() != ExecutionStatus.ALL_CLEAR) {
            batchPassed = false;
          }
        }
        testcaseRuns[i] = testcaseRun;
      }
      this.f.complete(testcaseRuns);
    }

    /**
     * Tests a given testcase and returns a {@code TestcaseRun} object representing
     * the result.
     *
     * @param testcase The {@code Testcase} to be tested.
     * @return         A {@code TestcaseRun} object representing the result.
     */
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
      runToReturn.setMemoryUsedBytes(childProcess.getMemoryUsageBytes());

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

      runToReturn = this.judgeOutput(stdout, testcase.getOutput(), runToReturn);

      // end of testing, close resources
      program.destroyForcibly();
      try {
        stdin.close();
        stdout.close();
      } catch (IOException e) {
        e.printStackTrace();
      }

      return runToReturn;
    }

    /**
     * Compares the output of the program's process to the expected output
     * and updates the {@code TestcaseRun} result accordingly.
     * <p>
     * Note: while judging the output, leading and trailing whitespaces are
     * not significant, but whitespace between is.
     *
     * @param stdout          The {@code InputStream} of the program's process.
     * @param expectedOutput  The expected output of the program.
     * @param testcaseRun     The {@code TestcaseRun} to be updated.
     * @return                The updated {@code TestcaseRun}.
     */
    private TestcaseRun judgeOutput(
      InputStream stdout,
      String expectedOutput,
      TestcaseRun testcaseRun
    ) {
      StringBuilder sb = new StringBuilder();
      // read output
      try {
        InputStreamReader reader = new InputStreamReader(stdout);
        char[] buf = new char[BatchRunner.BUF_SIZE];
        int read = reader.read(buf, 0, BatchRunner.BUF_SIZE);
        int byteCount = 0;
        long outputLimitBytes = this.outputLimitKb*1024;
        while (read != -1) {
          byteCount += read;
          if (byteCount > outputLimitBytes) {
            testcaseRun.setStatus(ExecutionStatus.OUTPUT_LIMIT_EXCEEDED);
            break;
          }
          sb.append(buf, 0, BatchRunner.BUF_SIZE);
          read = reader.read(buf, 0, BatchRunner.BUF_SIZE);
        }
      } catch (IOException e) {
        if (testcaseRun.getStatus() == ExecutionStatus.PENDING) {
          testcaseRun.setStatus(ExecutionStatus.WRONG_ANSWER);
        }
      }
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

    /**
     * Updates and returns the given {@code TestcaseRun} object upon the fail of
     * the judging process (can be caused either by the judge or the submitted
     * program).
     *
     * @param status          The status of the {@code TestcaseRun}.
     * @param exception       The exception that caused the failing.
     * @param printStackTrace Whether or not the stack trace of the exception
     *                        should be printed.
     * @param run             The {@code TestcaseRun} to be updated.
     * @return                The updated {@code TestcaseRun}.
     */
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
  }
}