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
    Timestamp lastModified,
    String title,
    String description,
    int points,
    int numSubmissions,
    int clearedSubmissions,
    int timeLimit,
    String editorial
  ) {
    super(
      ProblemType.PRACTICE,
      category,
      creatorId,
      createdAt,
      lastModified,
      title,
      description,
      points,
      numSubmissions,
      clearedSubmissions,
      timeLimit
    );

    this.editorial = editorial;
  }

  public String getEditorial() {
    return this.editorial;
  }
}
