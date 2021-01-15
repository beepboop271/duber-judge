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
public class Contest {
  private long creatorId;
  private String description;
  private String title;
  private Timestamp startTime;
  private Timestamp endTime;
  private int durationMinutes;

  public Contest(
    long creatorId,
    String description,
    String title,
    Timestamp startTime,
    Timestamp endTime,
    int durationMinutes
  ) {
    this.creatorId = creatorId;
    this.description = description;
    this.title = title;
    this.startTime = startTime;
    this.endTime = endTime;
    this.durationMinutes = durationMinutes;
  }

  public long getCreatorId() {
    return this.creatorId;
  }

  public String getDescription() {
    return this.description;
  }

  public String getTitle() {
    return this.title;
  }

  public Timestamp getStartTime() {
    return this.startTime;
  }

  public Timestamp getEndTime() {
    return this.endTime;
  }

  public int getDurationMinutes() {
    return this.durationMinutes;
  }


}
