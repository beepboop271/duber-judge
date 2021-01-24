package templater.compiler.parser;

import java.util.List;

import templater.Loop;
import templater.compiler.LanguageElement;
import templater.compiler.parser.TokenQueue.Iterator;
import templater.compiler.tokeniser.Token;
import templater.compiler.tokeniser.TokenKind;

class LoopMatcher extends TokenMatchable<Loop> {
  @Override
  protected Loop tryMatchInternal(Iterator input) {
    Token name = new TokenMatcher(TokenKind.IDENTIFIER).tryMatch(input);
    if ((name == null) || !(name.getContent().equals("for"))) {
      return null;
    }

    AttributeElement control = new IterationControlMatcher().tryMatch(input);
    if (control == null) {
      return null;
    }

    List<LanguageElement> body = new BlockMatcher().tryMatch(input);
    if (body == null) {
      return null;
    }

    return new Loop(body, control.getKey(), control.getValue());
  }
}
