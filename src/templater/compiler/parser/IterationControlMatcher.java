package templater.compiler.parser;

import templater.compiler.tokeniser.Token;
import templater.compiler.tokeniser.TokenKind;
import templater.language.AttributeElement;
import templater.language.StringResolvables;

class IterationControlMatcher extends TokenMatchable<AttributeElement> {
  @Override
  protected AttributeElement tryMatchInternal(TokenQueue.Iterator input) {
    Token punctuation = new TokenMatcher('(').tryMatch(input);
    if (punctuation == null) {
      return null;
    }

    Token loopingName = new TokenMatcher(TokenKind.IDENTIFIER).tryMatch(input);
    if (loopingName == null) {
      return null;
    }

    punctuation = new TokenMatcher(':').tryMatch(input);
    if (punctuation == null) {
      return null;
    }

    StringResolvables nameToLoop = new AttributeContentListMatcher().tryMatch(input);
    if (nameToLoop == null) {
      return null;
    }

    punctuation = new TokenMatcher(')').tryMatch(input);
    if (punctuation == null) {
      return null;
    }

    return new AttributeElement(loopingName.getContent(), nameToLoop);
  }
}
