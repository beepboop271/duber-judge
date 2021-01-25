package templater.compiler.tokeniser;

import java.util.regex.Pattern;

import templater.language.TokenKind;

class Identifier extends RegexMatcher {
  private static final Pattern REGEX =
    // identifier name from the CSS3 grammar
    // https://drafts.csswg.org/selectors-3/#grammar
    // this is a subset of valid names, we won't be allowing
    // random bytes and such
    Pattern.compile("^-?[_a-zA-Z][_a-zA-Z0-9-]*");

  Identifier() {
    super(Identifier.REGEX, TokenKind.IDENTIFIER);
  }
}
