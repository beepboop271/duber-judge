package webserver;

/**
 * An exception that is thrown to indicate that a header is
 * illegal, malformed, or invalid.
 * <p>
 * Created <b> 2021-01-19</b>.
 *
 * @since 0.0.4
 * @version1.0.0
 * @author Joseph Wang
 */
@SuppressWarnings("serial")
public class InvalidHeaderException extends Exception {
  /**
   * Constructs a new InvalidHeaderException with {@code null}
   * as its default message and no cause.
   */
  public InvalidHeaderException() {
    super();
  }

  /**
   * Constructs a new InvalidHeaderException with a specified
   * message and no cause.
   *
   * @param message The detail message for this exception.
   */
  public InvalidHeaderException(String message) {
    super(message);
  }

  /**
   * Constructs a new InvalidHeaderException with a specified
   * message and cause.
   *
   * @param message The detail message for this exception.
   * @param cause   The cause of this exception.
   */
  public InvalidHeaderException(String message, Throwable cause) {
    super(message, cause);
  }
}
