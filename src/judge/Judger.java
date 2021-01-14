package judge;

import java.io.File;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import entities.Batch;
import entities.ExecutionStatus;
import entities.Problem;
import entities.Submission;
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

public class Judger {

  private ExecutorService pool;
  private File tempFileDirectory;

  public Judger(int poolSize, File tempFileDirectory) {
    this.pool = Executors.newFixedThreadPool(poolSize);
    this.tempFileDirectory = tempFileDirectory;
  }

  @SuppressWarnings("unchecked")
  public void judge(Submission submission) {
    System.out.println("Received submission: " + submission);
    // check
    try {
      if (!ProgramChecker.isClean(submission)) {
        submission.setStatus(ExecutionStatus.ILLEGAL_CODE);
        this.updateDatabase(submission);
        return;
      }
    } catch (UnknownLanguageException unknownLanguageException) {
      submission.setStatus(ExecutionStatus.UNKNOWN_LANGUAGE);
      this.updateDatabase(submission);
      return;
    }
    System.out.println("Preparing to get launcher");
    // get launcher
    CompletableFuture<SourceLauncher> launcherFuture =
      SourceLauncherService.getSourceLauncher(
        submission, 
        this.tempFileDirectory,
        this.pool
      );
    SourceLauncher launcher;
    try {
      launcher = launcherFuture.get();
      
    } catch (CancellationException cancellationException) {
      submission.setStatus(ExecutionStatus.INTERNAL_ERROR);
      this.updateDatabase(submission);
      cancellationException.printStackTrace();
      return;

    } catch (ExecutionException executionException) {
      Throwable cause = executionException.getCause();
      if (cause instanceof UnknownLanguageException) {
        submission.setStatus(ExecutionStatus.UNKNOWN_LANGUAGE);
      } else {
        submission.setStatus(ExecutionStatus.INTERNAL_ERROR);
      }
      this.updateDatabase(submission);
      executionException.printStackTrace();
      return;

    } catch (InterruptedException interruptedException) {
      submission.setStatus(ExecutionStatus.INTERNAL_ERROR);
      this.updateDatabase(submission);
      interruptedException.printStackTrace();
      return;

    }

    try {
      launcher.setup();
    } catch (InternalErrorException internalErrorException) {
      submission.setStatus(ExecutionStatus.INTERNAL_ERROR);
      this.updateDatabase(submission);
      internalErrorException.printStackTrace();
      launcher.close();
      return;

    } catch (UserException userException) {
      if (userException instanceof CompileErrorException) {
        submission.setStatus(ExecutionStatus.COMPILE_ERROR);
      } else {
        submission.setStatus(ExecutionStatus.INTERNAL_ERROR);
      }
      this.updateDatabase(submission);
      launcher.close();
      return;
    }

    System.out.println("Preparing to test");
    // test
    int score = 0;
    long totalDurationMills = 0;
    Problem problem = submission.getProblem();
    long timeLimitMills = problem.getTimeLimitMills();
    long outputLimitBytes = problem.getOutputLimitBytes();
    ExecutionStatus submissionStatus = null;
    Batch[] batches = problem.getBatches();

    // loop through each batch in the problem
    for (int i = 0; i < batches.length; i++) {
      System.out.printf("Batch %d started\n", i+1);
      boolean batchPassed = true;
      Batch batch = batches[i];
      Testcase[] testcases = batch.getTestcases();
      CompletableFuture<TestcaseRun>[] batchCaseRunFutures =
        (CompletableFuture<TestcaseRun>[])(new CompletableFuture[testcases.length]);
      long start = System.currentTimeMillis();

      // loop through each testcase in the batch
      for (int j = 0; j < testcases.length; j++) {
        System.out.printf("Testcase %d of batch %d started\n", j+1, i+1);
        Testcase t = testcases[j];
        CompletableFuture<TestcaseRun> caseRunFuture = Tester.test(
          t,
          launcher,
          this.pool,
          timeLimitMills,
          outputLimitBytes
        );
        batchCaseRunFutures[j] = caseRunFuture;
      }

      CompletableFuture.allOf(batchCaseRunFutures).join(); // wait for all runs to complete
      long end = System.currentTimeMillis();
      totalDurationMills += end - start;
      System.out.println("Batch done");
      for (int j = 0;  j < batchCaseRunFutures.length; j++) {
        CompletableFuture<TestcaseRun> caseRunFuture = batchCaseRunFutures[j];
        TestcaseRun run;
        try {
          run = caseRunFuture.get();
          this.updateDatabase(run);
          
        } catch (CancellationException cancellationException) {
          run = new TestcaseRun(testcases[j]);
          run.setStatus(ExecutionStatus.INTERNAL_ERROR);
          this.updateDatabase(run);
          cancellationException.printStackTrace();

        } catch (ExecutionException executionException) {
          run = new TestcaseRun(testcases[j]);
          run.setStatus(ExecutionStatus.INTERNAL_ERROR);
          this.updateDatabase(run);
          executionException.printStackTrace();

        } catch (InterruptedException interruptedException) {
          run = new TestcaseRun(testcases[j]);
          run.setStatus(ExecutionStatus.INTERNAL_ERROR);
          this.updateDatabase(run);
          interruptedException.printStackTrace();
        }

        // update the batch's status if any of the testcase fails to clear
        if (run.getStatus() != ExecutionStatus.ALL_CLEAR) {
          batchPassed = false;
        }

        // update the submission's status
        // if the current TestcaseRun has a status with a higher priority
        if ((submissionStatus == null) || (run.getStatus().compareTo(submissionStatus) < 0)) {
          submissionStatus = run.getStatus();
        }
      }

      // end of batch test
      if (batchPassed) {
        score += batch.getPoints();
      }
    }

    // end of submission test
    // - calculate score, etc.
    submission.setRunDuration(totalDurationMills);
    submission.setScore(score);
    submission.setStatus(submissionStatus);
    // - update database
    this.updateDatabase(submission);
    // - close resources
    launcher.close();
  }

  public void shutdown() {
    this.pool.shutdown();
  }

  private void updateDatabase(Submission submission) {
    //TODO: write to db
    System.out.println("Updated submission: " + submission);
    this.display(submission);
  }

  private void updateDatabase(TestcaseRun testcaseRun) {
    //TODO: write to db
    System.out.println("Updated testcase run: " + testcaseRun);
    this.display(testcaseRun);
  }

  // ------temp methods

  private void display(Submission submission) {
    System.out.println("Submission: " + submission);
    System.out.println("Language: " + submission.getLanguage());
    System.out.println("Status: " + submission.getStatus());
    System.out.println("Score: " + submission.getScore());
    System.out.println("Run duration (milliseconds): " + submission.getRunDurationMills());
    System.out.println("Source Code:\n" + submission.getCode());
    System.out.println();
  }

  private void display(TestcaseRun run) {
    System.out.println("Testcase: " + run.getTestcase());
    System.out.println("Status: " + run.getStatus());
    System.out.println("Memory Usage (bytes): " + run.getMemoryUsage());
    System.out.println("Run duration (milliseconds): " + run.getRunDurationMills());
    System.out.println("Output: " + run.getOutput());
    System.out.println();
  }

}
