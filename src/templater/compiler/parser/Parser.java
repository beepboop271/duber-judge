package templater.compiler.parser;

import java.util.Collection;

import templater.compiler.tokeniser.Token;
import templater.language.Element;

public class Parser {
  private final TokenQueue tokens;

  public Parser(Collection<Token> tokens) {
    this.tokens = new TokenQueue(tokens);
  }

  public Element parse() {
    return new ElementMatcher().tryMatch(this.tokens.iterator());
  }
}
