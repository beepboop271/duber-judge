package entities;

import java.util.ArrayList;

public abstract class Problem {
  private ProblemType problemType;
  private int points;
  private int numSubmissions;
  private int clearedSubmissions;
  private long timeLimitMills;
  private ArrayList<Batch> batches;

  public Problem(
    ProblemType problemType,
    int points,
    int numSubmissions,
    int clearedSubmissions,
    long timeLimitMills
  ) {
    this.problemType = problemType;
    this.points = points;
    this.numSubmissions = numSubmissions;
    this.clearedSubmissions = clearedSubmissions;
    this.timeLimitMills = timeLimitMills;
    this.batches = new ArrayList<Batch>();
  }

  public void addBatch(Batch batchToAdd) {
    this.batches.add(batchToAdd);
  }
  
  public Batch[] getBatches() {
    Batch[] batchArr = new Batch[this.batches.size()];
    return this.batches.toArray(batchArr);
  }
  
  public ProblemType getProblemType() {
    return this.problemType;
  }

  public int getPoints() {
    return this.points;
  }

  public int getNumSubmissions() {
    return this.numSubmissions;
  }

  public int getClearedSubmissions() {
    return this.clearedSubmissions;
  }

  public long getTimeLimitMills() {
    return this.timeLimitMills;
  }

}
