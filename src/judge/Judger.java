package judge;

import java.io.File;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import entities.Batch;
import entities.Entity;
import entities.ExecutionStatus;
import entities.Problem;
import entities.Submission;
import entities.SubmissionResult;
import entities.TestcaseRun;
import judge.checker.SourceCheckerService;
import judge.launcher.SourceLauncher;
import judge.launcher.SourceLauncherService;

/**
 * A {@code Judger} judges submissions and returns
 * {@code SubmissionResult}. It contains a fixed thread pool
 * to submit its tasks, and a file directory for all
 * temporary files and folders generated during the judging
 * process. After a submission is processed, all related
 * resources, files, and directories will be removed.
 * <p>
 * Created on 2021.01.08.
 *
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class Judger {
  /** An {@code ExecutorService} to submit its tasks. */
  private ExecutorService pool;
  /**
   * A {@code File} object representing the file directory for
   * all temporary files and folders generated during the
   * judging process.
   */
  private File tempFileDirectory;

  /**
   * Creates a new {@code Judger} instance with the size of
   * the fixed thread pool and a file directory for all
   * temporary files and folders generated during the judging
   * process.
   *
   * @param poolSize          The size of the fixed thread
   *                          pool to submit its tasks.
   * @param tempFileDirectory A file directory for all
   *                          temporary files and folders
   *                          generated during the judging
   *                          process.
   */
  public Judger(int poolSize, File tempFileDirectory) {
    this.pool = Executors.newFixedThreadPool(poolSize);
    this.tempFileDirectory = tempFileDirectory;
  }

  /**
   * Judges a {@code Submission} according to the
   * {@code Problem} given, and returns a
   * {@code SubmissionResult} containing the information of
   * the judge result.
   *
   * @param submission The submission {@code Entity} to be
   *                   judged.
   * @param problem    The problem used for judging the
   *                   submission, containing necessary
   *                   information such as batches and
   *                   time/memory/output limits.
   * @return A {@code SubmissionResult} containing the
   *         information of the judge result.
   */
  public SubmissionResult judge(Entity<Submission> submission, Entity<Problem> problem) {
    SubmissionResult result = new SubmissionResult(submission.getContent());
    // check if code is clean
    try {
      if (!SourceCheckerService.isClean(submission.getContent())) {
        result.updateStatus(ExecutionStatus.ILLEGAL_CODE);
        return result;
      }
    } catch (UnknownLanguageException unknownLanguageException) {
      result.updateStatus(ExecutionStatus.UNKNOWN_LANGUAGE);
      return result;
    }
    // get launcher
    CompletableFuture<SourceLauncher> launcherFuture =
      SourceLauncherService.getSourceLauncher(
        submission.getContent(),
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

    result = this.testSubmission(submission, problem.getContent(), launcher, result);
    launcher.close();
    return result;
  }

  /**
   * Shuts down the {@code Judger}, in which previously submitted tasks are
   * executed, but no new tasks will be accepted. This should be called as soon
   * as the {@code Judger} will no longer be used.
   */
  public void shutdown() {
    this.pool.shutdown();
  }

  /**
   * Tests the given submission against all batches of
   * testcases in the given problem, updates and returns the
   * {@code SubmissionResult}.
   *
   * @param submission The {@code Submission} to test.
   * @param problem    The {@code Problem} that contains
   *                   batches of testcases.
   * @param launcher   The {@code SourceLauncher} to launch
   *                   the submitted program.
   * @param result     The {@code SubmissionResult} to be
   *                   updated.
   * @return The updated {@code SubmissionResult}.
   */
  @SuppressWarnings("unchecked")
  private SubmissionResult testSubmission(
    Entity<Submission> submission,
    Problem problem,
    SourceLauncher launcher,
    SubmissionResult result
  ) {
    int timeLimitMillis = problem.getTimeLimitMillis();
    int memoryLimitKb = problem.getMemoryLimitKb();
    int outputLimitKb = problem.getOutputLimitKb();
    List<Entity<Batch>> batches = problem.getBatches();

    // store the futures in an array and wait for them to complete
    CompletableFuture<TestcaseRun[]>[] batchRunResults =
      (CompletableFuture<TestcaseRun[]>[])(new CompletableFuture[batches.size()]);
    for (int i = 0; i < batches.size(); i++) {
      batchRunResults[i] = Tester.testBatch(
        submission,
        batches.get(i),
        launcher,
        this.pool,
        timeLimitMillis,
        memoryLimitKb,
        outputLimitKb
      );
    }
    CompletableFuture.allOf(batchRunResults).join();

    // loop through each bach results and grade the submission
    try {
      for (int i = 0; i < batchRunResults.length; i++) {
        this.gradeBatch(
          batches.get(i).getContent().getPoints(),
          batchRunResults[i].get(),
          result
        );
      }
    } catch (InterruptedException | ExecutionException e) {
      return this.fail(ExecutionStatus.INTERNAL_ERROR, e, true, launcher, result);
    }

    return result;
  }

  /**
   * Grades and updates the {@code SubmissionResult} according
   * to the testcase runs of a batch. If all runs are cleared
   * successfully, the {@code SubmissionResult} receives the
   * points of the batch. Otherwise, no points will be added.
   * The {@code SubmissionResult}'s status, run duration, and
   * memory usage will also be updated according to the
   * testcase runs.
   *
   * @param points       The points this batch worth.
   * @param testcaseRuns An array of {@code TestcaseRun},
   *                     representing the submission's test
   *                     results of the batch's testcases.
   * @param result       The {@code SubmissionResult} to be
   *                     updated.
   * @return The updated {@code SubmissionResult}.
   */
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
      result.updateMemoryUsageBytes(run.getMemoryUsageBytes());
    }
    // update score
    if (batchPassed) {
      result.addScore(points);
    }
    return result;
  }

  /**
   * Updates and returns the given {@code SubmissionResult}
   * object upon the fail of the judging process (can be
   * caused either by the judge or the submitted program). If
   * the submission used a {@code SourceLauncher}, the
   * launcher will be closed as well.
   *
   * @param status          The status of the
   *                        {@code SubmissionResult}.
   * @param exception       The exception that caused the
   *                        failing.
   * @param printStackTrace Whether or not the stack trace of
   *                        the exception should be printed.
   * @param launcher        The {@code SourceLauncher} used
   *                        for this submission, or null if
   *                        the submission has not received a
   *                        {@code SourceLauncher} yet.
   * @param result          The {@code SubmissionResult} to be
   *                        updated.
   * @return The updated {@code SubmissionResult}.
   */
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
}
