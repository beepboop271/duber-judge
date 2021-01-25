package services;

import java.util.ArrayList;

import dal.dao.ContestDao;
import dal.dao.ContestSessionDao;
import dal.dao.ProblemDao;
import dal.dao.RecordNotFoundException;
import dal.dao.SubmissionDao;
import dal.dao.UserDao;
import entities.Category;
import entities.Contest;
import entities.ContestSession;
import entities.ContestStatus;
import entities.Entity;
import entities.Problem;
import entities.SubmissionResult;
import entities.User;

/**
 * Serves public information (client does not need to login).
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
  private ContestSessionDao contestSessionDao;

  public PublicService() {
    this.userDao = new UserDao();
    this.problemDao = new ProblemDao();
    this.submissionDao = new SubmissionDao();
    this.contestDao = new ContestDao();
    this.contestSessionDao = new ContestSessionDao();
  }

  /**
   * Get the ongoing contests ordered from earliest to latest.
   *
   * @param index       the current offset
   * @param numContests the number of contests to retrieve
   * @return a list of contests
   */
  public ArrayList<Entity<Contest>> getOngoingContests(int index, int numContests) {
    return this.contestDao.getContests(index, numContests, ContestStatus.ONGOING);
  }

  public Entity<Problem> getProblem(long problemId) throws RecordNotFoundException {
    return this.problemDao.get(problemId);
  }

  /**
   * Get the upcoming contests ordered from earliest to latest.
   *
   * @param index       the current offset
   * @param numContests the number of contests to retrieve
   * @return a list of contests
   */
  public ArrayList<Entity<Contest>> getUpcomingContests(int index, int numContests) {
    return this.contestDao.getContests(index, numContests, ContestStatus.UPCOMING);
  }

  public ArrayList<Entity<User>> getLeaderboard(int index, int numUsers) {
    return this.userDao.getByPoints(index, numUsers);
  }

  public ArrayList<Entity<ContestSession>> getContestLeaderboard(
    int contestId,
    int index,
    int numUsers
  ) {
    return this.contestSessionDao.getLeaderboard(contestId, index, numUsers);
  }

  public ArrayList<Entity<SubmissionResult>> getProblemLeaderboard(
    int problemId,
    int index,
    int numUsers
  ) {
    return this.submissionDao.getProblemLeaderboard(problemId, index, numUsers);
  }

  public ArrayList<Entity<Problem>> getPracticeProblems(int index, int numProblems) {
    return this.problemDao.getPracticeProblems(index, numProblems);
  }

  public ArrayList<Entity<Problem>> getPracticeProblemsByCategory(
    Category category,
    int index,
    int numProblems
  ) {
    return this.problemDao.getPracticeProblemsByCategory(category, index, numProblems);
  }

  public ArrayList<Entity<Problem>> getPracticeProblemsByCreator(
    long creatorId,
    int index,
    int numProblems
  ) {
    return this.problemDao.getPracticeProblemsByCreator(creatorId, index, numProblems);
  }

  public ArrayList<Entity<Problem>> getPracticeProblemsByPoints(
    int min,
    int max,
    int index,
    int numProblems
  ) {
    return this.problemDao.getPracticeProblemsByPoints(min, max, index, numProblems);
  }

  public ArrayList<Entity<Problem>> getPracticeProblemsByNumSubmissions(
    int min,
    int max,
    int index,
    int numProblems
  ) {
    return this.problemDao.getPracticeProblemsByNumSubmissions(min, max, index, numProblems);
  }

  public int getContestNumParticipants(long contestId) {
    return this.contestSessionDao.getNumSessions(contestId);
  }
}
