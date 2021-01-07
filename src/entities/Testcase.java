package entities;

/**
 * [description]
 * <p>
 * Created on 2021.01.07.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class Testcase {
  private long batchId;
  private int order;
  private String input;
  private String output;


  public Testcase(long batchId, int order, String input, String output) {
    this.batchId = batchId;
    this.order = order;
    this.input = input;
    this.output = output;
  }

  public long getBatchId() {
    return this.batchId;
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
