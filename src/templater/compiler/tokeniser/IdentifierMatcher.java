package templater.compiler.tokeniser;

import java.util.regex.Pattern;

import templater.language.TokenKind;

/**
 * Matches an
 * {@code templater.language.TokenKind.IDENTIFIER} using
 * regex.
 */
class IdentifierMatcher extends RegexMatcher {
  /**
   * The regex which matches an identifier. Taken from the
   * CSS3 grammar
   * https://drafts.csswg.org/selectors-3/#grammar. This is a
   * subset of valid names, this class does not allow random
   * bytes and such (only alphanumeric+hyphen and underscore).
   */
  private static final Pattern REGEX =
    Pattern.compile("^-?[_a-zA-Z][_a-zA-Z0-9-]*");

  /**
   * Creates a new IdentifierMatcher.
   */
  IdentifierMatcher() {
    super(IdentifierMatcher.REGEX, TokenKind.IDENTIFIER);
  }
}
