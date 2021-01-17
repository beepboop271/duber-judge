package entities;

public class ContestProblem extends Problem {

  private int submissionsLimit;

  public ContestProblem(
    int points,
    int numSubmissions,
    int clearedSubmissions,
    int timeLimitMillis,
    int memoryLimitKb,
    int outputLimitKb,
    int submissionsLimit
  ) {
    super(
      ProblemType.CONTEST,
      points,
      numSubmissions,
      clearedSubmissions,
      timeLimitMillis,
      memoryLimitKb,
      outputLimitKb
    );

    this.submissionsLimit = submissionsLimit;
  }

  public int getSubmissionsLimit() {
    return this.submissionsLimit;
  }

}
