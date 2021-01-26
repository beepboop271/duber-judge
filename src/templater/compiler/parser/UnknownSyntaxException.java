package templater.compiler.parser;

/**
 * An exception that is thrown when a matcher has
 * successfully started matching a LanguageElement but then
 * fails on a later Token check. Due to the layout of the
 * language, this must mean the syntax (arrangement of
 * Tokens) is incorrect, because once a LanguageElement is
 * partially matched, it must be fully matched.
 */
public class UnknownSyntaxException extends RuntimeException {
  private static final long serialVersionUID = 0L;

  /**
   * Creates a new UnknownSyntaxException with the given
   * message.
   *
   * @param message The message.
   */
  public UnknownSyntaxException(String message) {
    super(message);
  }
}
