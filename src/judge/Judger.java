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
import entities.SubmissionResult;
import entities.TestcaseRun;
import judge.checker.ProgramChecker;
import judge.launcher.ProgramLauncher;
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

public class Judger {

  private ExecutorService pool;
  private File tempFileDirectory;

  public Judger(int poolSize, File tempFileDirectory) {
    this.pool = Executors.newFixedThreadPool(poolSize);
    this.tempFileDirectory = tempFileDirectory;
  }

  public SubmissionResult judge(Submission submission) {
    SubmissionResult result = new SubmissionResult(submission);
    // check if code is clean
    try {
      if (!ProgramChecker.isClean(submission)) {
        result.setStatus(ExecutionStatus.ILLEGAL_CODE);
        return result;
      }
    } catch (UnknownLanguageException unknownLanguageException) {
      result.setStatus(ExecutionStatus.UNKNOWN_LANGUAGE);
      return result;
    }
    // get launcher
    CompletableFuture<SourceLauncher> launcherFuture =
      ProgramLauncher.getSourceLauncher(
        submission, 
        this.tempFileDirectory,
        this.pool
      );
    SourceLauncher launcher;
    try {
      launcher = launcherFuture.get();
    } catch (ExecutionException e) {
      if (e.getCause() instanceof UnknownLanguageException) {
        return this.fail(ExecutionStatus.UNKNOWN_LANGUAGE, e, false, null, result);
      } else if (e.getCause() instanceof CompileErrorException) {
        return this.fail(ExecutionStatus.COMPILE_ERROR, e, false, null, result);
      } else {
        return this.fail(ExecutionStatus.INTERNAL_ERROR, e, true, null, result);
      }
    } catch (CancellationException | InterruptedException e) {
      return this.fail(ExecutionStatus.INTERNAL_ERROR, e, true, null, result);
    }

    result = this.testSubmission(submission, launcher, result);
    launcher.close();
    return result;
  }

  public void shutdown() {
    this.pool.shutdown();
  }

  @SuppressWarnings("unchecked")
  private SubmissionResult testSubmission(
    Submission submission,
    SourceLauncher launcher,
    SubmissionResult result
  ) {
    Problem problem = submission.getProblem();
    int timeLimitMillis = problem.getTimeLimitMillis();
    int memoryLimitKb = problem.getMemoryLimitKb();
    int outputLimitKb = problem.getOutputLimitKb();
    Batch[] batches = problem.getBatches();

    // store the futures in an array and wait for them to complete
    CompletableFuture<TestcaseRun[]>[] batchRunResults =
      (CompletableFuture<TestcaseRun[]>[])(new CompletableFuture[batches.length]);
    for (int i = 0; i < batches.length; i++) {
      batchRunResults[i] = Tester.testBatch(
        batches[i], launcher, this.pool, timeLimitMillis, memoryLimitKb, outputLimitKb
      );
    }
    CompletableFuture.allOf(batchRunResults).join();

    // loop through each bach results and grade the submission
    try {
      for (int i = 0; i < batchRunResults.length; i++) {
        this.gradeBatch(batches[i].getPoints(), batchRunResults[i].get(), result);
      }
    } catch (InterruptedException | ExecutionException e) {
      return this.fail(ExecutionStatus.INTERNAL_ERROR, e, true, launcher, result);
    }

    return result;
  }

  private SubmissionResult gradeBatch(
    int points, 
    TestcaseRun[] testcaseRuns,
    SubmissionResult result
  ) {
    boolean batchPassed = true;
    for (int i = 0; i < testcaseRuns.length; i++) {
      TestcaseRun run = testcaseRuns[i];
      result.addTestcaseRun(run);
      // update status
      ExecutionStatus runStatus = run.getStatus();
      result.updateStatus(runStatus);
      if (runStatus != ExecutionStatus.ALL_CLEAR) {
        batchPassed = false;
      }
      // update run duration
      result.addRunDurationMillis(run.getRunDurationMillis());
      // update memory usage
      result.updateMemoryUsedBytes(run.getMemoryUsedBytes());
    }
    // update score
    if (batchPassed) {
      result.addScore(points);
    }
    return result;
  }

  private SubmissionResult fail(
    ExecutionStatus status,
    Exception exception,
    boolean printStackTrace,
    SourceLauncher launcher,
    SubmissionResult result
  ) {
    result.setStatus(status);
    if (printStackTrace) {
      exception.printStackTrace();
    }
    if (launcher != null) {
      launcher.close();
    }
    return result;
  }


  // ------temp methods

  public static void display(SubmissionResult submission) {
    System.out.println("Submission: " + submission);
    System.out.println("Language: " + submission.getSubmission().getLanguage());
    System.out.println("Status: " + submission.getStatus());
    System.out.println("Score: " + submission.getScore());
    System.out.println("Run duration (milliseconds): " + submission.getRunDurationMillis());
    System.out.println("Memory used (bytes): " + submission.getMemoryUsedBytes());
    System.out.println("Source Code:\n" + submission.getSubmission().getCode());
    System.out.println();
  }

  public static void display(TestcaseRun run) {
    System.out.println("Testcase: " + run.getTestcase() + " of submission " + run.getSubmission());
    System.out.println("Status: " + run.getStatus());
    System.out.println("Run duration (milliseconds): " + run.getRunDurationMillis());
    System.out.println("Memory used (bytes): " + run.getMemoryUsedBytes());
    System.out.println("Output: " + run.getOutput());
    System.out.println();
  }

}
