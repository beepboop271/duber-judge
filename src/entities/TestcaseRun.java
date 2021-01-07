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
  private double runDuration;
  private double memoryUsage;
  private ExecutionStatus status;
  private String output;


  public TestcaseRun(
    long submissionId,
    long batchId,
    double runDuration,
    double memoryUsage,
    ExecutionStatus status,
    String output
  ) {
    this.submissionId = submissionId;
    this.batchId = batchId;
    this.runDuration = runDuration;
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

  public double getRunDuration() {
    return this.runDuration;
  }

  public double getMemoryUsage() {
    return this.memoryUsage;
  }

  public ExecutionStatus getStatus() {
    return this.status;
  }

  public String getOutput() {
    return this.output;
  }


}
