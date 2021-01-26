package templater.compiler.parser;

import java.util.List;

import templater.language.StringResolvables;
import templater.language.Token;
import templater.language.TokenKind;

class ContentListMatcher extends TokenMatchable<StringResolvables> {
  private final TokenMatcher[] matchers;

  ContentListMatcher(TokenMatcher... matchers) {
    this.matchers = matchers;
  }

  @Override
  protected StringResolvables tryMatchInternal(TokenQueue.Iterator input) {
    List<Token> match =
      new MatchUtils.OneOrMore<>(
        new MatchUtils.OneOf<>(this.matchers)
      ).tryMatch(input);

    if (match == null) {
      return null;
    }
    return new StringResolvables(match);
  }

  static ContentListMatcher any() {
    return new ContentListMatcher(
      new TokenMatcher(TokenKind.STRING_LITERAL),
      new TokenMatcher(TokenKind.TEMPLATE_LITERAL),
      new TokenMatcher(TokenKind.IDENTIFIER)
    );
  }

  static ContentListMatcher noIdentifier() {
    return new ContentListMatcher(
      new TokenMatcher(TokenKind.STRING_LITERAL),
      new TokenMatcher(TokenKind.TEMPLATE_LITERAL)
    );
  }
}
