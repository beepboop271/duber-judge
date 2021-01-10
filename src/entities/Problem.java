package entities;

import java.sql.Timestamp;

/**
 * [description]
 * <p>
 * Created on 2021.01.07.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class Problem {
  private ProblemType problemType;
  private Category category;
  private long creatorId;
  private Timestamp createdAt;
  private Timestamp lastModified;
  private String title;
  private String description;
  private int points;
  private int numSubmissions;
  private int clearedSubmissions;
  private int timeLimit;



  public Problem(
    ProblemType problemType,
    Category category,
    long creatorId,
    Timestamp createdAt,
    Timestamp lastModified,
    String title,
    String description,
    int points,
    int numSubmissions,
    int clearedSubmissions,
    int timeLimit
  ) {
    this.problemType = problemType;
    this.category = category;
    this.creatorId = creatorId;
    this.createdAt = createdAt;
    this.lastModified = lastModified;
    this.title = title;
    this.description = description;
    this.points = points;
    this.numSubmissions = numSubmissions;
    this.clearedSubmissions = clearedSubmissions;
    this.timeLimit = timeLimit;
  }

  public ProblemType getProblemType() {
    return this.problemType;
  }

  public Category getCategory() {
    return this.category;
  }

  public long getCreatorId() {
    return this.creatorId;
  }

  public Timestamp getCreatedAt() {
    return this.createdAt;
  }

  public Timestamp getLastModified() {
    return this.lastModified;
  }

  public String getTitle() {
    return this.title;
  }

  public String getDescription() {
    return this.description;
  }

  public int getPoints() {
    return this.points;
  }

  public int getNumSubmissions() {
    return this.numSubmissions;
  }

  public int getClearedSubmissions() {
    return this.clearedSubmissions;
  }

  public int getTimeLimit() {
    return this.timeLimit;
  }

}
