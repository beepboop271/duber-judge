package templater.compiler.tokeniser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import templater.compiler.TextFilePosition;
import templater.language.Token;
import templater.language.TokenKind;

/**
 * A TokenMatcher that matches a regular expression.
 */
abstract class RegexMatcher extends TokenMatcher {
  /** The regular expression to match. */
  private final Pattern regex;
  /** The type of Token this matcher creates. */
  private final TokenKind kind;

  /**
   * Creates a new matcher that matches Tokens of the given
   * kind using the given regular expression.
   *
   * @param regex The regular expression to match.
   * @param kind  The type of Token this matcher creates.
   */
  RegexMatcher(Pattern regex, TokenKind kind) {
    this.regex = regex;
    this.kind = kind;
  }

  /**
   * {@inheritDoc} The matcher uses
   * {@link java.util.regex.Matcher#lookingAt()} to match the
   * given regex at the start of the input sequence, so
   * placing an anchor at the start of the pattern is
   * optional.
   */
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
