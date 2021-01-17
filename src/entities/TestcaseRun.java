package entities;

public class TestcaseRun {
  private Testcase testcase;
  private Submission submission;
  private int runDurationMillis;
  private double memoryUsageKb;
  private ExecutionStatus status;
  private String output;

  public TestcaseRun(Testcase testcase) {
    this.testcase = testcase;
    this.submission = null;
    this.runDurationMillis = 0;
    this.memoryUsageKb = 0;
    this.status = ExecutionStatus.PENDING;
    this.output = "";
  }

  public TestcaseRun(
    Testcase testcase,
    Submission submission,
    int runDurationMillis,
    double memoryUsageKb,
    ExecutionStatus status,
    String output
  ) {
    this.testcase = testcase;
    this.submission = submission;
    this.runDurationMillis = runDurationMillis;
    this.memoryUsageKb = memoryUsageKb;
    this.status = status;
    this.output = output;
  }

  public Testcase getTestcase() {
    return this.testcase;
  }
  
  public Submission getSubmission() {
    return this.submission;
  }

  public void setSubmission(Submission submission) {
    this.submission = submission;
  }

  public int getRunDurationMillis() {
    return this.runDurationMillis;
  }

  public void setRunDurationMillis(int runDurationMillis) {
    this.runDurationMillis = runDurationMillis;
  }

  public double getMemoryUsageKb() {
    return this.memoryUsageKb;
  }

  public void setMemoryUsageKb(double memoryUsageKb) {
    this.memoryUsageKb = memoryUsageKb;
  }

  public ExecutionStatus getStatus() {
    return this.status;
  }

  public void setStatus(ExecutionStatus status) {
    this.status = status;
  }

  public String getOutput() {
    return this.output;
  }
  
  public void setOutput(String output) {
    this.output = output;
  }
  
}
