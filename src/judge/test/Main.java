package judge.test;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;

import entities.Batch;
import entities.ContestProblem;
import entities.Language;
import entities.Problem;
import entities.Submission;
import entities.Testcase;
import entities.TestcaseRun;
import judge.entities.Judger;
import judge.services.GlobalChildProcessService;

public class Main {

  public static ArrayList<TestcaseRun> runs = new ArrayList<TestcaseRun>();
  public static ArrayList<Submission> submissions = new ArrayList<Submission>();

  public static void main(String[] args) {
    GlobalChildProcessService.initialize();
    // input format:
    // char
    // string
    // output the occurrences of char in string
    Problem p1 = new ContestProblem(10, 0, 0, 1000*5, 1024*100, 1, 5);

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

    // should receive ALL_CLEAR
    Submission s1 = new Submission(
      p1,
      "char = input()\nstring = input()\nprint(string.count(char))",
      Language.PYTHON,
      new Timestamp(System.currentTimeMillis())
    );

    // should receive WRONG_ANSWER with score 0
    Submission s2 = new Submission(
      p1,
      "a = 1",
      Language.PYTHON,
      new Timestamp(System.currentTimeMillis())
    );

    // should receive WRONG_ANSWER, but partial points
    Submission s3 = new Submission(
      p1,
      "char = input()\n"
      + "string = input()\n"
      + "if len(string) < 10:\n"
      + "\tprint(string.count(char))",
      Language.PYTHON,
      new Timestamp(System.currentTimeMillis())
    );

    // should receive OUTPUT_LIMIT_EXCEEDED
    Submission s4 = new Submission(
      p1,
      "input()\n"
      +"input()\n"
      +"for i in range(100):"
      + "\tprint('hello im a very long string', end='')",
      Language.PYTHON,
      new Timestamp(System.currentTimeMillis())
    );

    // should receive TIME_LIMIT_EXCEEDED
    Submission s5 = new Submission(
      p1,
      "while True:\n\ta = 1",
      Language.PYTHON,
      new Timestamp(System.currentTimeMillis())
    );

    // should receive COMPILE_ERROR
    Submission s6 = new Submission(
      p1,
      "char = input()\nstring = input()\nprint(string.count(char))",
      Language.JAVA,
      new Timestamp(System.currentTimeMillis())
    );

    // should receive MEMORY_LIMIT_EXCEEDED
    Submission s7 = new Submission(
      p1,
      "input()\n"
      +"input()\n"
      +"thing = []\n"
      +"while True:\n"
      +"\tthing.append('aaaaaa')",
      Language.PYTHON,
      new Timestamp(System.currentTimeMillis())
    );

    // should receive ILLEGAL_CODE
    Submission s8 = new Submission(
      p1,
      "import os",
      Language.PYTHON,
      new Timestamp(System.currentTimeMillis())
    );

    File directory = new File("cache/judge/");
    Judger judger = new Judger(
      Runtime.getRuntime().availableProcessors(),
      directory
    );
    ArrayList<Submitter> submitters = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      submitters.add(new Submitter(s1, judger));
    }
    for (int i = 0; i < 3; i++) {
      submitters.add(new Submitter(s2, judger));
    }
    for (int i = 0; i < 3; i++) {
      submitters.add(new Submitter(s3, judger));
    }
    for (int i = 0; i < 3; i++) {
      submitters.add(new Submitter(s4, judger));
    }
    for (int i = 0; i < 3; i++) {
      submitters.add(new Submitter(s5, judger));
    }
    for (int i = 0; i < 3; i++) {
      submitters.add(new Submitter(s6, judger));
    }
    submitters.add(new Submitter(s7, judger));
    submitters.add(new Submitter(s8, judger));
    System.out.println("starting to judge");
    long start = System.currentTimeMillis();
    for (Submitter s : submitters) {
      s.run();
    }
    // judger.judge(s1);
    // judger.judge(s2);
    // judger.judge(s3);
    // judger.judge(s4);
    // judger.judge(s5);
    // judger.judge(s6);
    // judger.judge(s7);
    // judger.judge(s8);
    long end = System.currentTimeMillis();
    System.out.println("judging done, time: " + (end-start)/1000.0 + "seconds");
    judger.shutdown();
    GlobalChildProcessService.shutdown();

    // for (Submission submission : Main.submissions) {
    //   Judger.display(submission);
    // }
  }
}
