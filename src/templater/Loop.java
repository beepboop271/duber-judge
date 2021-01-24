package templater;

import java.util.List;

import templater.compiler.LanguageElement;

public class Loop extends Node {
  private StringResolvables target;
  private String loopVariable;

  public Loop(
    List<LanguageElement> children,
    String loopVariable,
    StringResolvables target
  ) {
    super(children);
    this.target = target;
    this.loopVariable = loopVariable;
  }

  public StringResolvables getTarget() {
    return this.target;
  }

  public String getLoopVariable() {
    return this.loopVariable;
  }
}
