package entities;

import java.sql.Timestamp;

/**
 * An entity designed to represent a specific contest problem, which
 * is part of a contest and limits the amount of submissions you can make.
 * <p>
 * Created on 2021.01.07.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class ContestProblem extends Problem {
  /** The amount of submissions a user can submit. */
  private int submissionsLimit;
  /** The id of the contest that contains this problem. */
  private long contestId;

  /**
   * Constructs a new ContestProblem.
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
   * @param submissionsLimit   the amount of submissions a user can submit.
   * @param contestId          the id of the contest that contains this problem.
   */
  public ContestProblem(
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
    int submissionsLimit,
    long contestId
  ) {
    super(
      ProblemType.CONTEST,
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
      clearedSubmissions
    );

    this.submissionsLimit = submissionsLimit;
    this.contestId = contestId;
  }

  /**
   * Retrieves this problem's submission limit.
   * 
   * @return this problem's submission limit.
   */
  public int getSubmissionsLimit() {
    return this.submissionsLimit;
  }

  /**
   * Retrieves this problem's associated contest id.
   * 
   * @return this problem's associated contest id.
   */
  public long getContestId() {
    return this.contestId;
  }


}
