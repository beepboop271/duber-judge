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
    Timestamp lastModified,
    String title,
    String description,
    int points,
    int numSubmissions,
    int clearedSubmissions,
    int timeLimit,
    int submissionsLimit,
    long contestId
  ) {
    super(
      ProblemType.CONTEST,
      category,
      creatorId,
      createdAt,
      lastModified,
      title,
      description,
      points,
      numSubmissions,
      clearedSubmissions,
      timeLimit
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
