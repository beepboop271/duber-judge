package entities;

import java.sql.Timestamp;

/**
 * An entity designed to represent a contest, which has a time limit and contains problems.
 * <p>
 * Created on 2021.01.07.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class Contest {
  /** The contest creator's id. */
  private long creatorId;
  /** The description of this contest. */
  private String description;
  /** The title of this contest. */
  private String title;
  /** The start time of this contest. */
  private Timestamp startTime;
  /** The end time of this contest. */
  private Timestamp endTime;
  /** The duration of this contest, in minutes. */
  private int durationMinutes;
  /** The current status of the contest. */
  private ContestStatus status;

  private PublishingState publishingState;

  /**
   * Constructs a new Contest.
   *
   * @param creatorId       the contest creator's id.
   * @param description     the description of this contest.
   * @param title           the title of this contest.
   * @param startTime       the start time of this contest.
   * @param endTime         the end time of this contest.
   * @param durationMinutes the duration of this contest, in minutes.
   */
  public Contest(
    long creatorId,
    String description,
    String title,
    Timestamp startTime,
    Timestamp endTime,
    ContestStatus status,
    int durationMinutes,
    PublishingState publishingState
  ) {
    this.creatorId = creatorId;
    this.description = description;
    this.title = title;
    this.startTime = startTime;
    this.endTime = endTime;
    this.status = status;
    this.durationMinutes = durationMinutes;
    this.publishingState = publishingState;
  }

  /**
   * Retrieves the contest creator's id.
   *
   * @return the contest creator's id.
   */
  public long getCreatorId() {
    return this.creatorId;
  }

  /**
   * Retrieves the description of this contest.
   *
   * @return the description of this contest.
   */
  public String getDescription() {
    return this.description;
  }

  /**
   * Retrieves the title of this contest.
   *
   * @return the title of this contest.
   */
  public String getTitle() {
    return this.title;
  }

  /**
   * Retrieves the start time of this contest.
   *
   * @return the start time of this contest.
   */
  public Timestamp getStartTime() {
    return this.startTime;
  }

  /**
   * Retrieves the end time of this contest.
   *
   * @return the end time of this contest.
   */
  public Timestamp getEndTime() {
    return this.endTime;
  }

  /**
   * Retrieves this contest's duration, in minutes.
   *
   * @return this contest's duration, in minutes.
   */
  public int getDurationMinutes() {
    return this.durationMinutes;
  }


  public ContestStatus getStatus() {
    return this.status;
  }

  public PublishingState getPublishingState() {
    return this.publishingState;
  }

}
