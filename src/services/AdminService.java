package services;

import java.sql.Timestamp;
import java.util.ArrayList;

import dal.dao.BatchDao;
import dal.dao.ClarificationDao;
import dal.dao.ContestDao;
import dal.dao.ContestSessionDao;
import dal.dao.ProblemDao;
import dal.dao.RecordNotFoundException;
import dal.dao.TestcaseDao;
import dal.dao.UserDao;
import entities.Batch;
import entities.Category;
import entities.Contest;
import entities.ContestProblem;
import entities.ContestSession;
import entities.ContestStatus;
import entities.Entity;
import entities.PracticeProblem;
import entities.Problem;
import entities.ProblemType;
import entities.Testcase;
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
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class AdminService {
  UserService userService = new UserService();
  ContestDao contestDao = new ContestDao();
  ContestSessionDao contestSessionDao = new ContestSessionDao();
  ProblemDao problemDao = new ProblemDao();
  BatchDao batchDao = new BatchDao();
  TestcaseDao testcaseDao = new TestcaseDao();
  ClarificationDao clarificationDao = new ClarificationDao();
  UserDao userDao = new UserDao();

  private void validate(long userId) throws InsufficientPermissionException {
    try {
      if (!this.userService.isAdmin(userId)) {
        throw new InsufficientPermissionException();
      }
    } catch (RecordNotFoundException e) {
      e.printStackTrace();
    }
  }


  public long createContest(
    long userId,
    String title,
    String description,
    int submissionsLimit,
    Timestamp startTime,
    Timestamp endTime,
    int durationMinutes
  ) throws InsufficientPermissionException {
    this.validate(userId);
    long id = this.contestDao.add(new Contest(
      userId,
      description,
      title,
      startTime,
      endTime,
      durationMinutes
    ));
    return id;
  }

  private void validateContest(long userId, long contestId) throws InsufficientPermissionException {
    this.validate(userId);
    try {
      if (this.contestDao.get(contestId).getContent().getCreatorId() != userId) {
        throw new InsufficientPermissionException();
      }
    } catch (RecordNotFoundException e) {
      System.out.println("contest does not exist");
    }
  }

  public void updateContestField(
    long userId,
    long contestId,
    ContestField field,
    String value
  ) throws RecordNotFoundException, InsufficientPermissionException {
    this.validateContest(userId, contestId);
    this.contestDao.update(contestId, field, value);
  }

  public void deleteContest(long userId, long contestId) throws InsufficientPermissionException {
    this.validateContest(userId, contestId);
    this.contestDao.deleteById(contestId);
    ArrayList<Entity<Problem>> problems = this.problemDao.getAllByContest(contestId);
    for (Entity<Problem> problem : problems) {
      this.problemDao.deleteById(problem.getId());
      ArrayList<Entity<Batch>> batches = this.batchDao.getByProblem(problem.getId());
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
      ContestSession session = this.contestSessionDao.get(contestId, kickedUserId).getContent();
      this.contestSessionDao.update(
        session.getContestId(), ContestSessionField.STATUS, ContestStatus.OVER
      );
    } catch (RecordNotFoundException e) {
      System.out.println("Contest not found");
    }

  }

  public long createProblem(
    long userId,
    ProblemType type,
    Category category,
    long creatorId,
    Timestamp createdAt,
    Timestamp lastModifiedAt,
    String title,
    String description,
    int points,
    int timeLimitMillis,
    int memoryLimitKb,
    int outputLimitKb,
    int numSubmissions,
    int clearedSubmissions,
    int submissionsLimit,
    long contestId,
    String editorial
  ) throws InsufficientPermissionException {
    this.validate(userId);
    long id = 0;
    switch (type) {
      case CONTEST:
        id = this.problemDao.add(new ContestProblem(
          category,
          creatorId,
          createdAt,
          lastModifiedAt,
          title,
          description,
          points,
          timeLimitMillis,
          memoryLimitKb,
          outputLimitKb,
          numSubmissions,
          clearedSubmissions,
          submissionsLimit,
          contestId
        ));
        break;
      case PRACTICE:
        id = this.problemDao.add(new PracticeProblem(
          category,
          creatorId,
          createdAt,
          lastModifiedAt,
          title,
          description,
          points,
          timeLimitMillis,
          memoryLimitKb,
          outputLimitKb,
          numSubmissions,
          clearedSubmissions,
          editorial
        ));
        break;
    }
    return id;
  }

  private void validateProblem(long userId, long problemId) throws InsufficientPermissionException {
    this.validate(userId);
    try {
      if (this.problemDao.get(problemId).getContent().getCreatorId() != userId) {
        throw new InsufficientPermissionException();
      }
    } catch (RecordNotFoundException e) {
      System.out.println("problem does not exist");
    }
  }

  public void updateProblemField(
    long userId,
    long problemId,
    ProblemField field,
    String value
  ) throws RecordNotFoundException, InsufficientPermissionException {
    this.validateProblem(userId, problemId);
    this.problemDao.update(problemId, field, value);
  }

  public void deleteProblem(long userId, long problemId) throws InsufficientPermissionException {
    this.validateProblem(userId, problemId);
    this.problemDao.deleteById(problemId);
    ArrayList<Entity<Batch>> batches = this.batchDao.getByProblem(problemId);
    for (Entity<Batch> batch : batches) {
      this.testcaseDao.deleteByBatch(batch.getId());
    }
  }

  private void validateBatch(long userId, long batchId) throws InsufficientPermissionException {
    this.validate(userId);
    try {
      if (this.batchDao.get(batchId).getContent().getCreatorId() != userId) {
        throw new InsufficientPermissionException();
      }
    } catch (RecordNotFoundException e) {
      System.out.println("batch does not exist");
    }
  }

  public long createBatch(
    long userId,
    long problemId,
    int sequence,
    int order,
    int points
  ) throws InsufficientPermissionException {
    this.validate(userId);
    long id = this.batchDao.add(new Batch(
      problemId,
      userId,
      sequence,
      points
    ));
    return id;
  }

  public void updateBatch(
    long userId,
    long batchId,
    BatchField field,
    String value
  ) throws RecordNotFoundException, InsufficientPermissionException {
    this.validateBatch(userId, batchId);
    this.batchDao.update(batchId, field, value);
  }

  public void deleteBatch(
    long userId,
    long batchId
  ) throws InsufficientPermissionException {
    this.validateBatch(userId, batchId);
    this.batchDao.deleteById(batchId);
    this.testcaseDao.deleteByBatch(batchId);
  }

  private void validateTestcase(long userId, long testcaseId) throws InsufficientPermissionException {
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
    int order,
    String input,
    String output
  ) throws InsufficientPermissionException {
    this.validate(userId);
    long id = this.testcaseDao.add(new Testcase(
      batchId,
      userId,
      sequence,
      input,
      output
    ));
    return id;
  }

  public void updateTestcase(
    long userId,
    long testcaseId,
    TestcaseField field,
    String value
  ) throws RecordNotFoundException, InsufficientPermissionException {
    this.validateTestcase(userId, testcaseId);
    this.testcaseDao.update(userId, field, value);
  }

  public void deleteTestcase(long userId, long testcaseId) throws InsufficientPermissionException {
    this.validateTestcase(userId, testcaseId);
    this.testcaseDao.deleteById(testcaseId);
  }

  public void clarifyProblem(
    long userId,
    long clarificationId,
    String response
  ) throws InsufficientPermissionException, RecordNotFoundException {
    this.validate(userId);
    this.clarificationDao.update(clarificationId, ClarificationField.RESPONSE, response);
  }
}
