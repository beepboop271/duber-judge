package templater;

public class Loop extends Node {
  private StringResolvable target;
  private String loopVariable;

  public Loop(StringResolvable target, String loopVariable) {
    this.target = target;
    this.loopVariable = loopVariable;
  }

  public StringResolvable getTarget() {
    return this.target;
  }

  public String getLoopVariable() {
    return this.loopVariable;
  }
}
