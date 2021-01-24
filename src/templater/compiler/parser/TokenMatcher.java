package templater.compiler.parser;

import templater.compiler.tokeniser.Token;
import templater.compiler.tokeniser.TokenKind;

class TokenMatcher extends TokenMatchable<Token> {
  private TokenKind kind;
  private char punctuationChar;

  TokenMatcher(char c) {
    this.punctuationChar = c;
    this.kind = TokenKind.PUNCTUATION;
  }

  TokenMatcher(TokenKind kind) {
    this.kind = kind;
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
