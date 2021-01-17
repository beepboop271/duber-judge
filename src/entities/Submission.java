package entities;

import java.sql.Timestamp;

public class Submission {
  private Problem problem;
  private String code;
  private Language language;
  private ExecutionStatus status;
  private int score;
  private int runDurationMillis;
  private double memoryUsageKb;

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
    this.runDurationMillis = 0;
  }

  public Submission(
    Problem problem,
    String code,
    Language language,
    Timestamp createdAt,
    ExecutionStatus status,
    int score,
    int runDurationMillis,
    int memoryUsageKb
  ) {
    this.problem = problem;
    this.code = code;
    this.language = language;
    this.status = status;
    this.score = score;
    this.runDurationMillis = runDurationMillis;
    this.memoryUsageKb = memoryUsageKb;
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

  public int getRunDurationMillis() {
    return this.runDurationMillis;
  }

  public void setRunDuration(int runDurationMillis) {
    this.runDurationMillis = runDurationMillis;
  }

  public double getMemoryUsageKb() {
    return this.memoryUsageKb;
  }

  public void setMemoryUsageKb(double memoryUsageKb) {
    this.memoryUsageKb = memoryUsageKb;
  }

}
