package templater.compiler.tokeniser;

import templater.compiler.TextFilePosition;

public class Token {
  private final String content;
  private final TokenKind kind;
  private final TextFilePosition position;

  Token(String content, TokenKind kind, TextFilePosition position) {
    this.content = content;
    this.kind = kind;
    this.position = position.clone();
  }

  @Override
  public String toString() {
    return super.toString()
      +"[kind:"+this.kind
      +",position:"+this.position.toDisplayString()
      +",content:\""+this.content+"\"]";
  }

  public String getContent() {
    return this.content;
  }

  public TokenKind getKind() {
    return this.kind;
  }

  public TextFilePosition getLineNumber() {
    return this.position.clone();
  }
}
