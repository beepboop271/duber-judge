package templater.compiler.tokeniser;

import templater.language.Token;
import templater.language.TokenKind;

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
