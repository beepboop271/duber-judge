package templater.compiler.tokeniser;

import templater.language.Token;
import templater.language.TokenKind;

/**
 * A TokenMatcher that searches for delimited content,
 * supporting escape characters.
 */
abstract class EscapedDelimitedMatcher extends TokenMatcher {
  /** The string which marks the beginning of a sequence. */
  private final String startDelimiter;
  /** The character which marks the end of a sequence. */
  private final char endDelimiter;
  /** The type of Token this matcher creates. */
  private final TokenKind kind;

  /**
   * Creates a new matcher that matches Tokens of the given
   * kind using the given delimiters.
   *
   * @param startDelimiter The string which marks the
   *                       beginning of a sequence.
   * @param endDelimiter   The character which marks the end
   *                       of a sequence.
   * @param kind           The type of Token this matcher
   *                       creates.
   */
  EscapedDelimitedMatcher(
    String startDelimiter,
    char endDelimiter,
    TokenKind kind
  ) {
    this.startDelimiter = startDelimiter;
    this.endDelimiter = endDelimiter;
    this.kind = kind;
  }

  /**
   * {@inheritDoc} The matcher retrieves all characters in
   * between the delimiters. In addition, a backslash in front
   * of any character will emit just that character, including
   * if the character is the endDelimiter or another
   * backslash.
   */
  @Override
  public Token tryMatch(CharListQueue input) {
    try {
      CharListQueue.Iterator it = input.iterator();
      for (int i = 0; i < this.startDelimiter.length(); ++i) {
        if (it.next() != this.startDelimiter.charAt(i)) {
          return null;
        }
      }

      StringBuilder sb = new StringBuilder();
      char c = it.next();

      while (c != this.endDelimiter) {
        while (c == '\\') {
          // skip the backslash (no sb.append(c)) and accept
          // the next character, no matter what it is
          sb.append(it.next());
          c = it.next();
          if (c == this.endDelimiter) {
            return this.end(input, it, sb);
          }
        }
        sb.append(c);
        c = it.next();
      }
      return this.end(input, it, sb);

    } catch (IndexOutOfBoundsException e) {
      return null;
    }
  }

  /**
   * Ends the match, returning the produced Token.
   *
   * @param input The input queue.
   * @param it    The iterator over the input queue.
   * @param sb    The StringBuilder containing the Token's
   *              content.
   * @return A new Token containing the information collected
   *         by this matcher.
   */
  private Token end(
    CharListQueue input,
    CharListQueue.Iterator it,
    StringBuilder sb
  ) {
    TextFilePosition position = input.getPosition();
    it.consumeRead();
    return new Token(sb.toString(), this.kind, position);
  }
}
