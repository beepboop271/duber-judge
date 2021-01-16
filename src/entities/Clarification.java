package entities;

/**
 * An entity that represents a clarification to a problem.
 * <p>
 * Created on 2021.01.07.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class Clarification {
  /** The problem id this clarification is for. */
  private long problemId;
  /** The user id of who requested the clarification. */
  private long userId;
  /** The clarification message. */
  private String message;
  /** The clarification response. */
  private String response;

  /**
   * Constructs a new Clarification.
   * 
   * @param problemId the problem id this clarification is for.
   * @param userId    the user id of who requested the clarification.
   * @param message   the clarification message.
   * @param response  the clarification response, null if there is no response yet.
   */
  public Clarification(long problemId, long userId, String message, String response) {
    this.problemId = problemId;
    this.userId = userId;
    this.message = message;
    this.response = response;
  }

  /**
   * Retrieves this clarification's associated problem id.
   * 
   * @return this clarification's associated problem id.
   */
  public long getProblemId() {
    return this.problemId;
  }

  /**
   * Retrieves the user id of the user who requested the clarification.
   * 
   * @return the user id of the user who requested the clarification.
   */
  public long getUserId() {
    return this.userId;
  }

  /**
   * Retrieves the clarification message.
   * 
   * @return the clarification message.
   */
  public String getMessage() {
    return this.message;
  }

  /**
   * Retrieves the clarification response.
   * 
   * @return the clarification response.
   */
  public String getResponse() {
    return this.response;
  }

}
