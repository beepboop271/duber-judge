package templater.compiler.parser;

import templater.language.Token;
import templater.language.TokenKind;

class TokenMatcher extends TokenMatchable<Token> {
  private final TokenKind kind;
  private final char punctuationChar;

  TokenMatcher(char c) {
    this.kind = TokenKind.PUNCTUATION;
    this.punctuationChar = c;
  }

  TokenMatcher(TokenKind kind) {
    this.kind = kind;
    this.punctuationChar = 0;
  }

  @Override
  protected Token tryMatchInternal(TokenQueue.Iterator input) {
    Token t = input.next();
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
