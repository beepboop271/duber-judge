package judge;

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
  private final int timeLimitMillis;
  private final int memoryLimitKb;
  
  private int runDurationMillis;
  private long memoryUsedBytes;
  
  public ChildProcess(
    int pid,
    Process process,
    int timeLimitMillis,
    int memoryLimitKb,
    int runDurationMillis,
    long memoryUsedBytes
  ) {
    this.pid = pid;
    this.process = process;
    this.timeLimitMillis = timeLimitMillis;
    this.memoryLimitKb = memoryLimitKb;
    this.runDurationMillis = runDurationMillis;
    this.memoryUsedBytes = memoryUsedBytes;
  }

  public void updateMemoryUsedBytes(long memoryUsedBytes) {
    this.memoryUsedBytes = Math.max(this.memoryUsedBytes, memoryUsedBytes);
  }
  
  public int getPid() {
    return this.pid;
  }

  public Process getProcess() {
    return this.process;
  }

  public int getTimeLimitMillis() {
    return this.timeLimitMillis;
  }

  public int getMemoryLimitKb() {
    return this.memoryLimitKb;
  }

  public int getRunDurationMillis() {
    return this.runDurationMillis;
  }

  public void setRunDurationMillis(int runDurationMillis) {
    this.runDurationMillis = runDurationMillis;
  }

  public long getMemoryUsedBytes() {
    return this.memoryUsedBytes;
  }
}
