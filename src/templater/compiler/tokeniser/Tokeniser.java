package templater.compiler.tokeniser;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import templater.language.Token;

/**
 * A tokeniser/lexer for the templating language. Converts a
 * string of source code into a list of tokens. Whitespace
 * between valid tokens are ignored.
 */
public class Tokeniser {
  /** The TokenMatchers that can produce Tokens from input. */
  private final List<TokenMatcher> tokenMatchers;
  /** The character queue of source code input. */
  private final CharListQueue input;

  /**
   * Constructs a new Tokeniser for the given string of source
   * code.
   *
   * @param template The template code to tokenise.
   */
  public Tokeniser(String template) {
    this.input = new CharListQueue(template);
    this.tokenMatchers = new ArrayList<>();
    this.registerTokens();
  }

  /**
   * Adds all the possible TokenMatchers to the list of
   * matchers.
   */
  protected void registerTokens() {
    this.tokenMatchers.add(new IdentifierMatcher());
    this.tokenMatchers.add(new PunctuationMatcher());
    this.tokenMatchers.add(new StringLiteralMatcher());
    this.tokenMatchers.add(new TemplateLiteralMatcher());
  }

  /**
   * Attempts to convert the entire input program into a list
   * of Tokens.
   *
   * @return The list of Tokens within the input program.
   * @throws UnknownTokenException If the tokeniser was unable
   *                               to completely match the
   *                               whole program.
   */
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

  /**
   * Matches a single Token from the TokenMatchers in this
   * Tokeniser.
   *
   * @return The Token matched, or null if no match succeeded.
   */
  private Token matchOnce() {
    for (TokenMatcher matcher : this.tokenMatchers) {
      Token t = matcher.tryMatch(this.input);
      if (t != null) {
        return t;
      }
    }
    return null;
  }

  /**
   * Consumes all whitespace characters at the front of the
   * character queue.
   *
   * @return Whether or not there are more characters in the
   *         queue.
   */
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
