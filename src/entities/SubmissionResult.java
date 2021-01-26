package entities;

import java.util.ArrayList;
import java.util.List;

/**
 * An entity representing a submission result.
 * <p>
 * Created on 2021.01.21.
 *
 * @author Shari Sun, Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */
public class SubmissionResult {
  /** The original submission. */
  private Submission submission;
  /** The current status of the submission. */
  private ExecutionStatus status;
  /** The score of the submission. */
  private int score;
  /**
   * The total amount of time the submission took to run, in
   * milliseconds.
   */
  private int runDurationMillis;
  /**
   * The maximum amount of memory used among its testcase
   * runs, in bytes.
   */
  private long memoryUsageBytes;
  /** The testcase runs. */
  private List<TestcaseRun> testcaseRuns;

  /**
   * Constructs a new {@code SubmissionResult} instance with
   * the given submission alongside the execution status,
   * score, run duration, memory usage, and testcase runs.
   *
   * @param submission        The submission.
   * @param status            The execution status of the
   *                          submission.
   * @param score             The total score the submission
   *                          should receive.
   * @param runDurationMillis The total run duration of the
   *                          submission, in milliseconds.
   * @param memoryUsageBytes  The maximum amount of memory
   *                          used among its testcase runs, in
   *                          bytes.
   * @param testcaseRuns      The testcase runs of the
   *                          submission.
   */
  public SubmissionResult(
    Submission submission,
    ExecutionStatus status,
    int score,
    int runDurationMillis,
    long memoryUsageBytes,
    List<TestcaseRun> testcaseRuns
  ) {
    this.submission = submission;
    this.status = status;
    this.score = score;
    this.runDurationMillis = runDurationMillis;
    this.memoryUsageBytes = memoryUsageBytes;
    this.testcaseRuns = testcaseRuns;
  }

  /**
   * Constructs a new {@code SubmissionResult} instance with
   * the given submission alongside the execution status,
   * score, run duration, and memory usage.
   *
   * @param submission        The submission.
   * @param status            The execution status of the
   *                          submission.
   * @param score             The total score the submission
   *                          should receive.
   * @param runDurationMillis The total run duration of the
   *                          submission, in milliseconds.
   * @param memoryUsageBytes  The maximum amount of memory
   *                          used among its testcase runs, in
   *                          bytes.
   */
  public SubmissionResult(
    Submission submission,
    ExecutionStatus status,
    int score,
    int runDurationMillis,
    long memoryUsageBytes
  ) {
    this.submission = submission;
    this.status = status;
    this.score = score;
    this.runDurationMillis = runDurationMillis;
    this.memoryUsageBytes = memoryUsageBytes;
    this.testcaseRuns = new ArrayList<TestcaseRun>();
  }

  /**
   * Constructs a new {@code SubmissionResult} instance with
   * the given submission.
   *
   * @param submission The submission.
   */
  public SubmissionResult(Submission submission) {
    this.submission = submission;
    this.status = ExecutionStatus.PENDING;
    this.score = 0;
    this.runDurationMillis = 0;
    this.memoryUsageBytes = 0;
    this.testcaseRuns = new ArrayList<TestcaseRun>();
  }

  /**
   * Adds the given {@code TestcaseRun} to the
   * {@code SubmissionResult}'s testcase runs.
   *
   * @param run The {@code TestcaseRun} to add.
   */
  public void addTestcaseRun(TestcaseRun run) {
    this.testcaseRuns.add(run);
  }

  /**
   * Adds the given run duration, in milliseconds, to the
   * {@code SubmissionResult}'s total run duration.
   *
   * @param durationToAdd The run duration to add, in
   *                      milliseconds.
   */
  public void addRunDurationMillis(int durationToAdd) {
    this.runDurationMillis += durationToAdd;
  }

  /**
   * Adds the given score to the {@code SubmissionResult}'s
   * total score.
   *
   * @param scoreToAdd The score to add.
   */
  public void addScore(int scoreToAdd) {
    this.score += scoreToAdd;
  }

  /**
   * Updates the maximum memory usage of the
   * {@code SubmissionResult}, only when the given usage is
   * higher than the result's current maximum memory usage.
   *
   * @param memoryUsageBytes The memory usage to be compared
   *                         with the current maximum memory
   *                         usage, in bytes.
   */
  public void updateMemoryUsageBytes(long memoryUsageBytes) {
    this.memoryUsageBytes = Math.max(this.memoryUsageBytes, memoryUsageBytes);
  }

  /**
   * Updates the {@code ExecutionStatus} of the
   * {@code SubmissionResult}, only when the given status has
   * a higher priority than the current status.
   *
   * @param incomingStatus The status to be compared with the
   *                       current status.
   */
  public void updateStatus(ExecutionStatus incomingStatus) {
    if (incomingStatus.compareTo(this.status) < 0) {
      this.status = incomingStatus;
    }
  }

  /**
   * Gets the original submission.
   *
   * @return The submission.
   */
  public Submission getSubmission() {
    return this.submission;
  }

  /**
   * Retrieves the current status of this submission.
   *
   * @return The current status of this submission.
   */
  public ExecutionStatus getStatus() {
    return this.status;
  }

  /**
   * Sets the current status of this submission.
   *
   * @param status The current status of this submission.
   */
  public void setStatus(ExecutionStatus status) {
    this.status = status;
  }

  /**
   * Retrieves the score of this submission.
   *
   * @return The score of this submission.
   */
  public int getScore() {
    return this.score;
  }

  /**
   * Retrieves the total runtime of this submission, in ms.
   *
   * @return The total runtime of this submission, in ms.
   */
  public int getRunDurationMillis() {
    return this.runDurationMillis;
  }


  /**
   * Retrieves the testcase runs.
   *
   * @return The testcase runs.
   */
  public List<TestcaseRun> getTestcaseRuns() {
    return this.testcaseRuns;
  }

  /**
   * Returns the maximum amount of memory used among its
   * testcase runs, in bytes.
   *
   * @return The maximum amount of memory used among its
   *         testcase runs, in bytes.
   */
  public long getMemoryUsageBytes() {
    return this.memoryUsageBytes;
  }
}
