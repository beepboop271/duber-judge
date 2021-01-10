package entities;

/**
 * [description]
 * <p>
 * Created on 2021.01.07.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class TestcaseRun {
  private long submissionId;
  private long batchId;
  private long runDurationMillis;
  private long memoryUsage;
  private ExecutionStatus status;
  private String output;


  public TestcaseRun(
    long submissionId,
    long batchId,
    long runDurationMillis,
    long memoryUsage,
    ExecutionStatus status,
    String output
  ) {
    this.submissionId = submissionId;
    this.batchId = batchId;
    this.runDurationMillis = runDurationMillis;
    this.memoryUsage = memoryUsage;
    this.status = status;
    this.output = output;
  }

  public long getSubmissionId() {
    return this.submissionId;
  }

  public long getBatchId() {
    return this.batchId;
  }

  public long getRunDurationMillis() {
    return this.runDurationMillis;
  }

  public long getMemoryUsage() {
    return this.memoryUsage;
  }

  public ExecutionStatus getStatus() {
    return this.status;
  }

  public String getOutput() {
    return this.output;
  }


}
