package templater.language;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A class representing a node in the syntax tree.
 *
 * @author Kevin Qiao
 * @version 1.0
 */
public class Node extends LanguageElement {
  private final List<LanguageElement> children;

  /**
   * Creates a new {@code Node}, given a list of its children.
   *
   * @param children The {@code List} of this node's children.
   */
  public Node(List<LanguageElement> children) {
    this.children = new ArrayList<>(children);
  }

  /**
   * Retrieves this node's children as an iterator.
   *
   * @return The children.
   */
  public ReadOnlyIterator<LanguageElement> getChildren() {
    return new ReadOnlyIterator<>(this.children.iterator());
  }
}
