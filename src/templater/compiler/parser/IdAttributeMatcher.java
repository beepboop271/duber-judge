package templater.compiler.parser;

import templater.StringResolvables;
import templater.compiler.tokeniser.Token;

class IdAttributeMatcher extends TokenMatchable<StringResolvables> {
  @Override
  protected StringResolvables tryMatchInternal(TokenQueue.Iterator input) {
    Token hash = new TokenMatcher('#').tryMatch(input);
    if (hash == null) {
      return null;
    }

    StringResolvables content = new AttributeContentListMatcher().tryMatch(input);
    return content;
  }
}
