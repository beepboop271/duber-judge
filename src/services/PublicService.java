package services;

import java.util.ArrayList;

import entities.Contest;
import entities.Entity;
import entities.User;

/**
 * Serves public information (client does not need to login).
 * <p>
 * Created on 2021.01.16.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class PublicService {
  /**
   * Get the contests ordered from latest to earliest.
   *
   * @param index            the current offset
   * @param numContests      the number of contests to retrieve
   * @return                 a list of contests
   */
  public ArrayList<Entity<Contest>> getContests(int index, int numContests) {

  }


  public ArrayList<Entity<User>> getLeaderboard(int index, int numUsers) {

  }


  public ArrayList<Entity<User>> getContestLeaderboard(int contestId, int index, int numUsers) {

  }

  public ArrayList<Entity<User>> getProblemLeaderboard(int problemId, int index, int numUsers) {

  }

  //change problem[] to ArrayList<Entity<Problem>> and 'Timetsamp before' to 'int index'
//   + Problem[] getPracticeProblems(Timestamp before, int numProblems)
// + Problem[] getPracticeProblemsByCategory(Category category, Timestamp before, int numProblems)
// + Problem[] getPracticeProblemsByCreator(long creatorId, Timestamp before, int numProblems)
// + Problem[] getPracticeProblemsByPoints(int min, int max, Timestamp before, int numProblems)
// + Problem[] getPracticeProblemsByNumSubmissions(int min, int max, Timestamp before, int numProblems)


}
