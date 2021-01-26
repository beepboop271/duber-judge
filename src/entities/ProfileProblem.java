package entities;

/**
 * A problem entity used inside the templater, which stores
 * relevant information about a problem and a user's results
 * on the problem.
 * <p>
 * Created <b>2021-01-25</b>.
 *
 * @since 0.0.7
 * @version1.0.0
 * @author Joseph Wang, Shari Sun
 */
public class ProfileProblem {
  /** The link to this problem. */
  private String link;
  /** This problem's category. */
  private Category category;
  /** This problem's title. */
  private String title;
  /** The amount of points this problem is worth. */
  private int points;
  /** The amount of points the user scored on this problem. */
  private int userScore;
  /** This problem's number of total submissions. */
  private int numSubmissions;
  /** This problem's number of cleared submissions. */
  private int clearedSubmissions;
  /** This problem's publishing state. */
  private PublishingState publishingState;

  private Language language;

  private ExecutionStatus status;

  private double runDuration;

  private double memoryUsage;

  private int userSubmissions;

  /**
   * Constructs a new ProfileProblem. Assumes all problems
   * made using this constructor is published.
   *
   * @param link               The link to this problem.
   * @param category           This problem's category.
   * @param title              This problem's title.
   * @param points             The amount of points this
   *                           problem is worth.
   * @param userScore          The amount of points the user
   *                           scored on this problem.
   * @param numSubmissions     This problem's number of total
   *                           submissions.
   * @param clearedSubmissions This problem's number of
   *                           cleared submissions.
   */
  public ProfileProblem(
    String link,
    Category category,
    String title,
    int points,
    int userScore,
    int numSubmissions,
    int clearedSubmissions
  ) {
    this.link = link;
    this.category = category;
    this.title = title;
    this.points = points;
    this.userScore = userScore;
    this.numSubmissions = numSubmissions;
    this.clearedSubmissions = clearedSubmissions;
    this.publishingState = PublishingState.PUBLISHED;
  }

  // TODO: potentially reformat to simply accepting a problem
  // and associated fields
  /**
   * Constructs a new ProfileProblem. Assumes all problems
   * made using this constructor is published.
   *
   * @param link               The link to this problem.
   * @param category           This problem's category.
   * @param title              This problem's title.
   * @param points             The amount of points this
   *                           problem is worth.
   * @param userScore          The amount of points the user
   *                           scored on this problem.
   * @param numSubmissions     This problem's number of total
   *                           submissions.
   * @param clearedSubmissions This problem's number of
   *                           cleared submissions.
   */
  public ProfileProblem(
    String link,
    Category category,
    String title,
    int points,
    int userScore,
    int numSubmissions,
    int clearedSubmissions,
    Language language,
    ExecutionStatus status,
    double runDuration,
    double memoryUsage,
    int userSubmissions
  ) {
    this.link = link;
    this.category = category;
    this.title = title;
    this.points = points;
    this.userScore = userScore;
    this.numSubmissions = numSubmissions;
    this.clearedSubmissions = clearedSubmissions;
    this.publishingState = PublishingState.PUBLISHED;
    this.language = language;
    this.status = status;
    this.runDuration = runDuration;
    this.memoryUsage = memoryUsage;
    this.userSubmissions = userSubmissions;
  }

  /**
   * Constructs a new ProfileProblem.
   *
   * @param link               The link to this problem.
   * @param category           This problem's category.
   * @param title              This problem's title.
   * @param points             The amount of points this
   *                           problem is worth.
   * @param userScore          The amount of points the user
   *                           scored on this problem.
   * @param numSubmissions     This problem's number of total
   *                           submissions.
   * @param clearedSubmissions This problem's number of
   *                           cleared submissions.
   * @param publishingState    What current editing state this
   *                           problem is at.
   */
  public ProfileProblem(
    String link,
    Category category,
    String title,
    int points,
    int userScore,
    int numSubmissions,
    int clearedSubmissions,
    Language language,
    ExecutionStatus status,
    double runDuration,
    double memoryUsage,
    int userSubmissions,
    PublishingState publishingState
  ) {
    this.link = link;
    this.category = category;
    this.title = title;
    this.points = points;
    this.userScore = userScore;
    this.numSubmissions = numSubmissions;
    this.clearedSubmissions = clearedSubmissions;
    this.publishingState = publishingState;
    this.language = language;
    this.status = status;
    this.runDuration = runDuration;
    this.memoryUsage = memoryUsage;
    this.userSubmissions = userSubmissions;
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
   * Retrieves this problem's category.
   *
   * @return this problem's category.
   */
  public Category getCategory() {
    return this.category;
  }

  /**
   * Retrieves this problem's title.
   *
   * @return this problem's title.
   */
  public String getTitle() {
    return this.title;
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
   * Retrieves this problem's total amount of submissions.
   *
   * @return this problem's total amount of submissions.
   */
  public int getNumSubmissions() {
    return this.numSubmissions;
  }

  /**
   * Retrieves this problem's number of cleared submissions.
   *
   * @return this problem's number of cleared submissions.
   */
  public int getClearedSubmissions() {
    return this.clearedSubmissions;
  }

  /**
   * Retrieves this problem's user's score.
   *
   * @return this problem's user's score.
   */
  public int getUserScore() {
    return this.userScore;
  }

  /**
   * Retrieves this problem's clear rate.
   * <p>
   * If no submissions have been made, the clear rate is
   * considered 0%.
   *
   * @return this problem's clear rate.
   */
  public int getClearRate() {
    if (this.numSubmissions == 0) {
      return 0;
    }

    return (this.clearedSubmissions/this.numSubmissions)*100;
  }

  /**
   * Retrieves this problem's publishing state.
   *
   * @return this problem's publishing state.
   */
  public PublishingState getState() {
    return this.publishingState;
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



  public int getUserSubmissions() {
    return this.userSubmissions;
  }



}
