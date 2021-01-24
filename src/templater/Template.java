package templater;

import java.util.ArrayList;
import java.util.List;

import templater.compiler.LanguageElement;
import templater.compiler.parser.Parser;
import templater.compiler.tokeniser.Tokeniser;
import templater.compiler.tokeniser.UnknownTokenException;

class Template {
  private final Root syntaxTree;

  public Template(String source) throws UnknownTokenException {
    List<LanguageElement> l = new ArrayList<>();
    l.add(
      new Parser(new Tokeniser(source).tokenise()).parse()
    );
    this.syntaxTree = new Root(l);
  }

  public Root getSyntaxTree() {
    return this.syntaxTree;
  }
}
