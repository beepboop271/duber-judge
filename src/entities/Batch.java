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
  private int order;
  private int points;


  public Batch(long problemId, int order, int points) {
    this.problemId = problemId;
    this.order = order;
    this.points = points;
  }

  public long getProblemId() {
    return this.problemId;
  }

  public int getOrder() {
    return this.order;
  }

  public int getPoints() {
    return this.points;
  }


}
