package judge;

import java.io.File;
import java.sql.Timestamp;

import entities.Batch;
import entities.ContestProblem;
import entities.Language;
import entities.Problem;
import entities.Submission;
import entities.Testcase;

public class Main {
  public static void main(String[] args) {
    // input format:
    // char
    // string
    // output the occurrences of char in string
    Problem p1 = new ContestProblem(10, 0, 0, 1000*1, 5);

    Batch p1b1 = new Batch(5);
    p1b1.addTestcase(
      new Testcase(
        0,
        "a\nasdasdasd",
        "3"
      )
    );
    p1.addBatch(p1b1);
    
    Batch p1b2 = new Batch(3);
    p1b2.addTestcase(
      new Testcase(
        0,
        "a\nasdasdasd",
        "3"
      )
    );
    p1b2.addTestcase(
      new Testcase(
        0,
        "a\nasdasdasd",
        "3"
      )
    );
    p1b2.addTestcase(
      new Testcase(
        0,
        "a\nasdasdasd",
        "3"
      )
    );
    p1.addBatch(p1b2);

    Batch p1b3 = new Batch(2);
    p1b3.addTestcase(
      new Testcase(
        1,
        "A\nAsdasdasd",
        "1"
      )
    );
    p1b3.addTestcase(
      new Testcase(
        2,
        "D\nasdasdasfasfnakjhiahioqnfkjansvkjasnvkajnskcnjaskbcksabkxbasjkxnaskjxnjasnjxnasknxnjksxnkanxjksxnkjasxkasnxnasjkxnaskjnckjasvjsabvausdhasd",
        "0"
      )
    );    
    p1.addBatch(p1b3);

    Submission s1 = new Submission(
      p1,
      "char = input()\nstring = input()\nprint(string.count(char))",
      Language.PYTHON,
      new Timestamp(System.currentTimeMillis())
    );

    Submission s2 = new Submission(
      p1,
      "a = 1",
      Language.PYTHON,
      new Timestamp(System.currentTimeMillis())
    );

    Submission s3 = new Submission(
      p1,
      "while True:\n\ta = 1",
      Language.PYTHON,
      new Timestamp(System.currentTimeMillis())
    );

    Submission s4 = new Submission(
      p1,
      "char = input()\nstring = input()\nprint(string.count(char))",
      Language.JAVA,
      new Timestamp(System.currentTimeMillis())
    );

    File directory = new File("cache/judge/");
    if (!directory.exists()) {
      directory.mkdirs();
    } //TODO: move this to launcher or sth
    Judger judger = new Judger(
      Runtime.getRuntime().availableProcessors(),
      directory
    );
    judger.judge(s1);
    judger.judge(s2);
    judger.judge(s3);
    judger.judge(s4);
    judger.shutdown();
  }
}
