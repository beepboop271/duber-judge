import dal.connection.GlobalConnectionPool;
import judge.ChildProcesses;

/**
 * A {@code Runnable} object that closes resources when the
 * program is shutting down.
 * <p>
 * Created on 2021.01.25.
 *
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class ResourceCleaner implements Runnable {
  /**
   * Creates a new {@code ResourceCleaner} instance.
   */
  public ResourceCleaner() {
  }

  /**
   * Shuts down global resources.
   */
  @Override
  public void run() {
    ChildProcesses.shutdown();
    // GlobalConnectionPool.pool.close();
  }
}
