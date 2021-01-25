package judge;

/**
 * Wraps a {@code Process} with its process id, time/memory
 * limit, and the maximum amount of memory it has used.
 * <p>
 * Created on 2021.01.16.
 *
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class ChildProcess {
  /** The process id of the {@code Process}. */
  private final int pid;
  /**
   * The running child {@code Process} to wrap and store run
   * information for.
   */
  private final Process process;
  /**
   * The maximum duration the process is allowed to run for,
   * in milliseconds.
   */
  private final int timeLimitMillis;
  /**
   * The maximum amount of memory the process is allowed to
   * use, in kilobytes.
   */
  private final int memoryLimitKb;
  /**
   * The maximum amount of memory the process has used, in
   * bytes.
   */
  private long memoryUsageBytes;

  /**
   * Creates a new {@code ChildProcess} instance with a
   * {@code Process} object, its process id, time/memory
   * limit, and the maximum amount of memory it has used.
   *
   * @param pid              The process id of the
   *                         {@code Process}.
   * @param process          The running child {@code Process}
   *                         to wrap and store run information
   *                         for.
   * @param timeLimitMillis  The maximum duration the process
   *                         is allowed to run for, in
   *                         milliseconds.
   * @param memoryLimitKb    The maximum amount of memory the
   *                         process is allowed to use, in
   *                         kilobytes.
   * @param memoryUsageBytes The maximum amount of memory the
   *                         process has used, in bytes.
   */
  public ChildProcess(
    int pid,
    Process process,
    int timeLimitMillis,
    int memoryLimitKb,
    long memoryUsageBytes
  ) {
    this.pid = pid;
    this.process = process;
    this.timeLimitMillis = timeLimitMillis;
    this.memoryLimitKb = memoryLimitKb;
    this.memoryUsageBytes = memoryUsageBytes;
  }

  /**
   * Updates the maximum memory usage of the
   * {@code ChildProcess}, only when the given usage is
   * higher than the result's current maximum memory usage.
   *
   * @param memoryUsageBytes The memory usage to be compared
   *                         with the current maximum memory
   *                         usage, in bytes.
   */
  public void updateMemoryUsedBytes(long memoryUsageBytes) {
    this.memoryUsageBytes = Math.max(this.memoryUsageBytes, memoryUsageBytes);
  }

  /**
   * Returns the process id of the running child {@code Process}.
   *
   * @return The process id of the running child {@code Process}.
   */
  public int getPid() {
    return this.pid;
  }

  /**
   * Returns the running child {@code Process}.
   *
   * @return The running child {@code Process} to wrap and
   *         store run information for.
   */
  public Process getProcess() {
    return this.process;
  }

  /**
   * Returns the maximum duration the process is allowed to
   * run for, in milliseconds.
   *
   * @return The maximum duration the process is allowed to
   *         run for, in milliseconds.
   */
  public int getTimeLimitMillis() {
    return this.timeLimitMillis;
  }

  /**
   * Returns the maximum amount of memory the
   *                         process is allowed to use, in
   *                         kilobytes.
   *
   * @return The maximum amount of memory the
   *                         process is allowed to use, in
   *                         kilobytes.
   */
  public int getMemoryLimitKb() {
    return this.memoryLimitKb;
  }

  /**
   * Returns the maximum amount of memory the process has
   * used, in bytes.
   *
   * @return The maximum amount of memory the process has
   *         used, in bytes.
   */
  public long getMemoryUsageBytes() {
    return this.memoryUsageBytes;
  }
}
