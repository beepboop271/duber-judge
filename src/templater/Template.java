package templater;

import java.util.Arrays;

import templater.compiler.parser.Parser;
import templater.compiler.parser.UnknownSyntaxException;
import templater.compiler.tokeniser.Tokeniser;
import templater.compiler.tokeniser.UnknownTokenException;
import templater.language.Root;

/**
 * A class representing an entire HTML template, consisting
 * of a syntax tree.
 *
 * @author Kevin Qiao
 * @version 1.0
 */
class Template {
  private final Root syntaxTree;

  /**
   * Creates a new {@code Template} by using the tokeniser and
   * parser on a source string written in the template
   * language.
   *
   * @param source The string in template language that
   *               details this template.
   * @throws UnknownTokenException  When an unknown string is
   *                                found in the source.
   * @throws UnknownSyntaxException When tokens cannot be
   *                                parsed into a program.
   */
  public Template(String source)
    throws UnknownTokenException, UnknownSyntaxException {
    this.syntaxTree = new Root(Arrays.asList(
      new Parser(new Tokeniser(source).tokenise()).parse()
    ));
  }

  /**
   * Retrieves this {@code Template}'s syntax tree.
   *
   * @return Root, the syntax tree.
   */
  public Root getSyntaxTree() {
    return this.syntaxTree;
  }
}
