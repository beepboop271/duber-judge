package templater.compiler.tokeniser;

class TemplateLiteral extends EscapedDelimitedMatcher {
  TemplateLiteral() {
    super("${", '}', TokenKind.TEMPLATE_LITERAL);
  }
}
