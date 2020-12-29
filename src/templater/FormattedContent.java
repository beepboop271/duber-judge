package templater;

class FormattedContent extends Node {
  private final String expression;

  public FormattedContent(String expression) {
    this.expression = expression;
  }

  public String getExpression() {
    return this.expression;
  }
}
