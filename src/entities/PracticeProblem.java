package entities;

public class PracticeProblem extends Problem {
  private String editorial;

  public PracticeProblem(
    int points,
    int numSubmissions,
    int clearedSubmissions,
    long timeLimitMills,
    String editorial
  ) {
    super(
      ProblemType.PRACTICE,
      points,
      numSubmissions,
      clearedSubmissions,
      timeLimitMills
    );

    this.editorial = editorial;
  }

  public String getEditorial() {
    return this.editorial;
  }
}
