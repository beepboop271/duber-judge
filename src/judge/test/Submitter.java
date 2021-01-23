package judge.test;

import entities.Submission;
import entities.SubmissionResult;
import judge.Judger;

public class Submitter implements Runnable {
  private Submission submission;
  private Judger judger;

  public Submitter(Submission submission, Judger judger) {
    this.submission = submission;
    this.judger = judger;
  }
  
  @Override
  public void run() {
    long start = System.currentTimeMillis();
    SubmissionResult result = judger.judge(submission);
    long end = System.currentTimeMillis();
    System.out.println(
      "Judging of submission " + submission.toString() + " done, duration: " + (end-start)
    );
    Judger.display(result);
  }
}
