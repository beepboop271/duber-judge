package templater;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class Node {
  private final List<Node> children;

  public Node() {
    this.children = new ArrayList<>();
  }

  public Iterator<Node> getChildren() {
    return this.children.iterator();
  }

  public void addChild(Node n) {
    this.children.add(n);
  }
}
