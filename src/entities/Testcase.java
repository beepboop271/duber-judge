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
  private int sequence;
  private String input;
  private String output;


  public Testcase(long batchId, int sequence, String input, String output) {
    this.batchId = batchId;
    this.sequence = sequence;
    this.input = input;
    this.output = output;
  }

  public long getBatchId() {
    return this.batchId;
  }

  public int getSequence() {
    return this.sequence;
  }

  public String getInput() {
    return this.input;
  }

  public String getOutput() {
    return this.output;
  }

}
