package services;

import java.util.ArrayList;

import dal.dao.UserDao;
import dal.dao.UserPoints;
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
  private UserDao userDao;

  public PublicService() {
    this.userDao = new UserDao();
  }

  /**
   * Get the contests ordered from latest to earliest.
   *
   * @param index            the current offset
   * @param numContests      the number of contests to retrieve
   * @return                 a list of contests
   */
  public ArrayList<Entity<Contest>> getContests(int index, int numContests) {
    
  }


  public ArrayList<UserPoints> getLeaderboard(int index, int numUsers) {
    return this.userDao.getByPoints(index, numUsers);
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
