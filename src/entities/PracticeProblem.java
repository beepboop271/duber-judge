package entities;

import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * An entity designed to represent a practice problem, which is a problem with an editorial.
 * <p>
 * An editorial is a solution and guide for solving the problem.
 * <p>
 * Created on 2021.01.07.
 *
 * @author Shari Sun, Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */
public class PracticeProblem extends Problem {
  /** The editorial for this problem. */
  private String editorial;

  /**
   * Constructs a new PracticeProblem.
   *
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
   * @param editorial          the editorial for this problem.
   */
  public PracticeProblem(
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
    String editorial,
    PublishingState state
  ) {
    super(
      ProblemType.PRACTICE,
      category,
      creatorId,
      createdAt,
      lastModifiedAt,
      title,
      description,
      points,
      timeLimitMillis,
      memoryLimitKb,
      outputLimitKb,
      numSubmissions,
      clearedSubmissions,
      state
    );

    this.editorial = editorial;
  }

  /**
   * Constructs a new PracticeProblem.
   *
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
   * @param editorial          the editorial for this problem.
   * @param batches            the batches.
   */
  public PracticeProblem(
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
    String editorial,
    ArrayList<Entity<Batch>> batches,
    PublishingState state
  ) {
    super(
      ProblemType.PRACTICE,
      category,
      creatorId,
      createdAt,
      lastModifiedAt,
      title,
      description,
      points,
      timeLimitMillis,
      memoryLimitKb,
      outputLimitKb,
      numSubmissions,
      clearedSubmissions,
      batches,
      state
    );

    this.editorial = editorial;
  }

  /**
   * Retrieves this practice problem's editorial,
   * which is a solution and guide for solving this problem.
   *
   * @return this practice problem's editorial.
   */
  public String getEditorial() {
    return this.editorial;
  }
}
