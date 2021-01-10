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
public class Submission {
  private long problemId;
  private long userId;
  private String code;
  private Language language;
  private Timestamp createdAt;
  private ExecutionStatus status;
  private int score;
  private long runDurationMillis;



  public Submission(
    long problemId,
    long userid,
    String code,
    Language language,
    Timestamp createdAt,
    ExecutionStatus status,
    int score,
    long runDurationMillis
  ) {
    this.problemId = problemId;
    this.userId = userid;
    this.code = code;
    this.language = language;
    this.createdAt = createdAt;
    this.status = status;
    this.score = score;
    this.runDurationMillis = runDurationMillis;
  }

  public long getProblemId() {
    return this.problemId;
  }

  public long getUserid() {
    return this.userId;
  }

  public String getCode() {
    return this.code;
  }

  public Language getLanguage() {
    return this.language;
  }

  public Timestamp getCreatedAt() {
    return this.createdAt;
  }

  public ExecutionStatus getStatus() {
    return this.status;
  }

  public int getScore() {
    return this.score;
  }

  public long getRunDurationMillis() {
    return this.runDurationMillis;
  }


}
