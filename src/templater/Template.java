package templater;

import java.util.ArrayList;
import java.util.List;

import templater.compiler.LanguageElement;
import templater.compiler.parser.Parser;
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
   */
  public Template(String source) throws UnknownTokenException {
    List<LanguageElement> l = new ArrayList<>();
    l.add(
      new Parser(new Tokeniser(source).tokenise()).parse()
    );
    this.syntaxTree = new Root(l);
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
