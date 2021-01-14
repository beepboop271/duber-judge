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

  public Session(
    String token,
    SessionInfo sessionInfo,
    Timestamp lastActive
  ) {
    this.token = token;
    this.sessionInfo = sessionInfo;
    this.lastActive = lastActive;
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