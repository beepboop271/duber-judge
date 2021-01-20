package services;

import java.util.ArrayList;

import dal.dao.ContestDao;
import dal.dao.ProblemDao;
import dal.dao.SubmissionDao;
import dal.dao.UserDao;
import dal.dao.UserPoints;
import entities.Category;
import entities.Contest;
import entities.Entity;
import entities.Problem;
import entities.User;

/**
 * Serves public information (client does not need to
 * login).
 * <p>
 * Created on 2021.01.16.
 *
 * @author Shari Sun, Joseph Wang
 * @version 1.0.0
 * @since 1.0.0
 */
public class PublicService {
  private UserDao userDao;
  private ProblemDao problemDao;
  private SubmissionDao submissionDao;
  private ContestDao contestDao;

  public PublicService() {
    this.userDao = new UserDao();
    this.problemDao = new ProblemDao();
    this.submissionDao = new SubmissionDao();
    this.contestDao = new ContestDao();
  }

  /**
   * Get the contests ordered from latest to earliest.
   *
   * @param index       the current offset
   * @param numContests the number of contests to retrieve
   * @return a list of contests
   */
  public ArrayList<Entity<Contest>> getContests(int index, int numContests) {
    return this.contestDao.getContests(index, numContests);
  }

  public ArrayList<UserPoints> getLeaderboard(int index, int numUsers) {
    return this.userDao.getByPoints(index, numUsers);
  }

  public ArrayList<Entity<User>> getContestLeaderboard(
    int contestId,
    int index,
    int numUsers
  ) {

  }

  public ArrayList<Entity<User>> getProblemLeaderboard(
    int problemId,
    int index,
    int numUsers
  ) {

  }

  // change problem[] to ArrayList<Entity<Problem>> and
  // 'Timetsamp before' to 'int index'
  ArrayList<Entity<Problem>> getPracticeProblems(int index, int numProblems) {
    return this.problemDao.getPracticeProblems(index, numProblems);
  }

  ArrayList<Entity<Problem>> getPracticeProblemsByCategory(
    Category category,
    int index,
    int numProblems
  ) {
    return this.problemDao.getPracticeProblemsByCategory(category, index, numProblems);
  }

  ArrayList<Entity<Problem>> getPracticeProblemsByCreator(
    long creatorId,
    int index,
    int numProblems
  ) {
    return this.problemDao.getPracticeProblemsByCreator(creatorId, index, numProblems);
  }

  ArrayList<Entity<Problem>> getPracticeProblemsByPoints(
    int min,
    int max,
    int index,
    int numProblems
  ) {
    return this.problemDao.getPracticeProblemsByPoints(min, max, index, numProblems);
  }

  ArrayList<Entity<Problem>> getPracticeProblemsByNumSubmissions(
    int min,
    int max,
    int index,
    int numProblems
  ) {
    return this.problemDao.getPracticeProblemsByNumSubmissions(min, max, index, numProblems);
  }
}
