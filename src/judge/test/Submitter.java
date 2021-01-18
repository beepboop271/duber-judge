package judge.test;

import entities.Submission;
import judge.entities.Judger;

public class Submitter implements Runnable{
  private Submission submission;
  private Judger judger;

  public Submitter(Submission submission, Judger judger) {
    this.submission = submission;
    this.judger = judger;
  }
  
  @Override
  public void run() {
    judger.judge(submission);
  }
}
