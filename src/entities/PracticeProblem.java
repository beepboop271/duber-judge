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

public class PracticeProblem extends Problem {
  private String editorial;

  public PracticeProblem(
    Category category,
    long creatorId,
    Timestamp createdAt,
    Timestamp lastModifiedAt,
    String title,
    String description,
    int points,
    int timeLimitMillis,
    int memoryLimitKb,
    int outputLimitKb,
    int numSubmissions,
    int clearedSubmissions,
    String editorial
  ) {
    super(
      ProblemType.PRACTICE,
      category,
      creatorId,
      createdAt,
      lastModifiedAt,
      title,
      description,
      points,
      timeLimitMillis,
      memoryLimitKb,
      outputLimitKb,
      numSubmissions,
      clearedSubmissions
    );

    this.editorial = editorial;
  }

  public String getEditorial() {
    return this.editorial;
  }
}
