package services;

import java.sql.Timestamp;

import dal.dao.ContestSessionDao;


/**
 * Updates status for contest sessions that are over.
 * <p>
 * Created on 2021.01.22.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class ContestSessionCleaner {
  /** The interval at which this thread runs. */
  private static final int RUN_INTERVAL = 1000*60;
  /** The running background thread. */
  private Thread thread = null;
  /** Whether this cleaner is running or not. */
  private boolean running = false;

  /**
   * Constructs a new session cleaner.
   */
  public ContestSessionCleaner() {
    this.thread = new Thread(new SessionHandler());
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

  private class SessionHandler implements Runnable {
    private ContestSessionDao contestSessionDao;

    public SessionHandler() {
      this.contestSessionDao = new ContestSessionDao();
    }


    private void updateSessions() {
      this.contestSessionDao.updateStatus();
    }

    @Override
    public void run() {
      while (ContestSessionCleaner.this.running) {
        this.updateSessions();
        try {
          Thread.sleep(ContestSessionCleaner.RUN_INTERVAL);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }

  }
}
