package entities;

import java.sql.Timestamp;

/**
 * [description]
 * <p>
 * Created on 2021.01.07.
 *
 * @author Shari Sun, Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public abstract class Problem {
  private ProblemType problemType;
  private Category category;
  private long creatorId;
  private Timestamp createdAt;
  private Timestamp lastModifiedAt;
  private String title;
  private String description;
  private int points;
  private int timeLimitMillis;
  private int memoryLimitKb;
  private int outputLimitKb;
  private int numSubmissions;
  private int clearedSubmissions;



  public Problem(
    ProblemType problemType,
    Category category,
    long creatorId,
    Timestamp createdAt,
    Timestamp lastModifiedAt,
    String title,
    String description,
    int points,
    int timeLimitMillis,
    int memoryLimitKb,
    int outputLimitKb,
    int numSubmissions,
    int clearedSubmissions
  ) {
    this.problemType = problemType;
    this.category = category;
    this.creatorId = creatorId;
    this.createdAt = createdAt;
    this.lastModifiedAt = lastModifiedAt;
    this.title = title;
    this.description = description;
    this.points = points;
    this.timeLimitMillis = timeLimitMillis;
    this.memoryLimitKb = memoryLimitKb;
    this.outputLimitKb = outputLimitKb;
    this.numSubmissions = numSubmissions;
    this.clearedSubmissions = clearedSubmissions;
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

  public Timestamp getLastModifiedAt() {
    return this.lastModifiedAt;
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

  public int getTimeLimitMillis() {
    return this.timeLimitMillis;
  }

  public int getMemoryLimitKb() {
    return this.memoryLimitKb;
  }

  public int getOutputLimitKb() {
    return this.outputLimitKb;
  }

}
