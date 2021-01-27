package templater.compiler.parser;

import templater.language.AttributeElement;
import templater.language.StringResolvables;
import templater.language.Token;
import templater.language.TokenKind;

/**
 * Matches the header of a loop which declares which
 * variables are involved in the loop.
 *
 * <pre>
 * IterationControl = '(', Identifier, ':', AnyContentList, ')';
 * </pre>
 */
class IterationControlMatcher extends TokenMatchable<AttributeElement> {
  @Override
  protected AttributeElement tryMatchInternal(TokenQueue.Iterator input) {
    Token punctuation = new TokenMatcher('(').tryMatch(input);
    if (punctuation == null) {
      return null;
    }

    Token loopingName = new TokenMatcher(TokenKind.IDENTIFIER).tryMatch(input);
    if (loopingName == null) {
      throw new UnknownSyntaxException(input.getPosition().toDisplayString());
    }

    punctuation = new TokenMatcher(':').tryMatch(input);
    if (punctuation == null) {
      throw new UnknownSyntaxException(input.getPosition().toDisplayString());
    }

    StringResolvables nameToLoop = ContentListMatcher.any().tryMatch(input);
    if (nameToLoop == null) {
      throw new UnknownSyntaxException(input.getPosition().toDisplayString());
    }

    punctuation = new TokenMatcher(')').tryMatch(input);
    if (punctuation == null) {
      throw new UnknownSyntaxException(input.getPosition().toDisplayString());
    }

    return new AttributeElement(loopingName.getContent(), nameToLoop);
  }
}
