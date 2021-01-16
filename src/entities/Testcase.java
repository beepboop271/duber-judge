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
  private long creatorId;
  private int sequence;
  private String input;
  private String output;


  /**
   *
   * @param batchId
   * @param sequence
   * @param input
   * @param output
   */
  public Testcase(long batchId, long creatorId, int sequence, String input, String output) {
    this.batchId = batchId;
    this.creatorId = creatorId;
    this.sequence = sequence;
    this.input = input;
    this.output = output;
  }

  public long getBatchId() {
    return this.batchId;
  }

  public long getCreatorId() {
    return this.creatorId;
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
