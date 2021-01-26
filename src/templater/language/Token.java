package templater.language;

import templater.compiler.TextFilePosition;

/**
 * A single token of the templating language. A program
 * consists of a linear sequence of tokens, which are parsed
 * into a tree of
 * {@code Element}s/{@code Node}s/{@code LanguageElement}s.
 */
public class Token extends LanguageElement {
  /** The characters that compose this Token. */
  private final String content;
  /** The type of Token this is. */
  private final TokenKind kind;
  /**
   * The position within the source code this Token is located
   * at.
   */
  private final TextFilePosition position;

  /**
   * Constructs a new Token of the specified kind with the
   * given content and position.
   *
   * @param content  The characters that compose this Token.
   * @param kind     The type of Token this is.
   * @param position The position within the source code this
   *                 Token is located at.
   */
  public Token(String content, TokenKind kind, TextFilePosition position) {
    this.content = content;
    this.kind = kind;
    this.position = position.clone();
  }

  @Override
  public String toString() {
    return super.toString()
      +"[kind:"+this.kind
      +",position:"+this.position.toDisplayString()
      +",content:\""+this.content+"\"]";
  }

  /**
   * Gets the characters that compose this Token.
   *
   * @return The characters that compose this Token.
   */
  public String getContent() {
    return this.content;
  }

  /**
   * Gets the type of this Token.
   *
   * @return The type of this Token.
   */
  public TokenKind getKind() {
    return this.kind;
  }

  /**
   * Gets the position within the source code this Token is
   * located at.
   *
   * @return The position within the source code this Token is
   *         located at.
   */
  public TextFilePosition getPosition() {
    return this.position.clone();
  }
}
