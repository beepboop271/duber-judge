package entities;

public class ContestProblem extends Problem {

  private int submissionsLimit;

  public ContestProblem(
    int points,
    int numSubmissions,
    int clearedSubmissions,
    long timeLimitMills,
    long outputLimitBytes,
    int submissionsLimit
  ) {
    super(
      ProblemType.CONTEST,
      points,
      numSubmissions,
      clearedSubmissions,
      timeLimitMills,
      outputLimitBytes
    );

    this.submissionsLimit = submissionsLimit;
  }

  public int getSubmissionsLimit() {
    return this.submissionsLimit;
  }

}
