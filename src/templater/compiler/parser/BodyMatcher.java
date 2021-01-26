package templater.compiler.parser;

import java.util.List;

import templater.language.LanguageElement;
import templater.language.Token;

/**
 * Matches a block ie the children of an element.
 *
 * <pre>
 * Body = '{', [{Element | NoIdentContentList | Loop}], '}';
 * </pre>
 */
class BodyMatcher extends TokenMatchable<List<LanguageElement>> {
  @Override
  protected List<LanguageElement> tryMatchInternal(TokenQueue.Iterator input) {
    Token brace = new TokenMatcher('{').tryMatch(input);
    if (brace == null) {
      return null;
    }

    List<LanguageElement> content = new MatchUtils.ZeroOrMore<>(
      new MatchUtils.OneOf<>(
        new ElementMatcher(),
        ContentListMatcher.noIdentifier(),
        new LoopMatcher()
      )
    ).tryMatch(input);

    brace = new TokenMatcher('}').tryMatch(input);
    if (brace == null) {
      throw new UnknownSyntaxException(input.getPosition().toDisplayString());
    }

    return content;
  }
}
