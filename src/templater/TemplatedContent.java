package templater;

class TemplatedContent extends Node {
  private final String expression;

  public TemplatedContent(String expression) {
    this.expression = expression;
  }

  public String getExpression() {
    return this.expression;
  }
}
