package templater.compiler.parser;

import java.util.List;

import templater.language.AttributeElement;
import templater.language.LanguageElement;
import templater.language.Loop;
import templater.language.Token;
import templater.language.TokenKind;

class LoopMatcher extends TokenMatchable<Loop> {
  @Override
  protected Loop tryMatchInternal(TokenQueue.Iterator input) {
    Token name = new TokenMatcher(TokenKind.IDENTIFIER).tryMatch(input);
    if ((name == null) || !(name.getContent().equals("for"))) {
      return null;
    }

    AttributeElement control = new IterationControlMatcher().tryMatch(input);
    if (control == null) {
      throw new UnknownSyntaxException(input.getPosition().toDisplayString());
    }

    List<LanguageElement> body = new BlockMatcher().tryMatch(input);
    if (body == null) {
      throw new UnknownSyntaxException(input.getPosition().toDisplayString());
    }

    return new Loop(body, control.getKey(), control.getValue());
  }
}
