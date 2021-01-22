package entities;

import java.sql.Timestamp;

public class Submission {
  private Problem problem;
  private String code;
  private Language language;
  private Timestamp createdAt;

  public Submission(
    Problem problem,
    String code,
    Language language,
    Timestamp createdAt
  ) {
    this.problem = problem;
    this.code = code;
    this.language = language;
    this.createdAt = createdAt;
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

  public Timestamp getCreatedAt() {
    return this.createdAt;
  }
}
