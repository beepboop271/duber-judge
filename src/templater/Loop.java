package templater;

public class Loop extends Node {
  private String target;

  public Loop(String target) {
    this.target = target;
  }

  public String getTarget() {
    return this.target;
  }
}
