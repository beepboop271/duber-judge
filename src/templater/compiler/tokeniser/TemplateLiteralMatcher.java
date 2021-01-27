package templater.compiler.tokeniser;

import templater.language.TokenKind;

/**
 * Matches an
 * {@code templater.language.TokenKind.TEMPLATE_LITERAL}
 * using an EscapedDelimitedMatcher. The delimiters used are
 * ${ and }.
 */
class TemplateLiteralMatcher extends EscapedDelimitedMatcher {
  /**
   * Creates a new TemplateLiteralMatcher.
   */
  TemplateLiteralMatcher() {
    super("${", '}', TokenKind.TEMPLATE_LITERAL);
  }
}
