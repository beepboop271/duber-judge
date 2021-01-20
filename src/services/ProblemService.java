package services;

import java.sql.Timestamp;

import dal.dao.ClarificationDao;
import dal.dao.ProblemDao;
import dal.dao.RecordNotFoundException;
import entities.Clarification;
import entities.ContestProblem;
import entities.Language;
import entities.PracticeProblem;
import entities.Problem;
import entities.Submission;
import judge.Judger;

/**
 * [description]
 * <p>
 * Created on 2021.01.16.
 *
 * @author Shari Sun, Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */
public class ProblemService {
  private ProblemDao problemDao;
  private ClarificationDao clarificationDao;
  private UserService userService;

  public ProblemService() {
    this.problemDao = new ProblemDao();
    this.clarificationDao = new ClarificationDao();
    this.userService = new UserService();
  }

  private boolean canSubmit(long userId, long problemId) {
    try {
      Problem problem = this.problemDao.get(problemId).getContent();
      if (problem instanceof PracticeProblem) {
        return true;
      } else if (problem instanceof ContestProblem) {
        int submissionCount = this.userService.getSubmissionCount(userId, problemId);
        return submissionCount < ((ContestProblem)problem).getSubmissionsLimit();
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
    Submission submission = new Submission(
      problemId,
      userId,
      code,
      language,
      new Timestamp(System.currentTimeMillis())
    );
    return Judger.judge(submission);
  }

  public void requestClarification(long userId, long problemId, String message)
    throws InsufficientPermissionException, RecordNotFoundException {

      Clarification clarification = new Clarification(
      problemId,
      userId,
      message,
      null
    );
    clarificationDao.add(clarification);
  }

  public ArrayList<Entity<Submission>> getSubmissions(long problemId, )
}
