package templater;

class LiteralContent extends Node {
  private final String content;

  public LiteralContent(String content) {
    this.content = content;
  }

  public String getContent() {
    return this.content;
  }
}
