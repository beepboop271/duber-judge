package templater.language;

/**
 * A class representing an item with string content, whether
 * that be a literal string or a variable with a string
 * value.
 *
 * @author Kevin Qiao
 * @version 1.0
 */
public class StringResolvable {
  private final String content;
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
   * Retrieves this {@code StringResolvable}'s content.
   *
   * @return String, the content.
   */
  public String getContent() {
    return this.content;
  }

  /**
   * Retrieves whether this {@code StringResolvable} is
   * templated.
   *
   * @return boolean, whether it's templated.
   */
  public boolean isTemplate() {
    return this.isTemplate;
  }
}
