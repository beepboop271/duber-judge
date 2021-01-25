package templater.compiler.parser;

import templater.compiler.tokeniser.Token;
import templater.language.StringResolvables;

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
