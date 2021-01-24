package entities;

/**
 * An entity designed to represent one specific run of a test case
 * with a user submission.
 * <p>
 * Created on 2021.01.07.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class TestcaseRun {
  /** The id of the associated submission. */
  private long submissionId;
  /** The id of the associated batch. */
  private long batchId;
  /** The total duration of this run, in ms. */
  private int runDurationMillis;
  /** The total amount of memory used, in bytes. */
  private long memoryUsageBytes;
  /** The current status of this run. */
  private ExecutionStatus status;
  /** The output generated from the submission for this run. */
  private String output;

  /**
   * Constructs a new {@code TestcaseRun}.
   *
   * @param submissionId      the id of the associated
   *                          submission.
   * @param batchId           the id of the associated batch.
   * @param runDurationMillis the total duration of this run,
   *                          in ms.
   * @param memoryUsageBytes  the total amount of memory used,
   *                          in bytes.
   * @param status            the current status of this run.
   * @param output            the output generated from the
   *                          submission for this run.
   */
  public TestcaseRun(
    long submissionId,
    long batchId,
    int runDurationMillis,
    long memoryUsageBytes,
    ExecutionStatus status,
    String output
  ) {
    this.submissionId = submissionId;
    this.batchId = batchId;
    this.runDurationMillis = runDurationMillis;
    this.memoryUsageBytes = memoryUsageBytes;
    this.status = status;
    this.output = output;
  }

  /**
   * Constructs a new {@code TestcaseRun}.
   *
   * @param submissionId      the id of the associated submission.
   * @param batchId           the id of the associated batch.
   */
  public TestcaseRun(
    long submissionId,
    long batchId
  ) {
    this.submissionId = submissionId;
    this.batchId = batchId;
    this.runDurationMillis = 0;
    this.memoryUsageBytes = 0;
    this.status = ExecutionStatus.PENDING;
    this.output = "";
  }

  /**
   * Retrieves this associated submission's id.
   *
   * @return this associated submission's id.
   */
  public long getSubmissionId() {
    return this.submissionId;
  }

  /**
   * Retrieves this associated batch's id.
   *
   * @return this associated batch's id.
   */
  public long getBatchId() {
    return this.batchId;
  }

  /**
   * Retrieves the total amount of time this run took, in ms.
   *
   * @return the total amount of time this run took, in ms.
   */
  public int getRunDurationMillis() {
    return this.runDurationMillis;
  }

  /**
   * Sets the total amount of time this run took, in ms.
   *
   * @param duration The total amount of time this run took,
   *                 in ms.
   */
  public void setRunDurationMillis(int duration) {
    this.runDurationMillis = duration;
  }

  /**
   * Retrieves the total amount of memory used, in bytes.
   *
   * @return the total amount of memory used, in bytes.
   */
  public long getMemoryUsageBytes() {
    return this.memoryUsageBytes;
  }

  /**
   * Sets the total amount of memory used, in bytes.
   *
   * @param memoryUsageBytes The total amount of memory used, in
   *                     bytes.
   */
  public void setMemoryUsageBytes(long memoryUsageBytes) {
    this.memoryUsageBytes = memoryUsageBytes;
  }

  /**
   * Retrieves the current status of the run.
   *
   * @return the current status of the run.
   */
  public ExecutionStatus getStatus() {
    return this.status;
  }

  /**
   * Sets the current status of this testcase run.
   *
   * @param status The current status of this submission.
   */
  public void setStatus(ExecutionStatus status) {
    this.status = status;
  }

  /**
   * Retrieves the outputted data for this run.
   *
   * @return the outputted data for this run.
   */
  public String getOutput() {
    return this.output;
  }

  /**
   * Sets the outputted data for this run.
   *
   * @param output the outputted data for this run.
   */
  public void setOutput(String output) {
    this.output = output;
  }
}
