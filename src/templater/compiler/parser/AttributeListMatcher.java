package templater.compiler.parser;

import java.util.List;

import templater.compiler.tokeniser.Token;
import templater.language.AttributeElement;

class AttributeListMatcher extends TokenMatchable<List<AttributeElement>> {
  @Override
  protected List<AttributeElement> tryMatchInternal(TokenQueue.Iterator input) {
    Token paren = new TokenMatcher('(').tryMatch(input);
    if (paren == null) {
      return null;
    }

    List<AttributeElement> args = new MatchUtils.ZeroOrMore<>(
      new AttributeMatcher()
    ).tryMatch(input);
    // zero or more never returns null

    paren = new TokenMatcher(')').tryMatch(input);
    if (paren == null) {
      return null;
    }

    return args;
  }
}
