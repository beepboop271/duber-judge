package entities;

import java.io.Serializable;

/**
 * An class designed to contain and represent the information of a session.
 * <p>
 * Created on 2021.01.07.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class SessionInfo implements Serializable {
  private static final long serialVersionUID = 1L;
  /** The associated user's id. */
  private long userId;

  /**
   * Constructs a new SessionInfo.
   * 
   * @param userId the associated user's id.
   */
  public SessionInfo(long userId) {
    this.userId = userId;
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
   * Sets the associated id to a specified id.
   * 
   * @param userId a new user's id.
   */
  public void setUserId(long userId) {
    this.userId = userId;
  }

}
