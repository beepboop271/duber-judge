package judge.services;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import entities.ExecutionStatus;
import entities.Testcase;
import entities.TestcaseRun;
import judge.entities.ChildProcess;
import judge.entities.InternalErrorException;
import judge.entities.SourceLauncher;

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

  public static CompletableFuture<TestcaseRun> test(
    Testcase testcase,
    SourceLauncher programLauncher,
    ExecutorService pool,
    int timeLimitMillis,
    int memoryLimitKb,
    int outputLimitKb
  ) {
    CompletableFuture<TestcaseRun> f = new CompletableFuture<TestcaseRun>();
    pool.submit(
      new TestcaseRunner(
        f,
        testcase,
        programLauncher,
        timeLimitMillis,
        memoryLimitKb,
        outputLimitKb
      )
    );
    return f;
  }

  
  private static class TestcaseRunner implements Runnable {

    private final Testcase testcase;
    private final SourceLauncher programLauncher;
    private final int timeLimitMillis;
    private final int memoryLimitKb;
    private final int outputLimitKb;

    private CompletableFuture<TestcaseRun> f;

    public TestcaseRunner(
      CompletableFuture<TestcaseRun> f,
      Testcase testcase,
      SourceLauncher programLauncher,
      int timeLimitMillis,
      int memoryLimitKb,
      int outputLimitKb
    ) {
      this.f = f;
      this.testcase = testcase;
      this.programLauncher = programLauncher;
      this.timeLimitMillis = timeLimitMillis;
      this.memoryLimitKb = memoryLimitKb;
      this.outputLimitKb = outputLimitKb;
    }

    @Override
    public void run() {
      TestcaseRun runToReturn = new TestcaseRun(this.testcase);
      runToReturn.setStatus(ExecutionStatus.PENDING);

      String input = this.testcase.getInput();
      if (!input.endsWith("\n")) {
        input += "\n";
      }
      String expectedOutput = this.testcase.getOutput();
      StringBuilder sb = new StringBuilder();

      try {
        ChildProcess childProcess = GlobalChildProcessService.launchChildProcess(
          this.programLauncher,
          this.memoryLimitKb
        );
        Process program = childProcess.getProcess();
        BufferedOutputStream stdin = new BufferedOutputStream(program.getOutputStream());
        BufferedInputStream stdout = new BufferedInputStream(program.getInputStream());

        long start = System.currentTimeMillis();
        stdin.write(input.getBytes());
        stdin.flush();       
        program.waitFor(this.timeLimitMillis, TimeUnit.MILLISECONDS);
        runToReturn.setMemoryUsageKb(childProcess.getMemoryUsedKb());
        long end = System.currentTimeMillis();
        runToReturn.setRunDurationMillis((int)(end - start)); //TODO: incorrectly 0 sometimes

        // timed out
        if (program.isAlive()) { 
          runToReturn.setStatus(ExecutionStatus.TIME_LIMIT_EXCEEDED);
          program.destroyForcibly();
        // memory limit exceeded
        } else if (runToReturn.getMemoryUsageKb() > this.memoryLimitKb) {
          runToReturn.setStatus(ExecutionStatus.MEMORY_LIMIT_EXCEEDED);
        // invalid return code
        } else if (program.exitValue() != 0) {
          runToReturn.setStatus(ExecutionStatus.INVALID_RETURN);
        }
        

        // read output
        int curByte = stdout.read();
        int byteCount = 0;
        long outputLimitBytes = this.outputLimitKb*1024;
        while (curByte != -1) {
          sb.append((char)curByte);
          curByte = stdout.read();
          byteCount++;
          if (byteCount > outputLimitBytes) {
            runToReturn.setStatus(ExecutionStatus.OUTPUT_LIMIT_EXCEEDED);
            break;
          }
        }
        // compare with expected output if submission hasn't received a status
        if (runToReturn.getStatus() == ExecutionStatus.PENDING) { 
          if (sb.toString().trim().equals(expectedOutput.trim())) { // trim whitespace
            runToReturn.setStatus(ExecutionStatus.ALL_CLEAR);
          } else {
            runToReturn.setStatus(ExecutionStatus.WRONG_ANSWER);
          }
        }
    
        // end of testing, close resources
        program.destroyForcibly();
        stdin.close();
        stdout.close();

      } catch (IOException ioException) {
        //ioException.printStackTrace();
        if (runToReturn.getStatus() == ExecutionStatus.PENDING) {
          runToReturn.setStatus(ExecutionStatus.WRONG_ANSWER);
        }
      } catch (InternalErrorException internalErrorException) {
        internalErrorException.printStackTrace();
      } catch (InterruptedException interruptedException) {
        interruptedException.printStackTrace();
      }

      f.complete(runToReturn);
    }

  }

}
