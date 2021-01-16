package entities;

import java.sql.Timestamp;

/**
 * An entity designed to represent a session, for authentication.
 * <p>
 * Created on 2021.01.07.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class Session {
  /** The session info for this session. */
  private SessionInfo sessionInfo;
  /** The last active time for this session. */
  private Timestamp lastActive;
  /** The token for this session. */
  private String token;

  /**
   * Constructs a new Session.
   * 
   * @param token       the token for this session.
   * @param sessionInfo the session info for this session.
   * @param lastActive  the last active time for this session.
   */
  public Session(String token, SessionInfo sessionInfo, Timestamp lastActive) {
    this.token = token;
    this.sessionInfo = sessionInfo;
    this.lastActive = lastActive;
  }

  /**
   * Retrieves this session's info.
   * 
   * @return this session's info.
   */
  public SessionInfo getSessionInfo() {
    return this.sessionInfo;
  }

  /**
   * Retrieves this session's last active time.
   * 
   * @return this session's last active time.
   */
  public Timestamp getLastActive() {
    return this.lastActive;
  }

  /**
   * Retrieves this session's token.
   * 
   * @return this session's token.
   */
  public String getToken() {
    return this.token;
  }

  /**
   * Updates this session's last active timestamp to the current system time.
   */
  public void updateLastActive() {
    this.lastActive = new Timestamp(System.currentTimeMillis());
  }

}