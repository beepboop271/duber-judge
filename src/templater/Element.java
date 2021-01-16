package templater;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Element extends Node {
  private final StringResolvable name;
  private final StringResolvable id;
  private final boolean isEmpty;
  private final List<StringResolvable> classes;
  private final Map<StringResolvable, StringResolvable> attributes;

  public Element(StringResolvable name, StringResolvable id, boolean isEmpty) {
    this.name = name;
    this.id = id;
    this.isEmpty = isEmpty;
    this.classes = new ArrayList<>();
    this.attributes = new HashMap<>();
  }

  public Element(StringResolvable name, boolean isEmpty) {
    this(name, null, isEmpty);
  }

  public StringResolvable getName() {
    return this.name;
  }

  public StringResolvable getId() {
    return this.id;
  }

  public boolean isEmpty() {
    return this.isEmpty;
  }

  public ReadOnlyIterator<Map.Entry<StringResolvable, StringResolvable>> getAttributes() {
    return new ReadOnlyIterator<>(this.attributes.entrySet().iterator());
  }

  public ReadOnlyIterator<StringResolvable> getClasses() {
    return new ReadOnlyIterator<>(this.classes.iterator());
  }

  public void addAttribute(StringResolvable name, StringResolvable value) {
    this.attributes.put(name, value);
  }

  public void addClass(StringResolvable name) {
    this.classes.add(name);
  }
}
