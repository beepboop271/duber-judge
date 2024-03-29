package entities;

import java.sql.Timestamp;

/**
 * An entity representing a submission to a problem.
 * <p>
 * Created on 2021.01.07.
 *
 * @author Shari Sun, Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */
public class Submission {
  /** The associated problem's id. */
  private long problemId;
  /** The id of the user who submitted. */
  private long userId;
  /** The code in the submission. */
  private String code;
  /** The language used for submission. */
  private Language language;
  /** When the submission was created. */
  private Timestamp createdAt;


  /**
   * Constructs a new Submission.
   *
   * @param problemId         the associated problem's id.
   * @param userId            the id of the user who submitted.
   * @param code              the code in the submission.
   * @param language          the language used for submission.
   * @param createdAt         when the submission was created.
   */
  public Submission(
    long problemId,
    long userId,
    String code,
    Language language,
    Timestamp createdAt
  ) {
    this.problemId = problemId;
    this.userId = userId;
    this.code = code;
    this.language = language;
    this.createdAt = createdAt;
  }

  /**
   * Retrieves the associated problem's id.
   *
   * @return the associated problem's id.
   */
  public long getProblemId() {
    return this.problemId;
  }

  /**
   * Retrieves the associated user's id.
   *
   * @return the associated user's id.
   */
  public long getUserId() {
    return this.userId;
  }

  /**
   * Retrieves the code in the submission.
   *
   * @return the code in the submission.
   */
  public String getCode() {
    return this.code;
  }

  /**
   * Retrieves the language used for this submission.
   *
   * @return the language used for this submission.
   */
  public Language getLanguage() {
    return this.language;
  }

  /**
   * Retrieves the time this submission was created at.
   *
   * @return the time this submission was created at.
   */
  public Timestamp getCreatedAt() {
    return this.createdAt;
  }
}
