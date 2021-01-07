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
public class Clarification {
  private long problemId;
  private long userId;
  private String message;
  private String response;


  public Clarification(long problemId, long userId, String message, String response) {
    this.problemId = problemId;
    this.userId = userId;
    this.message = message;
    this.response = response;
  }

  public long getProblemId() {
    return this.problemId;
  }

  public long getUserId() {
    return this.userId;
  }

  public String getMessage() {
    return this.message;
  }

  public String getResponse() {
    return this.response;
  }

}
