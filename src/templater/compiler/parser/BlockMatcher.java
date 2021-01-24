package templater.compiler.parser;

import java.util.List;

import templater.compiler.LanguageElement;
import templater.compiler.parser.TokenQueue.Iterator;
import templater.compiler.tokeniser.Token;

class BlockMatcher extends TokenMatchable<List<LanguageElement>> {
  @Override
  protected List<LanguageElement> tryMatchInternal(Iterator input) {
    Token brace = new TokenMatcher('{').tryMatch(input);
    if (brace == null) {
      return null;
    }

    List<LanguageElement> content = new MatchUtils.ZeroOrMore<>(
      new MatchUtils.OneOf<>(
        new ElementMatcher(),
        new ContentListMatcher(),
        new LoopMatcher()
      )
    ).tryMatch(input);

    brace = new TokenMatcher('}').tryMatch(input);
    if (brace == null) {
      return null;
    }

    return content;
  }
}
