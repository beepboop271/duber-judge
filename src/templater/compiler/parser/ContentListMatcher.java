package templater.compiler.parser;

import java.util.List;

import templater.language.StringResolvables;
import templater.language.Token;
import templater.language.TokenKind;

class ContentListMatcher extends TokenMatchable<StringResolvables> {
  @Override
  protected StringResolvables tryMatchInternal(TokenQueue.Iterator input) {
    List<Token> match =
      new MatchUtils.OneOrMore<>(
        new MatchUtils.OneOf<>(
          new TokenMatcher(TokenKind.STRING_LITERAL),
          new TokenMatcher(TokenKind.TEMPLATE_LITERAL)
        )
      ).tryMatch(input);

    if (match == null) {
      return null;
    }
    return new StringResolvables(match);
  }
}
