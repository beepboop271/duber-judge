package services;

import java.sql.Timestamp;
import java.util.ArrayList;

import dal.dao.ContestDao;
import dal.dao.ContestSessionDao;
import dal.dao.ProblemDao;
import dal.dao.RecordNotFoundException;
import entities.Contest;
import entities.ContestSession;
import entities.ContestStatus;
import entities.Entity;
import entities.Problem;
import entities.ContestSessionStatus;
import entities.entity_fields.ContestSessionField;

/**
 * [description]
 * <p>
 * Created on 2021.01.16.
 *
 * @author Shari Sun, Candice Zhang, Joseph Wang
 * @version 1.0.0
 * @since 1.0.0
 */

public class ContestService {
  private ContestSessionDao contestSessionDao;
  private ProblemDao problemDao;
  private ContestDao contestDao;

  public ContestService() {
    this.contestSessionDao = new ContestSessionDao();
    this.problemDao = new ProblemDao();
    this.contestDao = new ContestDao();
  }

  private boolean validateContestSession(long userId, long contestId) {
    try {
      ContestSession contestSession = this.contestSessionDao.get(contestId, userId).getContent();
      return contestSession.getStatus() == ContestSessionStatus.ONGOING;
    } catch (RecordNotFoundException e) {
      return true;
    }
  }


  public boolean allowProblemViewing(long userId, long contestId) {
    try {
      this.contestSessionDao.get(contestId, userId);
      return true;
    } catch (RecordNotFoundException e) {
      return false;
    }
  }

  public void startContest(long userId, long contestId)
    throws InsufficientPermissionException, RecordNotFoundException {
    if (!this.validateContestSession(userId, contestId)) {
      throw new InsufficientPermissionException();
    }
    this.contestSessionDao.add(
      new ContestSession(
        contestId,
        userId,
        new Timestamp(System.currentTimeMillis()),
        ContestSessionStatus.ONGOING,
        0
      )
    );
  }

  public void updateUserStatus(
    long userId,
    long contestId,
    ContestSessionStatus value
  )
    throws RecordNotFoundException {

    long contestSessionId = this.contestSessionDao.get(contestId, userId).getId();
    this.contestSessionDao.updateByUser(
      userId,
      contestSessionId,
      ContestSessionField.STATUS,
      value
    );
  }

  public void updateUserScore(
    long userId,
    long contestId,
    int score
  )
    throws RecordNotFoundException {
    Entity<ContestSession> session = this.contestSessionDao.get(contestId, userId);
    if (score < session.getContent().getScore()) {
      return;
    }
    this.contestSessionDao.updateByUser(
      userId,
      session.getId(),
      ContestSessionField.SCORE,
      score
    );
  }


  public ArrayList<Entity<Problem>> getProblems(
    long userId,
    long contestId
  ) throws InsufficientPermissionException {
    if (!this.allowProblemViewing(userId, contestId)) {
      throw new InsufficientPermissionException();
    }
    return this.problemDao.getAllByContest(contestId);
  }

  public Entity<Contest> getContest(long contestId)
    throws RecordNotFoundException {
    return this.contestDao.get(contestId);
  }


  public void updateScore(long contestSessionId, int score)
    throws RecordNotFoundException {
    this.contestSessionDao.update(
      contestSessionId,
      ContestSessionField.SCORE,
      score
    );
  }

}
