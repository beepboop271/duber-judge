package entities;

public class TestcaseRun {
  private Testcase testcase;
  private long runDurationMillis;
  private double memoryUsage;
  private ExecutionStatus status;
  private String output;

  public TestcaseRun(Testcase testcase) {
    this.testcase = testcase;
    this.runDurationMillis = 0;
    this.memoryUsage = 0;
    this.status = ExecutionStatus.PENDING;
    this.output = "";
  }

  public TestcaseRun(
    Testcase testcase,
    long runDurationMillis,
    double memoryUsage,
    ExecutionStatus status,
    String output
  ) {
    this.testcase = testcase;
    this.runDurationMillis = runDurationMillis;
    this.memoryUsage = memoryUsage;
    this.status = status;
    this.output = output;
  }

  public Testcase getTestcase() {
    return this.testcase;
  }
  
  public long getRunDurationMillis() {
    return this.runDurationMillis;
  }

  public void setRunDurationMillis(long runDurationMillis) {
    this.runDurationMillis = runDurationMillis;
  }

  public double getMemoryUsage() {
    return this.memoryUsage;
  }

  public void setMemoryUsage(double memoryUsage) {
    this.memoryUsage = memoryUsage;
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
