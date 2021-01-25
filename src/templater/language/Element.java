package templater.language;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import templater.ReadOnlyIterator;

/**
 * A class representing a {@code Element}, an HTML element.
 *
 * @author Kevin Qiao, Paula Yuan
 * @version 1.0
 */
public class Element extends Node {
  private final StringResolvables name;
  private final List<StringResolvables> classes;
  private final StringResolvables id;
  private final Map<String, StringResolvables> attributes;
  private final boolean isEmpty;

  /**
   * Creates a new {@code Element}, given a list of children,
   * a name, classes, id, attributes, and whether it's an
   * empty element.
   *
   * @param children   The {@code List} of this element's
   *                   children.
   * @param name       The {@code StringResolvables} name of
   *                   the element.
   * @param classes    The {@code List} of this element's
   *                   classes.
   * @param id         The {@code StringResolvables} id of
   *                   this element.
   * @param attributes The {@code List} of this element's
   *                   attributes
   * @param isEmpty    Whether this element is an empty
   *                   element.
   */
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

  /**
   * Retrieves this element's name.
   *
   * @return The name of this element.
   */
  public StringResolvables getName() {
    return this.name;
  }

  /**
   * Retrieves this element's id.
   *
   * @return The element's id.
   */
  public StringResolvables getId() {
    return this.id;
  }

  /**
   * Retrieves whether this element is empty.
   *
   * @return Whether the element is empty.
   */
  public boolean isEmpty() {
    return this.isEmpty;
  }

  /**
   * Retrieves this element's attribute list as an iterator.
   *
   * @return The attributes.
   */
  public ReadOnlyIterator<Map.Entry<String, StringResolvables>> getAttributes() {
    return new ReadOnlyIterator<>(this.attributes.entrySet().iterator());
  }

  /**
   * Retrieves this element's class list as an iterator.
   *
   * @return The classes.
   */
  public ReadOnlyIterator<StringResolvables> getClasses() {
    return new ReadOnlyIterator<>(this.classes.iterator());
  }
}
