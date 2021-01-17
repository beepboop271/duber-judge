package judge;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import entities.ExecutionStatus;
import entities.Testcase;
import entities.TestcaseRun;

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
      System.out.println("Testing testcase: " + this.testcase);
      String input = this.testcase.getInput();
      if (!input.endsWith("\n")) {
        input += "\n";
      }
      String expectedOutput = this.testcase.getOutput();
      StringBuilder sb = new StringBuilder();
      ExecutionStatus status = ExecutionStatus.PENDING;
      long runDurationMillis = 0;

      try {
        Process program = this.programLauncher.launch();
        BufferedOutputStream stdin = new BufferedOutputStream(program.getOutputStream());
        BufferedInputStream stdout = new BufferedInputStream(program.getInputStream());
        
        stdin.write(input.getBytes());
        stdin.flush();
        long start = System.currentTimeMillis();
  
        program.waitFor(this.timeLimitMillis, TimeUnit.MILLISECONDS);
        if (program.isAlive()) { // timed out
          status = ExecutionStatus.TIME_LIMIT_EXCEEDED;
          program.destroyForcibly();
          System.out.println("timed out");
        } else if (program.exitValue() != 0) { // returned with a nonzero exit code
          status = ExecutionStatus.INVALID_RETURN;
          System.out.println("exit code: " + program.exitValue());
        }
        long end = System.currentTimeMillis();
        runDurationMillis = end - start;
        System.out.println("run duration: " + runDurationMillis);

        // read output
        int curByte = stdout.read();
        int byteCount = 0;
        long outputLimitBytes = this.outputLimitKb*1024;
        while (curByte != -1) { //TODO: check for output limit exceeded
          sb.append((char)curByte);
          curByte = stdout.read();
          byteCount++;
          if (byteCount > outputLimitBytes) {
            status = ExecutionStatus.OUTPUT_LIMIT_EXCEEDED;
            break;
          }
        }
        // compare with expected output if submission hasn't received a status
        if (status == ExecutionStatus.PENDING) { 
          if (sb.toString().trim().equals(expectedOutput.trim())) { // trim whitespace
            status = ExecutionStatus.ALL_CLEAR;
          } else {
            status = ExecutionStatus.WRONG_ANSWER;
          }
        }
    
        // end of testing, close resources
        program.destroyForcibly();
        stdin.close();
        stdout.close();

      } catch (InternalErrorException internalErrorException) {
        internalErrorException.printStackTrace();
      } catch (IOException ioException) {
        ioException.printStackTrace();
      } catch (InterruptedException interruptedException) {
        interruptedException.printStackTrace();
      }
      
      // if status is still PENDING (errors caught above), mark as internal error
      if (status == ExecutionStatus.PENDING) {
        status = ExecutionStatus.INTERNAL_ERROR;
      }

      f.complete(
        new TestcaseRun(
          this.testcase,
          runDurationMillis,
          0, //TODO: see if i can figure out memory usage tracking
          status,
          sb.toString()
        )
      );
    }

  }

}
