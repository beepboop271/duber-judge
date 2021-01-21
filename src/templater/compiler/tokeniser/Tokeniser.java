package templater.compiler.tokeniser;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class Tokeniser {
  private final List<TokenMatcher> tokenMatchers;
  private final CharArrayQueue input;

  public Tokeniser(String template) {
    this.input = new CharArrayQueue(template);
    this.tokenMatchers = new ArrayList<>();
    this.registerTokens();
  }

  protected void registerTokens() {
    this.tokenMatchers.add(new Identifier());
    this.tokenMatchers.add(new Punctuation());
    this.tokenMatchers.add(new StringLiteral());
    this.tokenMatchers.add(new TemplateLiteral());
  }

  public List<Token> tokenise() throws UnknownTokenException {
    List<Token> tokens = new ArrayList<>();

    // all tokens start and end within the program, so unless
    // UnknownTokenException is thrown, the only way the end
    // of input can be reached is when we are trying to consume
    // whitespace between tokens.
    while (this.consumeWhitespace()) {
      Token t = this.matchOnce();
      if (t == null) {
        // there should never be any position that cannot match
        // any token
        throw new UnknownTokenException(
          "Unknown token on "+this.input.getPosition().toDisplayString()
        );
      }
      tokens.add(t);
    }
    return tokens;
  }

  private Token matchOnce() {
    for (TokenMatcher matcher : this.tokenMatchers) {
      Token t = matcher.tryMatch(this.input);
      if (t != null) {
        return t;
      }
    }
    return null;
  }

  private boolean consumeWhitespace() {
    try {
      char first = this.input.element();
      while (Character.isWhitespace(first)) {
        this.input.remove();
        first = this.input.element();
      }
      return true;
    } catch (NoSuchElementException e) {
      return false;
    }
  }
}
