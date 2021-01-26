package templater.compiler.tokeniser;

import java.util.regex.Pattern;

import templater.language.TokenKind;

/**
 * Matches an {@code templater.language.TokenKind.KEYWORD}
 * using regex.
 */
class KeywordMatcher extends RegexMatcher {
  /**
   * The regex which matches a keyword. Currently there is
   * only one keyword, 'for'.
   */
  private static final Pattern REGEX =
    Pattern.compile("^for");

  /**
   * Creates a new KeywordMatcher.
   */
  KeywordMatcher() {
    super(KeywordMatcher.REGEX, TokenKind.KEYWORD);
  }
}
