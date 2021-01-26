package templater.compiler.tokeniser;

/**
 * An exception that is thrown when the Tokeniser is in a
 * position in the input that cannot be matched to any
 * Token, meaning the template has some invalid character or
 * String.
 */
public class UnknownTokenException extends Exception {
  private static final long serialVersionUID = 0L;

  /**
   * Creates a new UnknownTokenException with the given
   * message.
   */
  public UnknownTokenException(String message) {
    super(message);
  }
}
