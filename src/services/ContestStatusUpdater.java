package services;

import dal.dao.ContestDao;
import dal.dao.ContestSessionDao;


/**
 * Updates status for contest sessions that are over.
 * Updates contest status as well.
 * <p>
 * Created on 2021.01.22.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class ContestStatusUpdater {
  /** The interval at which this thread runs. */
  private static final int RUN_INTERVAL = 1000*60;
  /** The running background thread. */
  private Thread thread = null;
  /** Whether this cleaner is running or not. */
  private boolean running = false;

  /**
   * Constructs a new session cleaner.
   */
  public ContestStatusUpdater() {
    this.thread = new Thread(new StatusUpdater());
  }

  /**
   * Starts the session cleaner.
   * It may be stopped by calling {@code .stop()}.
   */
  public void start() {
    if (this.running) {
      return;
    }
    this.running = true;
    this.thread.start();
  }

  /**
   * Stops the session cleaner.
   */
  public void stop() {
    this.running = false;
  }

  private class StatusUpdater implements Runnable {
    private ContestSessionDao contestSessionDao;
    private ContestDao contestDao;

    /**
     * Creates a new {@code StatusUpdater} {@code Runnable} object.
     */
    public StatusUpdater() {
      this.contestSessionDao = new ContestSessionDao();
      this.contestDao = new ContestDao();
    }

    @Override
    public void run() {
      while (ContestStatusUpdater.this.running) {
        this.contestSessionDao.updateStatus();
        this.contestDao.updateStatus();
        try {
          Thread.sleep(ContestStatusUpdater.RUN_INTERVAL);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }

  }
}
