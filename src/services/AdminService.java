package services;

import java.sql.Timestamp;
import java.util.Date;
import java.util.ArrayList;

import dal.dao.BatchDao;
import dal.dao.ClarificationDao;
import dal.dao.ContestDao;
import dal.dao.ContestSessionDao;
import dal.dao.ProblemDao;
import dal.dao.RecordNotFoundException;
import dal.dao.SubmissionDao;
import dal.dao.TestcaseDao;
import dal.dao.TestcaseRunDao;
import dal.dao.UserDao;
import entities.Batch;
import entities.Category;
import entities.Contest;
import entities.ContestProblem;
import entities.ContestSession;
import entities.ContestSessionStatus;
import entities.Entity;
import entities.PracticeProblem;
import entities.Problem;
import entities.ProblemType;
import entities.Testcase;
import entities.TestcaseRun;
import entities.entity_fields.BatchField;
import entities.entity_fields.ClarificationField;
import entities.entity_fields.ContestField;
import entities.entity_fields.ContestSessionField;
import entities.entity_fields.ProblemField;
import entities.entity_fields.TestcaseField;

/**
 * [description]
 * <p>
 * Created on 2021.01.16.
 *
 * @author Shari Sun, Joseph Wang
 * @version 1.0.0
 * @since 1.0.0
 */
public class AdminService {
  private UserService userService;
  private ContestDao contestDao;
  private ContestSessionDao contestSessionDao;
  private ProblemDao problemDao;
  private BatchDao batchDao;
  private TestcaseDao testcaseDao;
  private TestcaseRunDao testcaseRunDao;
  private ClarificationDao clarificationDao;
  private UserDao userDao;
  private SubmissionDao submissionDao;

  public AdminService() {
    this.userService = new UserService();
    this.contestDao = new ContestDao();
    this.contestSessionDao = new ContestSessionDao();
    this.problemDao = new ProblemDao();
    this.batchDao = new BatchDao();
    this.testcaseDao = new TestcaseDao();
    this.testcaseRunDao = new TestcaseRunDao();
    this.clarificationDao = new ClarificationDao();
    this.userDao = new UserDao();
    this.submissionDao = new SubmissionDao();
  }

  public long createContest(
    long userId,
    String title,
    String description,
    Timestamp startTime,
    Timestamp endTime,
    int durationMinutes
  ) throws InsufficientPermissionException {
    if (durationMinutes <= 0) {
      throw new IllegalArgumentException(
        "Duration cannot be equal or less than zero."
      );
    }

    if (!endTime.after(startTime) || startTime.compareTo(new Date()) < 0) {
      throw new IllegalArgumentException("Provided times are invalid.");
    }

    this.validate(userId);

    long id =
      this.contestDao.add(
        new Contest(
          userId,
          description,
          title,
          startTime,
          endTime,
          durationMinutes
        )
      );
    return id;
  }

  public <T> void updateContest(
    long userId,
    long contestId,
    ContestField field,
    T value
  ) throws RecordNotFoundException,
    InsufficientPermissionException {
    this.validateContest(userId, contestId);
    this.contestDao.update(contestId, field, value);
  }

  public void deleteContest(long userId, long contestId)
    throws InsufficientPermissionException {
    this.validateContest(userId, contestId);
    this.contestDao.deleteById(contestId);
    ArrayList<Entity<Problem>> problems =
      this.problemDao.getAllByContest(contestId);
    for (Entity<Problem> problem : problems) {
      this.problemDao.deleteById(problem.getId());
      ArrayList<Entity<Batch>> batches =
        this.batchDao.getByProblem(problem.getId());
      for (Entity<Batch> batch : batches) {
        this.testcaseDao.deleteByBatch(batch.getId());
      }
    }
  }

  public void kickUserFromContest(
    long userId,
    long kickedUserId,
    long contestId
  ) throws InsufficientPermissionException {
    this.validateContest(userId, contestId);
    try {
      ContestSession session =
        this.contestSessionDao.get(contestId, kickedUserId).getContent();
      this.contestSessionDao.update(
        session.getContestId(),
        ContestSessionField.STATUS,
        ContestSessionStatus.OVER
      );
    } catch (RecordNotFoundException e) {
      System.out.println("Contest not found");
    }

  }

  public long createProblem(
    long userId,
    ProblemType type,
    Category category,
    Timestamp createdAt,
    Timestamp lastModifiedAt,
    String title,
    String description,
    int points,
    int timeLimitMillis,
    int memoryLimitKb,
    int outputLimitKb,
    int numSubmissions,
    int submissionsLimit,
    long contestId,
    String editorial
  ) throws InsufficientPermissionException,
    IllegalArgumentException {
    this.validate(userId);

    if (memoryLimitKb <= 0 || outputLimitKb <= 0 || timeLimitMillis <= 0) {
      throw new IllegalArgumentException(
        "Problem limits cannot be less than or equal to 0."
      );
    }
    if (createdAt.after(lastModifiedAt)) {
      throw new IllegalArgumentException(
        "Problem must be created first before modified."
      );
    }
    if (submissionsLimit <= 0) {
      throw new IllegalArgumentException(
        "Submission limit must be greater than 0."
      );
    }
    if (numSubmissions < 0) {
      throw new IllegalArgumentException(
        "Number of submissions cannot be negative."
      );
    }
    if (points < 0) {
      throw new IllegalArgumentException("Points cannot be negative.");
    }

    long id = 0;
    switch (type) {
      case CONTEST:
        id =
          this.problemDao.add(
            new ContestProblem(
              category,
              userId,
              createdAt,
              lastModifiedAt,
              title,
              description,
              points,
              timeLimitMillis,
              memoryLimitKb,
              outputLimitKb,
              numSubmissions,
              submissionsLimit,
              contestId
            )
          );
        break;
      case PRACTICE:
        id =
          this.problemDao.add(
            new PracticeProblem(
              category,
              userId,
              createdAt,
              lastModifiedAt,
              title,
              description,
              points,
              timeLimitMillis,
              memoryLimitKb,
              outputLimitKb,
              numSubmissions,
              editorial
            )
          );
        break;
    }
    return id;
  }

  private void validateProblem(long userId, long problemId)
    throws InsufficientPermissionException {
    this.validate(userId);
    try {
      if (
        this.problemDao.get(problemId).getContent().getCreatorId() != userId
      ) {
        throw new InsufficientPermissionException();
      }
    } catch (RecordNotFoundException e) {
      System.out.println("problem does not exist");
    }
  }

  public <T> void updateProblem(
    long userId,
    long problemId,
    ProblemField field,
    T value
  ) throws RecordNotFoundException,
    InsufficientPermissionException {
    this.validateProblem(userId, problemId);
    this.problemDao.update(problemId, field, value);
  }

  public void deleteProblem(long userId, long problemId)
    throws InsufficientPermissionException {
    this.validateProblem(userId, problemId);
    this.problemDao.deleteById(problemId);
    ArrayList<Entity<Batch>> batches = this.batchDao.getByProblem(problemId);
    for (Entity<Batch> batch : batches) {
      this.testcaseDao.deleteByBatch(batch.getId());
    }

    // TODO: delete test case runs here
    this.submissionDao.deleteByProblem(problemId);
  }

  private void validateBatch(long userId, long batchId)
    throws InsufficientPermissionException {
    this.validate(userId);
    try {
      if (this.batchDao.get(batchId).getContent().getCreatorId() != userId) {
        throw new InsufficientPermissionException();
      }
    } catch (RecordNotFoundException e) {
      System.out.println("batch does not exist");
    }
  }

  public long createBatch(long userId, long problemId, int sequence, int points)
    throws InsufficientPermissionException,
    IllegalArgumentException {
    if (points < 0) {
      throw new IllegalArgumentException("Points cannot be negative.");
    }

    this.validate(userId);
    long id = this.batchDao.add(new Batch(problemId, userId, sequence, points));
    return id;
  }

  public <T> void updateBatch(
    long userId,
    long batchId,
    BatchField field,
    T value
  ) throws RecordNotFoundException,
    InsufficientPermissionException {
    this.validateBatch(userId, batchId);
    this.batchDao.update(batchId, field, value);
  }

  public void deleteBatch(long userId, long batchId)
    throws InsufficientPermissionException {
    this.validateBatch(userId, batchId);
    this.batchDao.deleteById(batchId);
    this.testcaseDao.deleteByBatch(batchId);
  }

  private void validateTestcase(long userId, long testcaseId)
    throws InsufficientPermissionException {
    this.validate(userId);
    try {
      if (this.batchDao.get(testcaseId).getContent().getCreatorId() != userId) {
        throw new InsufficientPermissionException();
      }
    } catch (RecordNotFoundException e) {
      System.out.println("testcase does not exist");
    }
  }

  public long createTestcase(
    long userId,
    long batchId,
    int sequence,
    String input,
    String output
  ) throws InsufficientPermissionException,
    IllegalArgumentException {
    if (sequence < 0) {
      throw new IllegalArgumentException("Sequences cannot be negative.");
    }

    this.validate(userId);
    long id =
      this.testcaseDao
        .add(new Testcase(batchId, userId, sequence, input, output));
    return id;
  }

  public <T> void updateTestcase(
    long userId,
    long testcaseId,
    TestcaseField field,
    T value
  ) throws RecordNotFoundException,
    InsufficientPermissionException {
    this.validateTestcase(userId, testcaseId);
    this.testcaseDao.update(userId, field, value);
  }

  public void deleteTestcase(long userId, long testcaseId)
    throws InsufficientPermissionException {
    this.validateTestcase(userId, testcaseId);
    this.testcaseDao.deleteById(testcaseId);
  }

  public void clarifyProblem(long userId, long clarificationId, String response)
    throws InsufficientPermissionException,
    RecordNotFoundException {
    this.validate(userId);
    this.clarificationDao
      .update(clarificationId, ClarificationField.RESPONSE, response);
  }

  // TODO: maybe move this to handlers
  private void validate(long userId) throws InsufficientPermissionException {
    try {
      if (!this.userService.isAdmin(userId)) {
        throw new InsufficientPermissionException();
      }
    } catch (RecordNotFoundException e) {
      e.printStackTrace();
    }
  }

  private void validateContest(long userId, long contestId)
    throws InsufficientPermissionException {
    this.validate(userId);
    try {
      if (
        this.contestDao.get(contestId).getContent().getCreatorId() != userId
      ) {
        throw new InsufficientPermissionException();
      }
    } catch (RecordNotFoundException e) {
      System.out.println("contest does not exist");
    }
  }

}
