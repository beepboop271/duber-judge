package templater.compiler.parser;

import java.util.Collection;

import templater.Element;
import templater.compiler.tokeniser.Token;

public class Parser {
  private TokenQueue tokens;

  public Parser(Collection<Token> tokens) {
    this.tokens = new TokenQueue(tokens);
  }

  public Element parse() {
    return new ElementMatcher().tryMatch(this.tokens.iterator());
  }
}
