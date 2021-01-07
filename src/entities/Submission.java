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
  private long userid;
  private String code;
  private Language language;
  private Timestamp createdAt;
  private ExecutionStatus status;
  private int score;
  private double runDuration;



  public Submission(
    long problemId,
    long userid,
    String code,
    Language language,
    Timestamp createdAt,
    ExecutionStatus status,
    int score,
    double runDuration
  ) {
    this.problemId = problemId;
    this.userid = userid;
    this.code = code;
    this.language = language;
    this.createdAt = createdAt;
    this.status = status;
    this.score = score;
    this.runDuration = runDuration;
  }

  public long getProblemId() {
    return this.problemId;
  }

  public long getUserid() {
    return this.userid;
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

  public double getRunDuration() {
    return this.runDuration;
  }


}
