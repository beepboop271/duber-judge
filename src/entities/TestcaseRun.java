package entities;

public class TestcaseRun {
  private Testcase testcase;
  private long runDurationMills;
  private double memoryUsage;
  private ExecutionStatus status;
  private String output;

  public TestcaseRun(Testcase testcase) {
    this.testcase = testcase;
    this.runDurationMills = 0;
    this.memoryUsage = 0;
    this.status = ExecutionStatus.PENDING;
    this.output = "";
  }

  public TestcaseRun(
    Testcase testcase,
    long runDurationMills,
    double memoryUsage,
    ExecutionStatus status,
    String output
  ) {
    this.testcase = testcase;
    this.runDurationMills = runDurationMills;
    this.memoryUsage = memoryUsage;
    this.status = status;
    this.output = output;
  }

  public Testcase getTestcase() {
    return this.testcase;
  }
  
  public long getRunDurationMills() {
    return this.runDurationMills;
  }

  public void setRunDurationMills(long runDurationMills) {
    this.runDurationMills = runDurationMills;
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
