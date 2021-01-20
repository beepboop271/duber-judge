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
  private static final long SESSION_DURATION = 1000*60*30;
  private static final int RUN_INTERVAL = 1000*30;
  private Thread thread = null;
  private boolean running = false;

  public void start() {
    if (this.running) {
      return;
    }
    this.running = true;
    this.thread = new Thread(new SessionHandler());
    this.thread.start();
  }

  public void terminate() {
    this.running = false;
  }

  private class SessionHandler implements Runnable {
    private SessionService sessionService = new SessionService();

    private void clearSessions() {
      this.sessionService.deleteFromBefore(new Timestamp(
        System.currentTimeMillis()-SessionCleaner.SESSION_DURATION
      ));
    }

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
