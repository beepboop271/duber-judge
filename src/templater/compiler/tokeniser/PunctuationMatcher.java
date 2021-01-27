package templater.compiler.tokeniser;

import java.util.regex.Pattern;

import templater.language.TokenKind;

/**
 * Matches an
 * {@code templater.language.TokenKind.PUNCTUATION} using
 * regex.
 */
class PunctuationMatcher extends RegexMatcher {
  /**
   * The regex which matches punctuation. $ from ${} does not
   * count for the same reason "" does not count: the literal
   * is parsed as a whole because the $, and " symbols are
   * solely for literals and do not represent any punctuation
   * of the language as a whole.
   */
  private static final Pattern REGEX = Pattern.compile("^[(){}.#=,;:]");

  /**
   * Creates a new PunctuationMatcher.
   */
  PunctuationMatcher() {
    super(PunctuationMatcher.REGEX, TokenKind.PUNCTUATION);
  }
}
