package templater.compiler.tokeniser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import templater.compiler.TextFilePosition;

abstract class RegexMatcher extends TokenMatcher {
  private final Pattern regex;
  private final TokenKind kind;

  RegexMatcher(Pattern regex, TokenKind kind) {
    this.regex = regex;
    this.kind = kind;
  }

  @Override
  public Token tryMatch(CharListQueue input) {
    Matcher m = this.regex.matcher(input);
    if (m.lookingAt()) {
      // need to extract the group before consuming because
      // .group() tries to read out of the sequence, likewise,
      // input.position reports the current head, so get it
      // before consuming
      String result = m.group();
      TextFilePosition position = input.getPosition();
      input.remove(m.end());
      return new Token(result, this.kind, position);
    }
    return null;
  }
}
