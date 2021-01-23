package judge;

/**
 * [description]
 * <p>
 * Created on 2021.01.08.
 *
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public abstract class UserException extends Exception {
  private static final long serialVersionUID = 1L;
  
  public UserException() {
  }

  public UserException(String message) {
    super(message);
  }

  public UserException(Throwable cause) {
    super(cause);
  }

  public UserException(String message, Throwable cause) {
    super(message, cause);
  }
}
