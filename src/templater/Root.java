package templater;

import java.util.List;

import templater.compiler.LanguageElement;

class Root extends Node {

  public Root(List<LanguageElement> children) {
    super(children);
  }
}
