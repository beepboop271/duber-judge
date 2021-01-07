package entities;

import java.io.Serializable;

/**
 * [description]
 * <p>
 * Created on 2021.01.07.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class SessionInfo implements Serializable {
  /**
   *
   */
  private static final long serialVersionUID = 1L;
  private long userId;

  public SessionInfo(long userId) {
    this.userId = userId;
  }

  public long getUserId() {
    return this.userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }

}
