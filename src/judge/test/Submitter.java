package judge.test;

import entities.Entity;
import entities.Problem;
import entities.Submission;
import entities.SubmissionResult;
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
    Judger.display(result);
  }
}
