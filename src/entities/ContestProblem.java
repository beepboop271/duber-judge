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
public class ContestProblem extends Problem {

  private int submissionsLimit;
  private long contestId;


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

  public int getSubmissionsLimit() {
    return this.submissionsLimit;
  }

  public long getContestId() {
    return this.contestId;
  }


}
