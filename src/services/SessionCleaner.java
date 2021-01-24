package services;

import java.sql.Timestamp;


/**
 * Clears out expired sessions.
 * <p>
 * Created on 2021.01.16.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class SessionCleaner {
  /** The duration of a session before it's expired. */
  private static final long SESSION_DURATION = 1000*60*30;
  /** The interval at which this thread runs. */
  private static final int RUN_INTERVAL = 1000*30;
  /** The running background thread. */
  private Thread thread = null;
  /** Whether this cleaner is running or not. */
  private boolean running = false;

  /**
   * Constructs a new session cleaner.
   */
  public SessionCleaner() {
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

  /**
   * An inner {@link Runanble} class used for performing the cleaning service.
   * {@link SessionCleaner} constructs a new thread based on
   * this class.
   */
  private class SessionHandler implements Runnable {
    /** A session service. */
    private SessionService sessionService = new SessionService();

    /**
     * Calls the relevant method in {@link SessionService} that
     * clears expired sessions.
     *
     * @see          SessionCleaner#SESSION_DURATION
     */
    private void clearSessions() {
      this.sessionService.deleteFromBefore(new Timestamp(
        System.currentTimeMillis()-SessionCleaner.SESSION_DURATION
      ));
    }

    /**
     * Clears expired session at a set interval.
     *
     * @see          SessionCleaner#RUN_INTERVAL
     */
    @Override
    public void run() {
      while (SessionCleaner.this.running) {
        this.clearSessions();
        try {
          Thread.sleep(SessionCleaner.RUN_INTERVAL);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }

  }
}
