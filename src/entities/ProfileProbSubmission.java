package entities;

/**
 * A problem entity used inside the templater, which stores
 * relevant information about a submission.
 * <p>
 * Created <b>2021-01-25</b>.
 *
 * @since 0.0.7
 * @version1.0.0
 * @author Shari Sun
 */
public class ProfileProbSubmission {
  /** The link to this problem. */
  private String link;
  /** The amount of points this problem is worth. */
  private int points;
  /** The amount of points the user scored on this problem. */
  private int userScore;

  private Language language;

  private ExecutionStatus status;

  private double runDuration;

  private double memoryUsage;

  private String user;


  /**
   *
   * @param link
   * @param points
   * @param userScore
   * @param language
   * @param status
   * @param runDuration
   * @param memoryUsage
   * @param userSubmissions
   */
  public ProfileProbSubmission(
    String link,
    int points,
    int userScore,
    Language language,
    ExecutionStatus status,
    double runDuration,
    double memoryUsage,
    String user
  ) {
    this.link = link;
    this.points = points;
    this.userScore = userScore;
    this.language = language;
    this.status = status;
    this.runDuration = runDuration;
    this.memoryUsage = memoryUsage;
    this.user = user;
  }

  /**
   * Retrieves this problem's link.
   *
   * @return this problem's link.
   */
  public String getLink() {
    return this.link;
  }

  /**
   * Retrieves the amount of points this problem is worth.
   *
   * @return the amount of points this problem is worth.
   */
  public int getPoints() {
    return this.points;
  }


  /**
   * Retrieves this problem's user's score.
   *
   * @return this problem's user's score.
   */
  public int getUserScore() {
    return this.userScore;
  }

  public Language getLanguage() {
    return this.language;
  }

  public ExecutionStatus getStatus() {
    return this.status;
  }

  public double getRunDuration() {
    return this.runDuration;
  }

  public double getMemoryUsage() {
    return this.memoryUsage;
  }

  public String getUser() {
    return this.user;
  }



}
