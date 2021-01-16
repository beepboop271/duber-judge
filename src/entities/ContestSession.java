package entities;

import java.sql.Timestamp;

/**
 * An entity designed to represent a contest session.
 * <p>
 * Created on 2021.01.07.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class ContestSession {
  /** The contest id of the associated contest. */
  private long contestId;
  /** The user id of the associated user. */
  private long userId;
  /** When the user started the contest. */
  private Timestamp startedAt;
  /** The user's current status for the contest. */
  private ContestStatus status;
  /** The user's current score for the contest. */
  private int score;

  /**
   * Constructs a new ContestSession.
   * 
   * @param contestId the contest id of the associated contest.
   * @param userId    the user id of the associated user.
   * @param startedAt when the user started the contest.
   * @param status    the user's current for during the contest.
   * @param score     the user's current score for the contest.
   */
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

  /**
   * Retrieves the associated contest's id.
   * 
   * @return the associated contest's id.
   */
  public long getContestId() {
    return this.contestId;
  }

  /**
   * Retrieves the associated user's id.
   * 
   * @return the associated user's id.
   */
  public long getUserId() {
    return this.userId;
  }

  /**
   * Retrieves when the user started the contest.
   * 
   * @return when the user started the contest.
   */
  public Timestamp getStartedAt() {
    return this.startedAt;
  }

  /**
   * Retrieves the user's current contest status.
   * 
   * @return the user's current contest status.
   */
  public ContestStatus getStatus() {
    return this.status;
  }

  /**
   * Retrieves the user's current contest score.
   * 
   * @return the user's current contest score.
   */
  public int getScore() {
    return this.score;
  }

}
