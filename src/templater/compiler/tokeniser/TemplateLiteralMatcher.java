package templater.compiler.tokeniser;

import templater.language.TokenKind;

class TemplateLiteral extends EscapedDelimitedMatcher {
  TemplateLiteral() {
    super("${", '}', TokenKind.TEMPLATE_LITERAL);
  }
}
