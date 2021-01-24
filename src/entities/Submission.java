package entities;

import java.sql.Timestamp;

public class Submission {
  private String code;
  private Language language;
  private Timestamp createdAt;

  public Submission(
    String code,
    Language language,
    Timestamp createdAt
  ) {
    this.code = code;
    this.language = language;
    this.createdAt = createdAt;
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
