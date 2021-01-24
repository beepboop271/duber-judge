package templater;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import templater.compiler.LanguageElement;

public class Element extends Node {
  private final StringResolvables name;
  private final List<StringResolvables> classes;
  private final StringResolvables id;
  private final Map<String, StringResolvables> attributes;
  private final boolean isEmpty;

  public Element(
    List<LanguageElement> children,
    StringResolvables name,
    List<StringResolvables> classes,
    StringResolvables id,
    Map<String, StringResolvables> attributes,
    boolean isEmpty
  ) {
    super(children);
    this.name = name;
    this.classes = new ArrayList<>(classes);
    this.id = id;
    this.attributes = new HashMap<>(attributes);
    this.isEmpty = isEmpty;
  }

  public StringResolvables getName() {
    return this.name;
  }

  public StringResolvables getId() {
    return this.id;
  }

  public boolean isEmpty() {
    return this.isEmpty;
  }

  public ReadOnlyIterator<Map.Entry<String, StringResolvables>> getAttributes() {
    return new ReadOnlyIterator<>(this.attributes.entrySet().iterator());
  }

  public ReadOnlyIterator<StringResolvables> getClasses() {
    return new ReadOnlyIterator<>(this.classes.iterator());
  }
}
