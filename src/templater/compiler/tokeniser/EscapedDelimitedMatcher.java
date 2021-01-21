package templater.compiler.tokeniser;

import templater.compiler.TextFilePosition;

abstract class EscapedDelimitedMatcher extends TokenMatcher {
  private final String startDelimiter;
  private final char endDelimiter;
  private final TokenKind kind;

  EscapedDelimitedMatcher(
    String startDelimiter,
    char endDelimiter,
    TokenKind kind
  ) {
    this.startDelimiter = startDelimiter;
    this.endDelimiter = endDelimiter;
    this.kind = kind;
  }

  @Override
  public Token tryMatch(CharArrayQueue input) {
    try {
      int i = 0;
      for (; i < this.startDelimiter.length(); ++i) {
        if (input.charAt(i) != this.startDelimiter.charAt(i)) {
          return null;
        }
      }

      StringBuilder sb = new StringBuilder();
      char c = input.charAt(i++);

      while (c != this.endDelimiter) {
        while (c == '\\') {
          // skip the backslash (no sb.append(c)) and accept
          // the next character, no matter what it is
          sb.append(input.charAt(i++));
          c = input.charAt(i++);
          if (c == this.endDelimiter) {
            return this.end(input, sb, i);
          }
        }
        sb.append(c);
        c = input.charAt(i++);
      }
      return this.end(input, sb, i);

    } catch (IndexOutOfBoundsException e) {
      return null;
    }
  }

  private Token end(CharArrayQueue input, StringBuilder sb, int i) {
    TextFilePosition position = input.getPosition();
    TokenMatcher.consumeInput(input, i);
    return new Token(sb.toString(), this.kind, position);
  }
}
