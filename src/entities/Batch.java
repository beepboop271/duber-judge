package entities;

/**
 * [description]
 * <p>
 * Created on 2021.01.07.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class Batch {
  private long problemId;
  private long creatorId;
  private int sequence;
  private int points;


  public Batch(long problemId, long creatorId, int sequence, int points) {
    this.problemId = problemId;
    this.creatorId = creatorId;
    this.sequence = sequence;
    this.points = points;
  }

  public long getProblemId() {
    return this.problemId;
  }

  public long getCreatorId() {
    return this.creatorId;
  }

  public int getSequence() {
    return this.sequence;
  }

  public int getPoints() {
    return this.points;
  }


}
