package templater.language;

/**
 * A class representing an item with string content, whether
 * that be a literal string or a variable name to be resolved
 * into a String at interpret time.
 *
 * @author Kevin Qiao
 * @version 1.0
 */
public class StringResolvable {
  /**
   * The value of this StringResolvable. Either the literal
   * String this resolvable respresents (if isTemplate is
   * false), or the name of a variable which will be resolved
   * to a String (if isTemplate is true).
   */
  private final String content;
  /**
   * Whether or not the content field holds a literal String
   * or the name of a variable to be resolved into a String.
   */
  private final boolean isTemplate;

  /**
   * Creates a new {@code StringResolvable}, given a token.
   *
   * @param token The {@code Token} whose content this
   *              {@code StringResolvable} shares.
   */
  public StringResolvable(Token token) {
    this.content = token.getContent();
    this.isTemplate = (token.getKind() == TokenKind.TEMPLATE_LITERAL);
  }

  /**
   * Gets the value of this StringResolvable. Either the
   * literal String this resolvable respresents (if isTemplate
   * is false), or the name of a variable which will be
   * resolved to a String (if isTemplate is true).
   *
   * @return The content.
   */
  public String getContent() {
    return this.content;
  }

  /**
   * Gets whether or not the content field holds a literal
   * String or the name of a variable to be resolved into a
   * String.
   *
   * @return Whether or not this StringResolvable is templated.
   */
  public boolean isTemplate() {
    return this.isTemplate;
  }
}
