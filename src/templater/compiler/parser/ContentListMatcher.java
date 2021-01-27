package templater.compiler.parser;

import java.util.List;

import templater.language.StringResolvables;
import templater.language.Token;
import templater.language.TokenKind;

/**
 * Matches a sequence of certain non-punctuation tokens.
 *
 * <pre>
 * AnyContentList = {Identifier | StringLiteral | TemplateLiteral};
 * NoIdentContentList = {StringLiteral | TemplateLiteral};
 * </pre>
 */
class ContentListMatcher extends TokenMatchable<StringResolvables> {
  /** The tokens to consider matching. */
  private final TokenMatcher[] matchers;

  /**
   * Creates a ContentListMatcher that matches a sequence of
   * the given tokens.
   *
   * @param matchers The TokenMatchers to consider matching.
   */
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

  /**
   * Creates a matcher for any non-punctuation Token.
   *
   * @return A new ContentListMatcher which matches any non
   *         punctuation Token.
   */
  static ContentListMatcher any() {
    return new ContentListMatcher(
      new TokenMatcher(TokenKind.STRING_LITERAL),
      new TokenMatcher(TokenKind.TEMPLATE_LITERAL),
      new TokenMatcher(TokenKind.IDENTIFIER)
    );
  }

  /**
   * Creates a matcher for a non-punctuation, non-identifier
   * Token, ie a string or template literal.
   *
   * @return A new ContentListMatcher which matches a string
   *         literal or template literal token.
   */
  static ContentListMatcher noIdentifier() {
    return new ContentListMatcher(
      new TokenMatcher(TokenKind.STRING_LITERAL),
      new TokenMatcher(TokenKind.TEMPLATE_LITERAL)
    );
  }
}
