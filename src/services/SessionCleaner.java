package services;

import java.sql.Timestamp;

import dal.dao.SessionDao;

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
    private SessionDao sessionDao = new SessionDao();
    private static final long SESSION_DURATION = 1000*60*30;

    private void clearSessions() {
      this.sessionDao.deleteSessionFromBefore(new Timestamp(
        System.currentTimeMillis()-SessionHandler.SESSION_DURATION
      ));
    }

    @Override
    public void run() {
      while (running) {
        this.clearSessions();
        try {
          Thread.sleep(1000*30);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }

  }
}
