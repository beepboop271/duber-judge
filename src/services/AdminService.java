package services;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import dal.dao.BatchDao;
import dal.dao.ClarificationDao;
import dal.dao.ContestDao;
import dal.dao.ContestSessionDao;
import dal.dao.IllegalCodeDao;
import dal.dao.ProblemDao;
import dal.dao.RecordNotFoundException;
import dal.dao.SubmissionDao;
import dal.dao.TestcaseDao;
import dal.dao.TestcaseRunDao;
import dal.dao.UserDao;
import entities.Batch;
import entities.Category;
import entities.Clarification;
import entities.Contest;
import entities.ContestProblem;
import entities.ContestSession;
import entities.ContestSessionStatus;
import entities.ContestStatus;
import entities.Entity;
import entities.IllegalCode;
import entities.Language;
import entities.PracticeProblem;
import entities.Problem;
import entities.ProblemType;
import entities.PublishingState;
import entities.Testcase;
import entities.User;
import entities.entity_fields.BatchField;
import entities.entity_fields.ClarificationField;
import entities.entity_fields.ContestField;
import entities.entity_fields.ContestSessionField;
import entities.entity_fields.IllegalCodeField;
import entities.entity_fields.ProblemField;
import entities.entity_fields.TestcaseField;

/**
 * {@code AdminService} deals with all admin related actions,
 * such as deleting user, creating contests, problems, etc.
 * <p>
 * Created on 2021.01.16.
 *
 * @author Shari Sun, Joseph Wang
 * @version 1.0.0
 * @since 1.0.0
 */
public class AdminService {
  private ContestDao contestDao;
  private ContestSessionDao contestSessionDao;
  private ProblemDao problemDao;
  private BatchDao batchDao;
  private TestcaseDao testcaseDao;
  private TestcaseRunDao testcaseRunDao;
  private ClarificationDao clarificationDao;
  private UserDao userDao;
  private SubmissionDao submissionDao;
  private IllegalCodeDao illegalCodeDao;

  public AdminService() {
    this.contestDao = new ContestDao();
    this.contestSessionDao = new ContestSessionDao();
    this.problemDao = new ProblemDao();
    this.batchDao = new BatchDao();
    this.testcaseDao = new TestcaseDao();
    this.testcaseRunDao = new TestcaseRunDao();
    this.clarificationDao = new ClarificationDao();
    this.userDao = new UserDao();
    this.submissionDao = new SubmissionDao();
    this.illegalCodeDao = new IllegalCodeDao();
  }

  public void removeUser(long userId) {
    this.userDao.deleteById(userId);
  }

  public ArrayList<Entity<User>> getUsers(int index, int numUsers) {
    return this.userDao.getUsers(index, numUsers);
  }

  public ArrayList<Entity<Problem>> getCreatedProblems(long adminId) {
    return this.problemDao.getCreatedProblems(adminId);
  }

  public ArrayList<Entity<Contest>> getCreatedContests(long adminId) {
    return this.contestDao.getCreatedContests(adminId);
  }

  public long createContest(
    long adminId,
    String title,
    String description,
    Timestamp startTime,
    Timestamp endTime,
    int durationMinutes
  ) throws InsufficientPermissionException, IllegalArgumentException {
    if (durationMinutes <= 0) {
      throw new IllegalArgumentException(
        "Duration cannot be equal or less than zero."
      );
    }

    if (!endTime.after(startTime) || startTime.compareTo(new Date()) < 0) {
      throw new IllegalArgumentException("Provided times are invalid.");
    }
    long id = -1;
    try {
      id = this.contestDao.add(
          new Contest(
            adminId,
            description,
            title,
            startTime,
            endTime,
            ContestStatus.UPCOMING,
            durationMinutes,
            PublishingState.PENDING
          )
        );
    } catch (IllegalArgumentException e) {
      if (InvalidArguments.valueOf(e.getMessage()) == InvalidArguments.TITLE_TAKEN) {
        throw e;
      }
    }
    return id;
  }

  public <T> void updateContest(
    long adminId,
    long contestId,
    ContestField field,
    T value
  ) throws RecordNotFoundException,
    InsufficientPermissionException {
    this.validateContest(adminId, contestId);
    this.contestDao.update(contestId, field, value);
  }

  public void deleteContest(long adminId, long contestId)
    throws InsufficientPermissionException {
    this.validateContest(adminId, contestId);
    this.contestDao.deleteById(contestId);
    ArrayList<Entity<Problem>> problems = this.problemDao.getAllByContest(contestId);
    for (Entity<Problem> problem : problems) {
      this.testcaseRunDao.deleteByProblem(problem.getId());
      this.submissionDao.deleteByProblem(problem.getId());
      ArrayList<Entity<Batch>> batches = this.batchDao.getByProblem(problem.getId());
      for (Entity<Batch> batch : batches) {
        this.testcaseDao.deleteByBatch(batch.getId());
      }
    }

    this.problemDao.deleteByContest(contestId);
  }

  public void kickUserFromContest(
    long adminId,
    long kickedUserId,
    long contestId
  ) throws InsufficientPermissionException {
    this.validateContest(adminId, contestId);
    try {
      Entity<ContestSession> session = this.contestSessionDao.get(contestId, kickedUserId);
      this.contestSessionDao.update(
        session.getId(),
        ContestSessionField.STATUS,
        ContestSessionStatus.OVER
      );
    } catch (RecordNotFoundException e) {
      System.out.println("Contest not found");
    }

  }

  public long createPracticeProblem(
    long adminId,
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
    String editorial
  ) throws InsufficientPermissionException,
    IllegalArgumentException {
    this.checkProblemCreation(
      createdAt,
      lastModifiedAt,
      points,
      timeLimitMillis,
      memoryLimitKb,
      outputLimitKb,
      numSubmissions,
      submissionsLimit
    );
    return this.problemDao.add(
      new PracticeProblem(
        category,
        adminId,
        createdAt,
        lastModifiedAt,
        title,
        description,
        points,
        timeLimitMillis,
        memoryLimitKb,
        outputLimitKb,
        numSubmissions,
        0,
        editorial,
        PublishingState.PENDING
      )
    );
  }

  public long createContestProblem(
    long adminId,
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
    long contestId
  ) throws InsufficientPermissionException,
    IllegalArgumentException {
    this.checkProblemCreation(
      createdAt,
      lastModifiedAt,
      points,
      timeLimitMillis,
      memoryLimitKb,
      outputLimitKb,
      numSubmissions,
      submissionsLimit
    );
    return this.problemDao.add(
      new ContestProblem(
        category,
        adminId,
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
        contestId,
        0,
        PublishingState.PENDING
      )
    );
  }

  private void checkProblemCreation(
    Timestamp createdAt,
    Timestamp lastModifiedAt,
    int points,
    int timeLimitMillis,
    int memoryLimitKb,
    int outputLimitKb,
    int numSubmissions,
    int submissionsLimit
  ) throws IllegalArgumentException {
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
  }

  private void validateProblem(long adminId, long problemId)
    throws InsufficientPermissionException {
    try {
      if (
        this.problemDao.get(problemId).getContent().getCreatorId() != adminId
      ) {
        throw new InsufficientPermissionException();
      }
    } catch (RecordNotFoundException e) {
      System.out.println("problem does not exist");
    }
  }

  public <T> void updateProblem(
    long adminId,
    long problemId,
    ProblemField field,
    T value
  ) throws RecordNotFoundException,
    InsufficientPermissionException {
    this.validateProblem(adminId, problemId);
    this.problemDao.update(problemId, field, value);
  }

  public void deleteProblem(long adminId, long problemId)
    throws InsufficientPermissionException {
    this.validateProblem(adminId, problemId);
    this.problemDao.deleteById(problemId);
    ArrayList<Entity<Batch>> batches = this.batchDao.getByProblem(problemId);
    for (Entity<Batch> batch : batches) {
      this.batchDao.deleteById(batch.getId());
      this.testcaseDao.deleteByBatch(batch.getId());
    }

    this.testcaseRunDao.deleteByProblem(problemId);
    this.submissionDao.deleteByProblem(problemId);
  }

  private void validateBatch(long adminId, long batchId)
    throws InsufficientPermissionException {
    try {
      if (this.batchDao.get(batchId).getContent().getCreatorId() != adminId) {
        throw new InsufficientPermissionException();
      }
    } catch (RecordNotFoundException e) {
      System.out.println("batch does not exist");
    }
  }

  public long createBatch(long adminId, long problemId, int sequence, int points)
    throws InsufficientPermissionException,
    IllegalArgumentException {
    if (points < 0) {
      throw new IllegalArgumentException("Points cannot be negative.");
    }
    long id = this.batchDao.add(new Batch(problemId, adminId, sequence, points));
    return id;
  }

  public <T> void updateBatch(
    long adminId,
    long batchId,
    BatchField field,
    T value
  ) throws RecordNotFoundException,
    InsufficientPermissionException {
    this.validateBatch(adminId, batchId);
    this.batchDao.update(batchId, field, value);
  }

  public void deleteBatch(long adminId, long batchId)
    throws InsufficientPermissionException {
    this.validateBatch(adminId, batchId);
    this.batchDao.deleteById(batchId);
    this.testcaseDao.deleteByBatch(batchId);
  }

  private void validateTestcase(long adminId, long testcaseId)
    throws InsufficientPermissionException {
    try {
      if (this.testcaseDao.get(testcaseId).getContent().getCreatorId() != adminId) {
        throw new InsufficientPermissionException();
      }
    } catch (RecordNotFoundException e) {
      System.out.println("testcase does not exist");
    }
  }

  public long createTestcase(
    long adminId,
    long batchId,
    int sequence,
    String input,
    String output
  ) throws InsufficientPermissionException,
    IllegalArgumentException {
    if (sequence < 0) {
      throw new IllegalArgumentException("Sequences cannot be negative.");
    }

    long id = this.testcaseDao.add(
      new Testcase(batchId, adminId, sequence, input, output)
    );
    return id;
  }

  public <T> void updateTestcase(
    long adminId,
    long testcaseId,
    TestcaseField field,
    T value
  ) throws RecordNotFoundException,
    InsufficientPermissionException {
    this.validateTestcase(adminId, testcaseId);
    this.testcaseDao.update(testcaseId, field, value);
  }

  public void deleteTestcase(long adminId, long testcaseId)
    throws InsufficientPermissionException {
    this.validateTestcase(adminId, testcaseId);
    this.testcaseDao.deleteById(testcaseId);
  }

  private void validateContest(long adminId, long contestId)
    throws InsufficientPermissionException {
    try {
      if (
        this.contestDao.get(contestId).getContent().getCreatorId() != adminId
      ) {
        throw new InsufficientPermissionException();
      }
    } catch (RecordNotFoundException e) {
      System.out.println("contest does not exist");
    }
  }

  public void clarifyProblem(long clarificationId, String response)
    throws RecordNotFoundException {
    this.clarificationDao.update(clarificationId, ClarificationField.RESPONSE, response);
  }

  public ArrayList<Entity<Clarification>> getUnresolvedClarifications(
    int adminId,
    int index,
    int numClarifications
  ) {
    return this.clarificationDao.getUnresolvedClarifications(adminId, index, numClarifications);
  }


  public ArrayList<Entity<ContestSession>> getContestParticipants(
    long contestId,
    int index,
    int numSessions
  ) {
    return this.contestSessionDao.getByContest(contestId, index, numSessions);
  }


  public <T> void updateRestriction(
    long illegalCodeId,
    IllegalCodeField field,
    T value
  ) throws RecordNotFoundException {
    this.illegalCodeDao.update(illegalCodeId, field, value);
  }

  public long createRestriction(Language language, String content) {
    return this.illegalCodeDao.add(new IllegalCode(language, content));
  }

  public void deleteRestriction(long id) {
    this.illegalCodeDao.deleteById(id);
  }
}
