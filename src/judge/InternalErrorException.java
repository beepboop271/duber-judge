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

public class InternalErrorException extends Exception {
  private static final long serialVersionUID = 1L;
  
  public InternalErrorException(Throwable cause) {
    super(cause);
  }

  public InternalErrorException(String message) {
    super(message);
  }
}
