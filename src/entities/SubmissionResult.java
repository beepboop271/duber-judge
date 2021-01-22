package entities;

import java.util.ArrayList;

/**
 * An entity representing a submission result.
 * <p>
 * Created on 2021.01.21.
 *
 * @author Shari Sun
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
  /** The total amount of time the submission took to run, in ms. */
  private long runDurationMillis;
  /** The testcase runs. */
  private ArrayList<TestcaseRun> testcaseRuns;


  /**
   * Constructs a new submission result.
   *
   * @param submission               The submission.
   * @param status                   The execution status.
   * @param score                    The score.
   * @param runDurationMillis        The run duration in milliseconds.
   * @param testcaseRuns             The testcase runs.
   */
  public SubmissionResult(
    Submission submission,
    ExecutionStatus status,
    int score,
    long runDurationMillis,
    ArrayList<TestcaseRun> testcaseRuns
  ) {
    this.submission = submission;
    this.status = status;
    this.score = score;
    this.runDurationMillis = runDurationMillis;
    this.testcaseRuns = testcaseRuns;
  }

  /**
   * Gets the original submission.
   *
   * @return        The submission.
   */
  public Submission getSubmission() {
    return this.submission;
  }

  /**
   * Retrieves the current status of this submission.
   *
   * @return       The current status of this submission.
   */
  public ExecutionStatus getStatus() {
    return this.status;
  }

  /**
   * Retrieves the score of this submission.
   *
   * @return       The score of this submission.
   */
  public int getScore() {
    return this.score;
  }

  /**
   * Retrieves the total runtime of this submission, in ms.
   *
   * @return    The total runtime of this submission, in ms.
   */
  public long getRunDurationMillis() {
    return this.runDurationMillis;
  }


  /**
   * Retrieves the testcase runs.
   *
   * @return     The testcase runs.
   */
  public ArrayList<TestcaseRun> getTestcaseRuns() {
    return this.testcaseRuns;
  }
}
