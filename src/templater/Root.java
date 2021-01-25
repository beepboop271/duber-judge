package templater;

import java.util.List;

import templater.compiler.LanguageElement;

/**
 * A class representing a node at the start (a.k.a. root) of
 * the syntax tree.
 *
 * @author Kevin Qiao
 * @version 1.0
 */
class Root extends Node {

  /**
   * Creates a new {@code Root}, given a list of its children.
   *
   * @param children The {@code List} of this root's children.
   */
  public Root(List<LanguageElement> children) {
    super(children);
  }
}
