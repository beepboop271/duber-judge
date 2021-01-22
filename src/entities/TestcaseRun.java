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
  private long runDurationMillis;
  /** The total amount of memory used, in kB. */
  private long memoryUsageB;
  /** The current status of this run. */
  private ExecutionStatus status;
  /** The output generated from the submission for this run. */
  private String output;

  /**
   * Constructs a new TestcaseRun.
   *
   * @param submissionId      the id of the associated submission.
   * @param batchId           the id of the associated batch.
   * @param runDurationMillis the total duration of this run, in ms.
   * @param memoryUsageB     the total amount of memory used, in kB.
   * @param status            the current status of this run.
   * @param output            the output generated from the submission for this run.
   */
  public TestcaseRun(
    long submissionId,
    long batchId,
    long runDurationMillis,
    long memoryUsageB,
    ExecutionStatus status,
    String output
  ) {
    this.submissionId = submissionId;
    this.batchId = batchId;
    this.runDurationMillis = runDurationMillis;
    this.memoryUsageB = memoryUsageB;
    this.status = status;
    this.output = output;
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
  public long getRunDurationMillis() {
    return this.runDurationMillis;
  }

  /**
   * Retrieves the total amount of memory used, in kB.
   *
   * @return the total amount of memory used, in kB.
   */
  public long getMemoryUsageB() {
    return this.memoryUsageB;
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
   * Retrieves the generated output for this run.
   *
   * @return the generated output for this run.
   */
  public String getOutput() {
    return this.output;
  }


}
