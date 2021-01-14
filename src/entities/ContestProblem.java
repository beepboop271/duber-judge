package entities;

public class ContestProblem extends Problem {

  private int submissionsLimit;

  public ContestProblem(
    int points,
    int numSubmissions,
    int clearedSubmissions,
    long timeLimitMils,
    int submissionsLimit
  ) {
    super(
      ProblemType.CONTEST,
      points,
      numSubmissions,
      clearedSubmissions,
      timeLimitMils
    );

    this.submissionsLimit = submissionsLimit;
  }

  public int getSubmissionsLimit() {
    return this.submissionsLimit;
  }

}
