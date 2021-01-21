package services;

import java.sql.Timestamp;
import java.util.ArrayList;

import dal.dao.ClarificationDao;
import dal.dao.ProblemDao;
import dal.dao.RecordNotFoundException;
import dal.dao.SubmissionDao;
import entities.Clarification;
import entities.ContestProblem;
import entities.Entity;
import entities.ExecutionStatus;
import entities.Language;
import entities.PracticeProblem;
import entities.Problem;
import entities.Submission;
// import judge.Judger;

/**
 * [description]
 * <p>
 * Created on 2021.01.16.
 *
 * @author Shari Sun, Candice Zhang, Joseph Wang
 * @version 1.0.0
 * @since 1.0.0
 */
public class ProblemService {
  private ProblemDao problemDao;
  private ClarificationDao clarificationDao;
  private UserService userService;
  private SubmissionDao submissionDao;

  public ProblemService() {
    this.problemDao = new ProblemDao();
    this.clarificationDao = new ClarificationDao();
    this.userService = new UserService();
    this.submissionDao = new SubmissionDao();
  }

  private boolean canSubmit(long userId, long problemId) {
    try {
      Problem problem = this.problemDao.get(problemId).getContent();
      if (problem instanceof PracticeProblem) {
        return true;
      } else if (problem instanceof ContestProblem) {
        int submissionCount =
          this.userService.getSubmissionCount(userId, problemId);
        return submissionCount
          < ((ContestProblem)problem).getSubmissionsLimit();
      }
    } catch (RecordNotFoundException e) {
      System.out.println(e.getMessage());
    }
    return false;
  }

  public Submission submitSolution(
    long userId,
    long problemId,
    String code,
    Language language
  ) throws InsufficientPermissionException {
    if (!this.canSubmit(userId, problemId)) {
      throw new InsufficientPermissionException();
    }
    Submission submission =
      new Submission(
        problemId,
        userId,
        code,
        language,
        new Timestamp(System.currentTimeMillis())
      );
    return submission;
    //TODO: uncomment this line
    // return Judger.judge(submission);
  }

  public void requestClarification(long userId, long problemId, String message)
    throws InsufficientPermissionException,
    RecordNotFoundException {

    Clarification clarification =
      new Clarification(problemId, userId, message, null);
    clarificationDao.add(clarification);
  }

  public ArrayList<Entity<Clarification>> getClarificationsByUser(
    long problemId,
    long userId
  ) {
    return this.clarificationDao.getByProblemAndUser(problemId, userId);
  }

  public ArrayList<Entity<Submission>> getSubmissions(
    long problemId,
    int index,
    int numSubmissions
  ) {
    return this.submissionDao.getByProblem(problemId, index, numSubmissions);
  }

  public ArrayList<Entity<Submission>> getSubmissionsWithStatus(
    long problemId,
    ExecutionStatus status,
    int index,
    int numProblems
  ) {
    return this.submissionDao
      .getByProblemAndStatus(problemId, status, index, numProblems);
  }
}
