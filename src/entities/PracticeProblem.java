package entities;

public class PracticeProblem extends Problem {
  private String editorial;

  public PracticeProblem(
    int points,
    int numSubmissions,
    int clearedSubmissions,
    int timeLimitMillis,
    int memoryLimitKb,
    int outputLimitKb,
    String editorial
  ) {
    super(
      ProblemType.PRACTICE,
      points,
      numSubmissions,
      clearedSubmissions,
      timeLimitMillis,
      memoryLimitKb,
      outputLimitKb
    );

    this.editorial = editorial;
  }

  public String getEditorial() {
    return this.editorial;
  }
}
