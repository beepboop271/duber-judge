package templater.compiler.parser;

import templater.StringResolvables;
import templater.compiler.LanguageElement;

class AttributeElement extends LanguageElement {
  private String key;
  private StringResolvables value;

  AttributeElement(String key, StringResolvables value) {
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
