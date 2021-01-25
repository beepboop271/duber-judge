package templater.compiler.tokeniser;

import java.util.regex.Pattern;

import templater.language.TokenKind;

class Punctuation extends RegexMatcher {
  private static final Pattern REGEX =
    // punctuation symbols. $ and % from ${} and %{} do not
    // count for the same reason "" does not count: the literal
    // is parsed as a whole because the $, %, and " symbols
    // are solely for literals
    Pattern.compile("^[(){}.#=,;:]");

  Punctuation() {
    super(Punctuation.REGEX, TokenKind.PUNCTUATION);
  }
}
