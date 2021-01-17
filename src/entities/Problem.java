package entities;

import java.util.ArrayList;

public abstract class Problem {
  private ProblemType problemType;
  private int points;
  private int numSubmissions;
  private int clearedSubmissions;
  private int timeLimitMillis;
  private int memoryLimitKb;
  private int outputLimitKb;
  private ArrayList<Batch> batches;

  public Problem(
    ProblemType problemType,
    int points,
    int numSubmissions,
    int clearedSubmissions,
    int timeLimitMillis,
    int memoryLimitKb,
    int outputLimitKb
  ) {
    this.problemType = problemType;
    this.points = points;
    this.numSubmissions = numSubmissions;
    this.clearedSubmissions = clearedSubmissions;
    this.timeLimitMillis = timeLimitMillis;
    this.memoryLimitKb = memoryLimitKb;
    this.outputLimitKb = outputLimitKb;
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

  public int getTimeLimitMillis() {
    return this.timeLimitMillis;
  }

  public int getMemoryLimitKb() {
    return this.memoryLimitKb;
  }

  public int getOutputLimitKb() {
    return this.outputLimitKb;
  }

}
