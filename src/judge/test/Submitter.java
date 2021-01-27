package judge.test;

import entities.Entity;
import entities.Problem;
import entities.Submission;
import entities.SubmissionResult;
import entities.TestcaseRun;
import judge.Judger;

public class Submitter implements Runnable {
  private Submission submission;
  private Problem problem;
  private Judger judger;

  public Submitter(Submission submission, Problem problem, Judger judger) {
    this.submission = submission;
    this.problem = problem;
    this.judger = judger;
  }

  public static synchronized void displaySubmissionResult(SubmissionResult result) {
    Submission submission = result.getSubmission();
    System.out.println("----------------------------------");
    System.out.println("Submission: " + submission);
    System.out.println("Language: " + submission.getLanguage());
    System.out.println("Status: " + result.getStatus());
    System.out.println("Score: " + result.getScore());
    System.out.println("Run duration (milliseconds): " + result.getRunDurationMillis());
    System.out.println("Memory used (bytes): " + result.getMemoryUsageBytes());
    System.out.println("Source Code:\n" + submission.getCode());
    System.out.println();
    for (TestcaseRun run : result.getTestcaseRuns()) {
      System.out.println("  Testcase run: " + run + " of batch " + run.getBatchId());
      System.out.println("  Status: " + run.getStatus());
      System.out.println("  Run duration (milliseconds): " + run.getRunDurationMillis());
      System.out.println("  Memory used (bytes): " + run.getMemoryUsageBytes());
      System.out.println("  Output: " + run.getOutput());
    }
    System.out.println("----------------------------------");
  }

  @Override
  public void run() {
    long start = System.currentTimeMillis();
    SubmissionResult result = judger.judge(
      new Entity<>(0, this.submission),
      new Entity<>(0, this.problem)
    );
    long end = System.currentTimeMillis();
    System.out.println(
      "Judging of submission " + this.submission.toString() + " done, duration: " + (end-start)
    );
    Submitter.displaySubmissionResult(result);
  }



  public static void display(SubmissionResult submission) {

  }

  public static void display(TestcaseRun run) {

  }
}
