package entities;

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

  public int getUserScore() {
    return this.userScore;
  }

  /**
   * Retrieves this problem's clear rate.
   *
   * @return this problem's clear rate.
   */
  public int getClearRate() {
    if (this.numSubmissions == 0) {
      return 0;
    }

    return (this.clearedSubmissions / this.numSubmissions) * 100;
  }
}
