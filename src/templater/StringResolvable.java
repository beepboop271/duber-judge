package templater;

import templater.compiler.tokeniser.Token;
import templater.compiler.tokeniser.TokenKind;

class StringResolvable {
  private final String content;
  private final boolean isTemplate;

  public StringResolvable(Token token) {
    this.content = token.getContent();
    this.isTemplate = (token.getKind() == TokenKind.TEMPLATE_LITERAL);
  }

  public String getContent() {
    return this.content;
  }

  public boolean isTemplate() {
    return this.isTemplate;
  }
}
