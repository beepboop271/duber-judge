package templater.compiler.parser;

import templater.language.StringResolvables;
import templater.language.Token;

/**
 * Matches a single element class declaration.
 *
 * <pre>
 * ClassAttribute = '.', AnyContentList;
 * </pre>
 */
class ClassAttributeMatcher extends TokenMatchable<StringResolvables> {
  @Override
  protected StringResolvables tryMatchInternal(TokenQueue.Iterator input) {
    Token period = new TokenMatcher('.').tryMatch(input);
    if (period == null) {
      return null;
    }

    StringResolvables content = ContentListMatcher.any().tryMatch(input);
    if (content == null) {
      throw new UnknownSyntaxException(input.getPosition().toDisplayString());
    }
    return content;
  }
}
