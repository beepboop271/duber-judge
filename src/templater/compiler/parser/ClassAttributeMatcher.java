package templater.compiler.parser;

import templater.compiler.tokeniser.Token;
import templater.language.StringResolvables;

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
