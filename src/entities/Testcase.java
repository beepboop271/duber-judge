package entities;

public class Testcase {
  private int order;
  private String input;
  private String output;


  public Testcase(int order, String input, String output) {
    this.order = order;
    this.input = input;
    this.output = output;
  }

  public int getOrder() {
    return this.order;
  }

  public String getInput() {
    return this.input;
  }

  public String getOutput() {
    return this.output;
  }

}
