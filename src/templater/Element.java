package templater;

import java.util.HashMap;
import java.util.Map;

class Element extends Node {
  private final String name;
  private final String id;
  private final Map<String, String> attributes;

  public Element(String name, String id) {
    this.name = name;
    this.id = id;
    this.attributes = new HashMap<>();
  }

  public Element(String name) {
    this(name, null);
  }

  public String getName() {
    return this.name;
  }

  public String getId() {
    return this.id;
  }

  public ReadOnlyIterator<Map.Entry<String, String>> getAttributes() {
    return new ReadOnlyIterator<>(this.attributes.entrySet().iterator());
  }

  public void addAttribute(String name, String value) {
    this.attributes.put(name, value);
  }
}
