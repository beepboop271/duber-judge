package entities;

import java.sql.Timestamp;

/**
 * An class designed to contain and represent the information of a session.
 * <p>
 * Created on 2021.01.07.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class Session implements Comparable<Session> {
  /** The unique token for this session. */
  private String token;
  /** The associated user's id. */
  private long userId;
  /** The user's last active time. */
  private Timestamp lastActive;

  /**
   * Constructs a new {@code Session} with a logged in user.
   *
   * @param userId     the associated user's id.
   * @param token      the token.
   */
  public Session(long userId, String token) {
    this.userId = userId;
    this.token = token;
    this.lastActive = new Timestamp(System.currentTimeMillis());
  }

  /**
   * Constructs a new {@code Session} with a non-logged in user.
   *
   * @param token     the token.
   */
  public Session(String token) {
    this.userId = -1;
    this.token = token;
    this.lastActive = new Timestamp(System.currentTimeMillis());
  }


  /**
   * Gets the token of the session.
   *
   * @return      the token.
   */
  public String getToken() {
    return this.token;
  }

  /**
   * Gets the client's last active time.
   *
   * @return    the last active time.
   */
  public Timestamp getLastActive() {
    return this.lastActive;
  }

  /**
   * Updates the client's last active time.
   *
   * @param lastActive        the last active time.
   */
  public void updateLastActive(Timestamp lastActive) {
    this.lastActive = lastActive;
  }

  /**
   * Retrieves the associated user's id.
   *
   * @return the associated user's id.
   */
  public long getUserId() {
    return this.userId;
  }

  /**
   * Checks if this session contains a logged in user or not.
   *
   * @return      whether the client is a logged in user or not.
   */
  public boolean isLoggedIn() {
    return this.userId != -1;
  }

  /**
   * Sets the associated id to a specified id.
   *
   * @param userId a new user's id.
   */
  public void setUserId(long userId) {
    this.userId = userId;
  }

  @Override
  public int compareTo(Session other) {
    return this.lastActive.compareTo(other.getLastActive());
  }

}
