package judge.test;

import java.io.File;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;

import entities.Batch;
import entities.ContestProblem;
import entities.Entity;
import entities.Language;
import entities.Problem;
import entities.Submission;
import entities.Testcase;
import judge.ChildProcesses;
import judge.Judger;

public class Main {
  public static void main(String[] args) {
    ChildProcesses.initialize();
    // input format:
    // char
    // string
    // output: the occurrences of char in string
    Problem p1 =
      new ContestProblem(
        null, 0L, null, null, null, null, 10, 5000, 100*1024, 1, 0, 5, 0L, 0,
        Arrays.asList(
          new Entity<>(0, new Batch(
            0L, 0L, 1, 5,
            Arrays.asList(
              new Entity<>(0, new Testcase(0, "a\nasdasdasd", "3"))
            )
          )),
          new Entity<>(0, new Batch(
            0L, 0L, 2, 3,
            Arrays.asList(
              new Entity<>(0, new Testcase(0, "a\nasdasdasd", "3")),
              new Entity<>(0, new Testcase(0, "a\nasdasdasd", "3")),
              new Entity<>(0, new Testcase(0, "a\nasdasdasd", "3"))
            )
          )),
          new Entity<>(0, new Batch(
            0L, 0L, 3, 2,
            Arrays.asList(
              new Entity<>(0, new Testcase(1, "A\nAsdasdasd", "1")),
              new Entity<>(0, new Testcase(2, "D\nasdasdasfasfnakjhiahioqnfkjansvkjasnvkajnskcnjaskbcksabkxbasjkxnaskjxnjasnjxnasknxnjksxnkanxjksxnkjasxkasnxnasjkxnaskjnckjasvjsabvausdhasd", "0"))
            )
          ))
        ),
        null
      );

    // should receive ALL_CLEAR
    Submission s1 = new Submission(
      "char = input()\nstring = input()\nprint(string.count(char))",
      Language.PYTHON,
      new Timestamp(System.currentTimeMillis())
    );
    Submission s2 = new Submission(
      "import java.util.Scanner;\n"
      +"public class Main {\n"
      +"\tpublic static void main(String[] args) {\n"
      +"\t\tScanner input = new Scanner(System.in);\n"
      +"\t\tString needle = input.nextLine();\n"
      +"\t\tString haystack = input.nextLine();\n"
      +"\t\tSystem.out.println(haystack.split(needle).length-1);\n"
      +"\t\tinput.close();\n"
      +"\t}\n"
      +"}\n",
      Language.JAVA,
      new Timestamp(System.currentTimeMillis())
    );

    // should receive WRONG_ANSWER, but partial points
    Submission s3 = new Submission(
      "char = input()\n"
      + "string = input()\n"
      + "if len(string) < 10:\n"
      + "\tprint(string.count(char))",
      Language.PYTHON,
      new Timestamp(System.currentTimeMillis())
    );

    // should receive TIME_LIMIT_EXCEEDED
    Submission s4 = new Submission(
      "while True:\n\ta = 1",
      Language.PYTHON,
      new Timestamp(System.currentTimeMillis())
    );

    // should receive COMPILE_ERROR
    Submission s5 = new Submission(
      "char = input()\nstring = input()\nprint(string.count(char))",
      Language.JAVA,
      new Timestamp(System.currentTimeMillis())
    );

    File directory = new File("temp/judge/");
    Judger judger = new Judger(
      Runtime.getRuntime().availableProcessors()+1,
      directory
    );
    ArrayList<Submitter> submitters = new ArrayList<>();
    submitters.add(new Submitter(s1, p1, judger));
    submitters.add(new Submitter(s2, p1, judger));
    submitters.add(new Submitter(s3, p1, judger));
    submitters.add(new Submitter(s4, p1, judger));
    submitters.add(new Submitter(s5, p1, judger));
    System.out.println("starting to judge");
    ArrayList<Thread> threads = new ArrayList<>();
    for (Submitter s : submitters) {
      Thread t = new Thread(s);
      threads.add(t);
      t.start();
    }
    for (Thread t : threads) {
      try {
        t.join();
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }

    judger.shutdown();
    ChildProcesses.shutdown();
  }
}
