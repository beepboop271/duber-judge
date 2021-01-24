package templater.compiler.parser;

import templater.StringResolvables;
import templater.compiler.tokeniser.Token;
import templater.compiler.tokeniser.TokenKind;

class AttributeMatcher extends TokenMatchable<AttributeElement> {
  @Override
  protected AttributeElement tryMatchInternal(TokenQueue.Iterator input) {
    Token key = new TokenMatcher(TokenKind.IDENTIFIER).tryMatch(input);
    if (key == null) {
      return null;
    }
    Token token = new TokenMatcher('=').tryMatch(input);
    if (token == null) {
      return null;
    }
    StringResolvables value = new ContentListMatcher().tryMatch(input);
    if (value == null) {
      return null;
    }
    new TokenMatcher(',').tryMatch(input);  // optional
    return new AttributeElement(key.getContent(), value);
  }
}
