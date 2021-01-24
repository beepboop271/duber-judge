package templater.compiler.parser;

import templater.StringResolvables;
import templater.compiler.tokeniser.Token;

class ClassAttributeMatcher extends TokenMatchable<StringResolvables> {
  @Override
  protected StringResolvables tryMatchInternal(TokenQueue.Iterator input) {
    Token period = new TokenMatcher('.').tryMatch(input);
    if (period == null) {
      return null;
    }

    StringResolvables content = new AttributeContentListMatcher().tryMatch(input);
    return content;
  }
}
