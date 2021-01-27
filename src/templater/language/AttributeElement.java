package templater.language;

/**
 * A language element that represents a mapping between a
 * String and a StringResolvables, such as an attribute
 * (src="hi.jpg") or a loop control (num : nums).
 *
 * @author Kevin Qiao
 * @version 1.0
 */
public class AttributeElement extends LanguageElement {
  /** The key of the mapping. */
  private final String key;
  /** The value associated with the key. */
  private final StringResolvables value;

  /**
   * Constructs a new AttributeElement mapping the given key
   * to the given value.
   *
   * @param key   The key of the mapping.
   * @param value The value associated with the key.
   */
  public AttributeElement(String key, StringResolvables value) {
    this.key = key;
    this.value = value;
  }

  /**
   * Gets the key of the mapping.
   *
   * @return The key of the mapping.
   */
  public String getKey() {
    return this.key;
  }

  /**
   * Gets the value associated with the key of this mapping.
   *
   * @return The value associated with the key of this
   *         mapping.
   */
  public StringResolvables getValue() {
    return this.value;
  }
}
