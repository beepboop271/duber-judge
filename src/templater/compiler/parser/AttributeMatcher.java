package templater.compiler.parser;

import templater.language.AttributeElement;
import templater.language.StringResolvables;
import templater.language.Token;
import templater.language.TokenKind;

/**
 * Matches a single attribute declaration.
 *
 * <pre>
 * Attribute = Identifier, '=', NoIdentContentList, [','];
 * </pre>
 */
class AttributeMatcher extends TokenMatchable<AttributeElement> {
  @Override
  protected AttributeElement tryMatchInternal(TokenQueue.Iterator input) {
    Token key = new TokenMatcher(TokenKind.IDENTIFIER).tryMatch(input);
    if (key == null) {
      return null;
    }
    Token token = new TokenMatcher('=').tryMatch(input);
    if (token == null) {
      throw new UnknownSyntaxException(input.getPosition().toDisplayString());
    }
    StringResolvables value = ContentListMatcher.noIdentifier().tryMatch(input);
    if (value == null) {
      throw new UnknownSyntaxException(input.getPosition().toDisplayString());
    }
    new TokenMatcher(',').tryMatch(input);  // optional
    return new AttributeElement(key.getContent(), value);
  }
}
