package templater;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import templater.compiler.LanguageElement;

/**
 * A class representing a node in the syntax tree.
 *
 * @author Kevin Qiao
 * @version 1.0
 */
class Node extends LanguageElement {
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
   * @return Iterator<LanguageElement>, the children.
   */
  public Iterator<LanguageElement> getChildren() {
    return this.children.iterator();
  }

  /**
   * Adds a child to this node.
   *
   * @param n the {@code LanguageElement} to add as a child.
   */
  public void addChild(LanguageElement n) {
    this.children.add(n);
  }
}
