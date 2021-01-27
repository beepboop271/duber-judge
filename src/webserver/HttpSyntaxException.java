package webserver;

/**
 * An exception thrown to indicate that the parameters
 * provided violates expected HTTP syntax, and an
 * HTTP message could not be formed with the provided parameters.
 * <p>
 * Created <b> 2021-01-20 </b>.
 *
 * @since 0.0.4
 * @version 1.0.0
 * @author Joseph Wang
 */
@SuppressWarnings("serial")
public class HttpSyntaxException extends Exception {
  /**
   * Constructs a new HTTPSyntaxException with {@code null}
   * as its default message and no cause.
   */
  public HttpSyntaxException() {
    super();
  }

  /**
   * Constructs a new HttpSyntaxException with a specified
   * message and no cause.
   *
   * @param message The detail message for this exception.
   */
  public HttpSyntaxException(String message) {
    super(message);
  }

  /**
   * Constructs a new HttpSyntaxException with a provided
   * message and cause.
   *
   * @param message The detail message for this exception.
   * @param cause   The cause of this exception.
   */
  public HttpSyntaxException(String message, Throwable cause) {
    super(message, cause);
  }
}
