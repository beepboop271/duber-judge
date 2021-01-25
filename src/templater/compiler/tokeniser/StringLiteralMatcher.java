package templater.compiler.tokeniser;

import templater.language.TokenKind;

class StringLiteral extends EscapedDelimitedMatcher {
  StringLiteral() {
    super("\"", '"', TokenKind.STRING_LITERAL);
  }
}
