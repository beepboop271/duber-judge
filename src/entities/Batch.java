package entities;

import java.util.ArrayList;

public class Batch {
  private int points;
  private ArrayList<Testcase> testcases;

  public Batch(int points) {
    this.points = points;
    this.testcases = new ArrayList<Testcase>();
  }

  public void addTestcase(Testcase caseToAdd) {
    this.testcases.add(caseToAdd);
  }
  
  public Testcase[] getTestcases() {
    Testcase[] caseArr = new Testcase[this.testcases.size()];
    return this.testcases.toArray(caseArr);
  }

  public int getPoints() {
    return this.points;
  }

}
