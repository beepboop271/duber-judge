package entities;

/**
 * An entity designed to represent one test case in a batch of test cases.
 * <p>
 * Created on 2021.01.07.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class Testcase {
  /** The batch id for this test case. */
  private long batchId;
  /** The sequence number for this test case. */
  private int sequence;
  /** The input for this test case. */
  private String input;
  /** The expected output test case. */
  private String output;

  /**
   * Constructs a new Testcase.
   * 
   * @param batchId the batch id for this test case.
   * @param sequence the sequence number for this test case.
   * @param input the input for this test case.
   * @param output the expected output test case.
   */
  public Testcase(long batchId, int sequence, String input, String output) {
    this.batchId = batchId;
    this.sequence = sequence;
    this.input = input;
    this.output = output;
  }

  /**
   * Retrieves this test case's batch id.
   * 
   * @return this test case's batch id.
   */
  public long getBatchId() {
    return this.batchId;
  }

  /**
   * Retrieves this test case's sequence number.
   * 
   * @return this test case's sequence number.
   */
  public int getSequence() {
    return this.sequence;
  }

  /**
   * Retrieves this test case's input.
   * 
   * @return this test case's input.
   */
  public String getInput() {
    return this.input;
  }

  /**
   * Retrieves this test case's expected output.
   * 
   * @return this test case's output.
   */
  public String getOutput() {
    return this.output;
  }

}
