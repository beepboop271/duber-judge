package entities;

import java.sql.Timestamp;

/**
 * [description]
 * <p>
 * Created on 2021.01.07.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class Session {
  private SessionInfo sessionInfo;
  private Timestamp lastActive;
  private String token;

  public Session(Timestamp lastActive, SessionInfo sessionInfo, String token) {
    this.lastActive = lastActive;
    this.sessionInfo = sessionInfo;
    this.token = token;
  }

  public SessionInfo getSessionInfo() {
    return this.sessionInfo;
  }

  public Timestamp getLastActive() {
    return this.lastActive;
  }

  public String getToken() {
    return this.token;
  }

  public void updateLastActive() {
    this.lastActive = new Timestamp(System.currentTimeMillis());
  }

}