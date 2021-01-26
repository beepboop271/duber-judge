package templater.compiler.tokeniser;

import templater.language.TokenKind;

/**
 * Matches an
 * {@code templater.language.TokenKind.STRING_LITERAL} using
 * an EscapedDelimitedMatcher. The delimiters used are
 * double quotes.
 */
class StringLiteralMatcher extends EscapedDelimitedMatcher {
  /**
   * Creates a new StringLiteralMatcher.
   */
  StringLiteralMatcher() {
    super("\"", '"', TokenKind.STRING_LITERAL);
  }
}
