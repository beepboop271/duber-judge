package judge.entities;

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
import judge.services.Tester;

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
        cause.printStackTrace();
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
        userException.printStackTrace();
      }
      this.updateDatabase(submission);
      launcher.close();
      return;
    }

    // test
    int score = 0;
    int totalDurationMillis = 0;
    double maxMemoryUsageKb = 0;
    Problem problem = submission.getProblem();
    int timeLimitMillis = problem.getTimeLimitMillis();
    int memoryLimitKb = problem.getMemoryLimitKb();
    int outputLimitKb = problem.getOutputLimitKb();
    ExecutionStatus submissionStatus = null;
    Batch[] batches = problem.getBatches();

    // loop through each batch in the problem
    for (int i = 0; i < batches.length; i++) {
      boolean batchPassed = true;
      Batch batch = batches[i];
      Testcase[] testcases = batch.getTestcases();      
      
      // loop through each testcase in the batch
      for (int j = 0; j < testcases.length; j++) {
        Testcase t = testcases[j];
        TestcaseRun run;
        if (!batchPassed) {
          run = new TestcaseRun(t);
          run.setStatus(ExecutionStatus.SKIPPED);
        } else {
          long start = System.currentTimeMillis();
          CompletableFuture<TestcaseRun> caseRunFuture = Tester.test(
            t,
            launcher,
            this.pool,
            timeLimitMillis,
            memoryLimitKb,
            outputLimitKb
          );
          try {
            run = caseRunFuture.get();
            long end = System.currentTimeMillis();
            totalDurationMillis += end - start;
            if (run.getMemoryUsageKb() > maxMemoryUsageKb) { // update max memory usage
              maxMemoryUsageKb = run.getMemoryUsageKb();
            }
            if (run.getStatus() != ExecutionStatus.ALL_CLEAR) {
              batchPassed = false; // skip the rest of the cases in the batch
            }
            
          } catch (CancellationException cancellationException) {
            run = new TestcaseRun(testcases[j]);
            run.setStatus(ExecutionStatus.INTERNAL_ERROR);
            cancellationException.printStackTrace();
  
          } catch (ExecutionException executionException) {
            run = new TestcaseRun(testcases[j]);
            run.setStatus(ExecutionStatus.INTERNAL_ERROR);
            executionException.printStackTrace();
  
          } catch (InterruptedException interruptedException) {
            run = new TestcaseRun(testcases[j]);
            run.setStatus(ExecutionStatus.INTERNAL_ERROR);
            interruptedException.printStackTrace();
          }
        }
        run.setSubmission(submission);
        this.updateDatabase(run);
        // update the submission's status
        // if the current TestcaseRun has a status with a higher priority
        if ((submissionStatus == null) || (run.getStatus().compareTo(submissionStatus) < 0)) {
          submissionStatus = run.getStatus();
        }
      }
      if (batchPassed) {
        score += batch.getPoints();
      }
      // end of batch test
    }

    // end of submission test
    // - calculate score, etc.
    submission.setRunDuration(totalDurationMillis);
    submission.setMemoryUsageKb(maxMemoryUsageKb);
    submission.setScore(score);
    submission.setStatus(submissionStatus);
    // - update submission to the database
    this.updateDatabase(submission);
    // - close resources
    launcher.close();
  }

  public void shutdown() {
    this.pool.shutdown();
  }

  private void updateDatabase(Submission submission) {
    //TODO: write to db
    display(submission);
  }

  private void updateDatabase(TestcaseRun testcaseRun) {
    //TODO: write to db
    display(testcaseRun);
  }

  // ------temp methods

  public static void display(Submission submission) {
    System.out.println("Submission: " + submission);
    System.out.println("Language: " + submission.getLanguage());
    System.out.println("Status: " + submission.getStatus());
    System.out.println("Score: " + submission.getScore());
    System.out.println("Run duration (milliseconds): " + submission.getRunDurationMillis());
    System.out.println("Memory used (kilobytes): " + submission.getMemoryUsageKb());
    System.out.println("Source Code:\n" + submission.getCode());
    System.out.println();
  }

  public static void display(TestcaseRun run) {
    System.out.println("Testcase: " + run.getTestcase() + " of submission " + run.getSubmission());
    System.out.println("Status: " + run.getStatus());
    System.out.println("Run duration (milliseconds): " + run.getRunDurationMillis());
    System.out.println("Memory used (kilobytes): " + run.getMemoryUsageKb());
    System.out.println("Output: " + run.getOutput());
    System.out.println();
  }

}
