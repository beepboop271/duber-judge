package entities;

import java.sql.Timestamp;

public class Submission {
  private Problem problem;
  private String code;
  private Language language;
  private ExecutionStatus status;
  private int score;
  private long runDurationMills;

  public Submission(
    Problem problem,
    String code,
    Language language,
    Timestamp createdAt
  ) {
    this.problem = problem;
    this.code = code;
    this.language = language;
    this.status = ExecutionStatus.PENDING;
    this.score = 0;
    this.runDurationMills = 0;
  }

  public Submission(
    Problem problem,
    String code,
    Language language,
    Timestamp createdAt,
    ExecutionStatus status,
    int score,
    long runDurationMills
  ) {
    this.problem = problem;
    this.code = code;
    this.language = language;
    this.status = status;
    this.score = score;
    this.runDurationMills = runDurationMills;
  }
  
  public Problem getProblem() {
    return this.problem;
  }

  public String getCode() {
    return this.code;
  }

  public Language getLanguage() {
    return this.language;
  }

  public ExecutionStatus getStatus() {
    return this.status;
  }

  public void setStatus(ExecutionStatus status) {
    this.status = status;
  }

  public int getScore() {
    return this.score;
  }

  public void setScore(int score) {
    this.score = score;
  }

  public long getRunDurationMills() {
    return this.runDurationMills;
  }

  public void setRunDuration(long runDurationMills) {
    this.runDurationMills = runDurationMills;
  }

}
