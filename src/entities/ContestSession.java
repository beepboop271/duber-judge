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
public class ContestSession {
  private long contestId;
  private long userId;
  private Timestamp startedAt;
  private ContestStatus status;
  private int score;


  public ContestSession(
    long contestId,
    long userId,
    Timestamp startedAt,
    ContestStatus status,
    int score
  ) {
    this.contestId = contestId;
    this.userId = userId;
    this.startedAt = startedAt;
    this.status = status;
    this.score = score;
  }

  public long getContestId() {
    return this.contestId;
  }

  public long getUserId() {
    return this.userId;
  }

  public Timestamp getStartedAt() {
    return this.startedAt;
  }

  public ContestStatus getStatus() {
    return this.status;
  }

  public int getScore() {
    return this.score;
  }

}
