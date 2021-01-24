package entities;

import java.util.ArrayList;

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
  private long creatorId;
  /** The sequence number of the batch. */
  private int sequence;
  /** The number of points the batch gives. */
  private int points;

  private ArrayList<Entity<Testcase>> testcases;

  /**
   * Constructs a new Batch.
   *
   * @param problemId the id of the problem that the batch belongs to
   * @param creatorId the id of the creator of this batch
   * @param sequence  the sequence number of the batch.
   * @param points    the number of points the batch gives.
   */
  public Batch(long problemId, long creatorId, int sequence, int points) {
    this.problemId = problemId;
    this.creatorId = creatorId;
    this.sequence = sequence;
    this.points = points;
  }


  /**
   * Constructs a new Batch.
   *
   * @param problemId the id of the problem that the batch belongs to
   * @param creatorId the id of the creator of this batch
   * @param sequence  the sequence number of the batch.
   * @param points    the number of points the batch gives.
   * @param testcases The testcases.
   */
  public Batch(
    long problemId,
    long creatorId,
    int sequence,
    int points,
    ArrayList<Entity<Testcase>> testcases
  ) {
    this.problemId = problemId;
    this.creatorId = creatorId;
    this.sequence = sequence;
    this.points = points;
    this.testcases = testcases;
  }

  /**
   * Retrieves this batch's associated problem id.
   *
   * @return this batch's associated problem id.
   */
  public long getProblemId() {
    return this.problemId;
  }

  public long getCreatorId() {
    return this.creatorId;
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


  public ArrayList<Entity<Testcase>> getTestcases() {
    return this.testcases;
  }

  public void setTestcases(ArrayList<Entity<Testcase>> testcases) {
    this.testcases = testcases;
  }

}