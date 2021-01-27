package webserver;

/**
 * An exception that is thrown to indicate that a cookie name or value is
 * illegal, malformed, or invalid.
 * <p>
 * Created <b> 2021-01-19</b>.
 *
 * @since 0.0.6
 * @version 1.0.0
 * @author Joseph Wang
 */
@SuppressWarnings("serial")
public class InvalidCookieException extends Exception {
  /**
   * Constructs a new InvalidCookieException with {@code null}
   * as its default message and no cause.
   */
  public InvalidCookieException() {
    super();
  }

  /**
   * Constructs a new InvalidCookieException with a specified
   * message and no cause.
   *
   * @param message The detail message for this exception.
   */
  public InvalidCookieException(String message) {
    super(message);
  }

  /**
   * Constructs a new InvalidCookieException with a specified
   * message and cause.
   *
   * @param message The detail message for this exception.
   * @param cause   The cause of this exception.
   */
  public InvalidCookieException(String message, Throwable cause) {
    super(message, cause);
  }
}
