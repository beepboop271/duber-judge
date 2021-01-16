package templater;

public class Loop extends Node {
  private String target;
  private String loopVariable;

  public Loop(String target, String loopVariable) {
    this.target = target;
    this.loopVariable = loopVariable;
  }

  public String getTarget() {
    return this.target;
  }

  public String getLoopVariable() {
    return this.loopVariable;
  }
}
