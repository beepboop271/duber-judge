package templater.compiler.parser;

import java.util.Collection;

import templater.language.Element;
import templater.language.Token;

/**
 * Parses a program from list of tokens into a single
 * Element node.
 */
public class Parser {
  /** The tokens of the program to parse. */
  private final TokenQueue tokens;

  /**
   * Creates a new Parser for the given collection of Tokens.
   * The tokens are interpreted in the order they are returned
   * from the collection's iterator.
   *
   * @param tokens The tokens of the program to parse.
   */
  public Parser(Collection<Token> tokens) {
    this.tokens = new TokenQueue(tokens);
  }

  /**
   * Attempts to match the tokens into a single root Element.
   *
   * @return The Element produced. null may also be returned
   *         if the program failed but an exception was not
   *         thrown for some reason.
   * @throws UnknownSyntaxException When tokens cannot be
   *                                parsed into a program.
   */
  public Element parse() throws UnknownSyntaxException {
    return new ElementMatcher().tryMatch(this.tokens.iterator());
  }
}
