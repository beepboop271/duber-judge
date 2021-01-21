package templater.compiler.tokeniser;

class StringLiteral extends EscapedDelimitedMatcher {
  StringLiteral() {
    super("\"", '"', TokenKind.STRING_LITERAL);
  }
}
