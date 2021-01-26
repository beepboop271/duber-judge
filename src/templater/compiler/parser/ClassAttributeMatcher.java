package templater.compiler.parser;

import templater.language.StringResolvables;
import templater.language.Token;

class ClassAttributeMatcher extends TokenMatchable<StringResolvables> {
  @Override
  protected StringResolvables tryMatchInternal(TokenQueue.Iterator input) {
    Token period = new TokenMatcher('.').tryMatch(input);
    if (period == null) {
      return null;
    }

    StringResolvables content = new AttributeContentListMatcher().tryMatch(input);
    if (content == null) {
      throw new UnknownSyntaxException(input.getPosition().toDisplayString());
    }
    return content;
  }
}
