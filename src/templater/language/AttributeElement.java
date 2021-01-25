package templater.language;

public class AttributeElement extends LanguageElement {
  private final String key;
  private final StringResolvables value;

  public AttributeElement(String key, StringResolvables value) {
    this.key = key;
    this.value = value;
  }

  public String getKey() {
    return this.key;
  }

  public StringResolvables getValue() {
    return this.value;
  }
}
