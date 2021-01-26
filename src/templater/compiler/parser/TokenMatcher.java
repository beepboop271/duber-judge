package templater.compiler.parser;

import java.util.NoSuchElementException;

import templater.language.Token;
import templater.language.TokenKind;

/**
 * Matches a single Token in the TokenQueue.
 */
class TokenMatcher extends TokenMatchable<Token> {
  /** The kind of Token to match. */
  private final TokenKind kind;
  /**
   * The punctuation character to match, if kind is
   * TokenKind.PUNCTUATION.
   */
  private final char punctuationChar;

  /**
   * Creates a TokenMatcher to match a punctuation token with
   * the given punctuation character.
   *
   * @param c The punctuation character to match.
   */
  TokenMatcher(char c) {
    this.kind = TokenKind.PUNCTUATION;
    this.punctuationChar = c;
  }

  /**
   * Creates a TokenMatcher to match a token with the given
   * kind.
   *
   * @param kind The kind of token to match.
   */
  TokenMatcher(TokenKind kind) {
    this.kind = kind;
    this.punctuationChar = 0;
  }

  @Override
  protected Token tryMatchInternal(TokenQueue.Iterator input) {
    Token t;
    try {
      t = input.next();
    } catch (NoSuchElementException e) {
      return null;
    }

    if (
      (t.getKind() == this.kind)
        && ((this.kind != TokenKind.PUNCTUATION)
          || (t.getContent().charAt(0) == this.punctuationChar))
    ) {
      return t;
    }
    return null;
  }
}
