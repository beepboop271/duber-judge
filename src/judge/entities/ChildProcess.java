package judge.entities;

/**
 * [description]
 * <p>
 * Created on 2021.01.16.
 *
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class ChildProcess {
  private final int pid;
  private final Process process;
  private final int memoryLimitKb;
  
  private double memoryUsedKb;

  public ChildProcess(
    int pid,
    Process process,
    int memoryLimitKb,
    double memoryUsedKb
  ) {
    this.pid = pid;
    this.process = process;
    this.memoryLimitKb = memoryLimitKb;
    this.memoryUsedKb = memoryUsedKb;
  }

  public int getPid() {
    return this.pid;
  }

  public Process getProcess() {
    return this.process;
  }

  public int getMemoryLimitKb() {
    return this.memoryLimitKb;
  }

  public double getMemoryUsedKb() {
    return this.memoryUsedKb;
  }

  public void setMemoryUsedKb(double memoryUsedKb) {
    this.memoryUsedKb = memoryUsedKb;
  }

}
