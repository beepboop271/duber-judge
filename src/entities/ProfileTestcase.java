package entities;

/**
 * A testcase entity used inside the templater, which stores
 * relevant information about a problem's testcases.
 * <p>
 * Created <b>2021-01-25</b>.
 *
 * @since 0.0.7
 * @version 0.0.7
 * @author Joseph Wang
 */
public class ProfileTestcase {
  /** The link to this testcase. */
  private String link;
  /** The testcase object that holds information. */
  private Testcase testcase;
  /** The id of this testcase. */
  private long testcaseId;

  /**
   * Constructs a new ProfileTestcase.
   *
   * @param link       The link to this testcase.
   * @param testcaseId The id of this testcase.
   * @param testcase   The actual testcase.
   */
  public ProfileTestcase(String link, long testcaseId, Testcase testcase) {
    this.link = link;
    this.testcaseId = testcaseId;
    this.testcase = testcase;
  }

  /**
   * Retrieves this testcase's sequence number.
   *
   * @return this testcase's sequence number.
   */
  public int getSequence() {
    return testcase.getSequence();
  }

  /**
   * Retrieves this testcase's link.
   *
   * @return this testcase's link.
   */
  public String getLink() {
    return this.link;
  }

  /**
   * Retrieves the testcase with information.
   *
   * @return the testcase with information.
   */
  public Testcase getTestcase() {
    return this.testcase;
  }

  /**
   * Retrieves this testcase's id.
   *
   * @return this testcase's id.
   */
  public long getTestcaseId() {
    return this.testcaseId;
  }
}
