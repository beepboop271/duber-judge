package templater;

class Template {
  private final Root syntaxTree;

  public Template() {
    this.syntaxTree = new Root();
  }

  public Root getSyntaxTree() {
    return this.syntaxTree;
  }
}
