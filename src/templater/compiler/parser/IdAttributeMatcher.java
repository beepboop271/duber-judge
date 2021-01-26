package templater.compiler.parser;

import templater.language.StringResolvables;
import templater.language.Token;

class IdAttributeMatcher extends TokenMatchable<StringResolvables> {
  @Override
  protected StringResolvables tryMatchInternal(TokenQueue.Iterator input) {
    Token hash = new TokenMatcher('#').tryMatch(input);
    if (hash == null) {
      return null;
    }

    StringResolvables content = new AttributeContentListMatcher().tryMatch(input);
    if (content == null) {
      throw new UnknownSyntaxException(input.getPosition().toDisplayString());
    }
    return content;
  }
}
