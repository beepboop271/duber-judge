package templater;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import templater.compiler.LanguageElement;

class Node extends LanguageElement {
  private final List<LanguageElement> children;

  public Node(List<LanguageElement> children) {
    this.children = new ArrayList<>(children);
  }

  public Iterator<LanguageElement> getChildren() {
    return this.children.iterator();
  }

  public void addChild(LanguageElement n) {
    this.children.add(n);
  }
}
