package entities;

import java.util.ArrayList;

/**
 * [description]
 * <p>
 * Created on 2021.01.21.
 *
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class SubmissionResult {
  private final Submission submission;

  private ExecutionStatus status;
  private int score;
  private int runDurationMillis;
  private long memoryUsedBytes;
  private ArrayList<TestcaseRun> testcaseRuns;

  public SubmissionResult(Submission submission) {
    this.submission = submission;
    this.status = ExecutionStatus.PENDING;
    this.score = 0;
    this.runDurationMillis = 0;
    this.memoryUsedBytes = 0;
    this.testcaseRuns = new ArrayList<TestcaseRun>();
  }

  public SubmissionResult(
    Submission submission,
    ExecutionStatus status,
    int score,
    int runDurationMillis,
    long memoryUsedBytes,
    ArrayList<TestcaseRun> testcaseRuns
  ) {
    this.submission = submission;
    this.status = status;
    this.score = score;
    this.runDurationMillis = runDurationMillis;
    this.memoryUsedBytes = memoryUsedBytes;
    this.testcaseRuns = testcaseRuns;
  }
  
  public void addTestcaseRun(TestcaseRun run) {
    this.testcaseRuns.add(run);
  }

  public void addRunDurationMillis(int durationToAdd) {
    this.runDurationMillis += durationToAdd;
  }

  public void addScore(int scoreToAdd) {
    this.score += scoreToAdd;
  }

  public void updateMemoryUsedBytes(long memoryUsedBytes) {
    this.memoryUsedBytes = Math.max(this.memoryUsedBytes, memoryUsedBytes);
  }

  public void updateStatus(ExecutionStatus incomingStatus) {
    // update only when the given status has higher priority
    if (incomingStatus.compareTo(this.status) < 0) { 
      this.status = incomingStatus;
    }
  }

  public Submission getSubmission() {
    return this.submission;
  }

  public ExecutionStatus getStatus() {
    return this.status;
  }

  public void setStatus(ExecutionStatus status) {
    this.status = status;
  }

  public int getScore() {
    return this.score;
  }

  public void setScore(int score) {
    this.score = score;
  }

  public int getRunDurationMillis() {
    return this.runDurationMillis;
  }

  public long getMemoryUsedBytes() {
    return this.memoryUsedBytes;
  }

  public ArrayList<TestcaseRun> getTestcaseRuns() {
    return this.testcaseRuns;
  }
}
