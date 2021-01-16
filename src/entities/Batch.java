package entities;

/**
 * An entity designed to represent a batch of test cases.
 * <p>
 * A problem contains multiple batches.
 * <p>
 * Created on 2021.01.07.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class Batch {
  /** The id of the problem that the batch belongs to */
  private long problemId;
  /** The sequence number of the batch. */
  private int sequence;
  /** The number of points the batch gives. */
  private int points;

  /**
   * Constructs a new Batch.
   * 
   * @param problemId the id of the problem that the batch belongs to
   * @param sequence  the sequence number of the batch.
   * @param points    the number of points the batch gives.
   */
  public Batch(long problemId, int sequence, int points) {
    this.problemId = problemId;
    this.sequence = sequence;
    this.points = points;
  }

  /**
   * Retrieves this batch's associated problem id.
   * 
   * @return this batch's associated problem id.
   */
  public long getProblemId() {
    return this.problemId;
  }

  /**
   * Retrieves this batch's sequence number.
   * 
   * @return this batch's sequence number.
   */
  public int getSequence() {
    return this.sequence;
  }

  /**
   * Retrieves the number of points this batch gives.
   * 
   * @return the number of points this batch gives.
   */
  public int getPoints() {
    return this.points;
  }

}