package templater;

class StringResolvable {
  private final String content;
  private final boolean isTemplate;

  public StringResolvable(String content, boolean isTemplate) {
    this.content = content;
    this.isTemplate = isTemplate;
  }

  public String getContent() {
    return this.content;
  }

  public boolean isTemplate() {
    return this.isTemplate;
  }
}
