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
    return this.testcases.toArray(new Testcase[this.testcases.size()]);
  }

  public int getPoints() {
    return this.points;
  }

}
