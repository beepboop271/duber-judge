package judge;

/**
 * An {@code Exception} thrown when the judge encounters an error.
 * <p>
 * Created on 2021.01.08.
 *
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */
public class InternalErrorException extends Exception {
  /** The serial version ID used for serialization. */
  private static final long serialVersionUID = 1L;

  /**
   * Creates a new {@code InternalErrorException} with a {@code Throwable} object
   * as the cause.
   *
   * @param cause The cause of the exception.
   */
  public InternalErrorException(Throwable cause) {
    super(cause);
  }

  /**
   * Creates a new {@code InternalErrorException} with an error message.
   *
   * @param message The error message.
   */
  public InternalErrorException(String message) {
    super(message);
  }
}
