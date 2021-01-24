package entities;

import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * An entity designed to represent specific problems.
 * <p>
 * Created on 2021.01.07.
 *
 * @author Shari Sun, Candice Zhang, Joseph Wang
 * @version 1.0.0
 * @since 1.0.0
 */
public abstract class Problem {
  /** This problem's problem type. */
  private ProblemType problemType;
  /** This problem's category. */
  private Category category;
  /** This id of the creator of this problem. */
  private long creatorId;
  /** The time this problem was created at. */
  private Timestamp createdAt;
  /** The time this problem was last modified at. */
  private Timestamp lastModifiedAt;
  /** This problem's title. */
  private String title;
  /** This problem's description. */
  private String description;
  /** The amount of points this problem is worth. */
  private int points;
  /** This problem's time limit, in ms. */
  private int timeLimitMillis;
  /** This problem's memory limit, in kB. */
  private int memoryLimitKb;
  /** This problem's output limit, in kB. */
  private int outputLimitKb;
  /** This problem's number of total submissions. */
  private int numSubmissions;
  /** This problem's number of cleared submissions. */
  private int clearedSubmissions;

  private ArrayList<Entity<Batch>> batches;

  private PublishingState state;

  /**
   * Constructs a new Problem.
   *
   * @param problemType        the type of this problem.
   * @param category           the category this problem is in.
   * @param creatorId          the id of the creator of this problem.
   * @param createdAt          the time this problem was created.
   * @param lastModifiedAt     the time this problem was last modified.
   * @param title              the title of this problem.
   * @param description        the description of this problem.
   * @param points             the amount of points this problem is worth.
   * @param timeLimitMillis    this problem's time limit, in milliseconds.
   * @param memoryLimitKb      this problem's memory limit, in kilobytes.
   * @param outputLimitKb      this problem's output limit, in kilobytes.
   * @param numSubmissions     this problem's amount of total submissions.
   * @param clearedSubmissions this problem's amount of cleared submissions.
   */
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
    int clearedSubmissions,
    PublishingState state
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
    this.state = state;
  }


  /**
   * Constructs a new Problem.
   *
   * @param problemType        the type of this problem.
   * @param category           the category this problem is in.
   * @param creatorId          the id of the creator of this problem.
   * @param createdAt          the time this problem was created.
   * @param lastModifiedAt     the time this problem was last modified.
   * @param title              the title of this problem.
   * @param description        the description of this problem.
   * @param points             the amount of points this problem is worth.
   * @param timeLimitMillis    this problem's time limit, in milliseconds.
   * @param memoryLimitKb      this problem's memory limit, in kilobytes.
   * @param outputLimitKb      this problem's output limit, in kilobytes.
   * @param numSubmissions     this problem's amount of total submissions.
   * @param clearedSubmissions this problem's amount of cleared submissions.
   * @param batches            this batches in this problem.
   */
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
    int clearedSubmissions,
    ArrayList<Entity<Batch>> batches,
    PublishingState state
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
    this.batches = batches;
    this.state = state;
  }

  /**
   * Retrieves this problem's problem type.
   *
   * @return this problem's problem type.
   */
  public ProblemType getProblemType() {
    return this.problemType;
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
   * Retrieves this problem's creator's id.
   *
   * @return this problem's creator's id.
   */
  public long getCreatorId() {
    return this.creatorId;
  }

  /**
   * Retrieves when this problem was created.
   *
   * @return a {@code Timestamp} with when this problem was created.
   */
  public Timestamp getCreatedAt() {
    return this.createdAt;
  }

  /**
   * Retrieves when this problem was last modified.
   *
   * @return a {@code Timestamp} with when this problem was last modified.
   */
  public Timestamp getLastModifiedAt() {
    return this.lastModifiedAt;
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
   * Retrieves this problem's description.
   *
   * @return this problem's description.
   */
  public String getDescription() {
    return this.description;
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
   * Retrieves this problem's time limit in ms.
   *
   * @return this problem's time limit in ms.
   */
  public int getTimeLimitMillis() {
    return this.timeLimitMillis;
  }

  /**
   * Retrieves this problem's memory limit in kB.
   *
   * @return this problem's memory limit in kB.
   */
  public int getMemoryLimitKb() {
    return this.memoryLimitKb;
  }

  /**
   * Retrieves this problem's output limit in kB.
   *
   * @return this problem's output limit in kB.
   */
  public int getOutputLimitKb() {
    return this.outputLimitKb;
  }

  public void setBatches(ArrayList<Entity<Batch>> batches) {
    this.batches = batches;
  }

  public ArrayList<Entity<Batch>> getBatches() {
    return this.batches;
  }

  public PublishingState getPublishingState() {
    return this.state;
  }

}
