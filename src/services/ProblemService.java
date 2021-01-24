package services;

import java.sql.Timestamp;
import java.util.ArrayList;

import dal.dao.ClarificationDao;
import dal.dao.ContestSessionDao;
import dal.dao.ProblemDao;
import dal.dao.RecordNotFoundException;
import dal.dao.SubmissionDao;
import entities.Clarification;
import entities.ContestProblem;
import entities.ContestSession;
import entities.Entity;
import entities.ExecutionStatus;
import entities.Language;
import entities.PracticeProblem;
import entities.Problem;
import entities.Submission;
import entities.SubmissionResult;
import judge.Judger;
import entities.entity_fields.ContestSessionField;
import entities.entity_fields.ProblemField;

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
  private ContestSessionDao contestSessionDao;

  public ProblemService() {
    this.problemDao = new ProblemDao();
    this.clarificationDao = new ClarificationDao();
    this.userService = new UserService();
    this.submissionDao = new SubmissionDao();
    this.contestSessionDao = new ContestSessionDao();
  }

  private boolean canSubmit(long userId, long problemId) {
    try {
      Problem problem = this.problemDao.get(problemId).getContent();
      if (problem instanceof PracticeProblem) {
        return true;
      } else if (problem instanceof ContestProblem) {
        int submissionCount =
          this.userService.getSubmissionCount(userId, problemId);
        return submissionCount < ((ContestProblem)problem).getSubmissionsLimit();
      }
    } catch (RecordNotFoundException e) {
      System.out.println(e.getMessage());
    }
    return false;
  }

  /**
   *
   * @param userId
   * @param problemId
   * @param code
   * @param language
   * @return
   * @throws InsufficientPermissionException    The user is unable to submit.
   * @throws RecordNotFoundException            The problem is not found.
   */
  public SubmissionResult submitSolution(
    long userId,
    long problemId,
    String code,
    Language language
  ) throws InsufficientPermissionException, RecordNotFoundException {
    if (!this.canSubmit(userId, problemId)) {
      throw new InsufficientPermissionException();
    }
    Submission submission = new Submission(
      problemId,
      userId,
      code,
      language,
      new Timestamp(System.currentTimeMillis())
    );

    Entity<Problem> problem = this.problemDao.getNested(problemId);
    SubmissionResult result = Judger.judge(submission, problem);
    long submissionId = this.submissionDao.add(result);

    Problem pContent = problem.getContent();
    if (pContent instanceof ContestProblem) {
      long sessionId = this.contestSessionDao.get(
        ((ContestProblem)pContent).getContestId(),
        userId
      ).getId();
      this.contestSessionDao.updateLatestScore(userId, sessionId);
    }

    this.problemDao.update(
      problem.getId(),
      ProblemField.NUM_SUBMISSIONS,
      pContent.getNumSubmissions()+1
    );

    if (result.getStatus() == ExecutionStatus.ALL_CLEAR) {
      this.problemDao.update(
        problem.getId(),
        ProblemField.CLEARED_SUBMISSIONS,
        pContent.getClearedSubmissions()+1
      );
    }

    return result;
  }

  public void requestClarification(long userId, long problemId, String message)
    throws InsufficientPermissionException,
    RecordNotFoundException {

    Clarification clarification = new Clarification(
      problemId,
      userId,
      message,
      null,
      new Timestamp(System.currentTimeMillis())
    );
    clarificationDao.add(clarification);
  }

  public ArrayList<Entity<Clarification>> getClarifications(
    long userId,
    long problemId
  ) {
    return this.clarificationDao.getByProblemAndUser(problemId, userId);
  }

  public ArrayList<Entity<SubmissionResult>> getAllSubmissions(
    long problemId,
    int index,
    int numSubmissions
  ) {
    return this.submissionDao.getByProblem(problemId, index, numSubmissions);
  }

  public ArrayList<Entity<SubmissionResult>> getSubmissionsWithStatus(
    long problemId,
    ExecutionStatus status,
    int index,
    int numProblems
  ) {
    return this.submissionDao.getByProblemAndStatus(problemId, status, index, numProblems);
  }

  public Entity<Problem> getProblem(
    long problemId
  ) throws RecordNotFoundException {
    return this.problemDao.get(problemId);
  }

  public Entity<SubmissionResult> getSubmission(long submissionId)
    throws RecordNotFoundException {
    return this.submissionDao.get(submissionId);
  }

  public boolean validateSubmissionId(
    long userId,
    long submissionId
  ) throws RecordNotFoundException {
    return this.submissionDao.get(submissionId)
                             .getContent()
                             .getSubmission()
                             .getUserId() == userId;
  }
}