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
   * The punctuation character or keyword string to match, if
   * kind is TokenKind.PUNCTUATION or KEYWORD.
   */
  private final String content;

  /**
   * Creates a TokenMatcher to match a punctuation token with
   * the given punctuation character.
   *
   * @param c The punctuation character to match.
   */
  TokenMatcher(char c) {
    this.kind = TokenKind.PUNCTUATION;
    this.content = ""+c;
  }

  /**
   * Creates a TokenMatcher to match a keyword token with the
   * given keyword.
   *
   * @param s The keyword character to match.
   */
  TokenMatcher(String s) {
    this.kind = TokenKind.KEYWORD;
    this.content = s;
  }

  /**
   * Creates a TokenMatcher to match a token with the given
   * kind.
   *
   * @param kind The kind of token to match.
   */
  TokenMatcher(TokenKind kind) {
    this.kind = kind;
    this.content = "";
  }

  @Override
  protected Token tryMatchInternal(TokenQueue.Iterator input) {
    Token t;
    try {
      t = input.next();
    } catch (NoSuchElementException e) {
      return null;
    }

    if (t.getKind() == this.kind) {
      if ((this.kind == TokenKind.PUNCTUATION) || (this.kind == TokenKind.KEYWORD)) {
        if (this.content.equals(t.getContent())) {
          return t;
        }
      } else {
        return t;
      }
    }
    return null;
  }
}
