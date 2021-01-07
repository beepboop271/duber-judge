package entities;

import java.sql.Date;

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
  private Date startTime;
  private Date endTime;

  public Contest(long creatorId, String description, String title, Date startTime, Date endTime) {
    this.creatorId = creatorId;
    this.description = description;
    this.title = title;
    this.startTime = startTime;
    this.endTime = endTime;
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

  public Date getStartTime() {
    return this.startTime;
  }

  public Date getEndTime() {
    return this.endTime;
  }



}
