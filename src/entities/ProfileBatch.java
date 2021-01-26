package entities;

import java.util.List;

/**
 * A batch entity used inside the templater, which stores
 * relevant information about a problem's batches and
 * testcases. *
 * <p>
 * Created <b>2021-01-25</b>.
 *
 * @since 0.0.7
 * @version 0.0.7
 * @author Joseph Wang
 */
public class ProfileBatch {
  /** The list of contained profile testcases. */
  private List<ProfileTestcase> testcases;
  /** The id of this batch. */
  private long batchId;
  /** This batch's sequence number. */
  private int batchSequence;
  /** A link for adding a testcase to this batch. */
  private String addTestcaseLink;

  /**
   * Constructs a new ProfileBatch.
   *
   * @param testcases The list of testcases in this batch.
   * @param batchId   The id of this batch.
   * @param sequence  The sequence number of this batch.
   * @param addTestcaseLink the link to add a testcase to this batch.
   */
  public ProfileBatch(
    List<ProfileTestcase> testcases,
    long batchId,
    int sequence,
    String addTestcaseLink
  ) {
    this.testcases = testcases;
    this.batchId = batchId;
    this.batchSequence = sequence;
    this.addTestcaseLink = addTestcaseLink;
  }

  /**
   * Retrieves this batch's testcases.
   *
   * @return a list of testcases.
   */
  public List<ProfileTestcase> getTestcases() {
    return this.testcases;
  }

  /**
   * Retrieves this batch's sequence number.
   *
   * @return this batch's sequence number.
   */
  public int getSequence() {
    return this.batchSequence;
  }

  /**
   * Retrieves this batch's id.
   *
   * @return this batch's id.
   */
  public long getBatchId() {
    return this.batchId;
  }

  /**
   * Retrieves the link to add testcases to this batch.
   *
   * @return the link to add testcases to this batch.
   */
  public String getAddTestcaseLink() {
    return this.addTestcaseLink;
  }
}
