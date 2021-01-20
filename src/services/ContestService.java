package services;

import java.sql.Timestamp;

import dal.dao.ContestSessionDao;
import dal.dao.RecordNotFoundException;
import entities.ContestSession;
import entities.ContestSessionStatus;
import entities.entity_fields.ContestSessionField;

/**
 * [description]
 * <p>
 * Created on 2021.01.16.
 *
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class ContestService {
  private ContestSessionDao contestSessionDao;

  public ContestService() {
    this.contestSessionDao = new ContestSessionDao();
  }

  private boolean validateContestSession(long userId, long contestId) {
    try {
      ContestSession contestSession = this.contestSessionDao.get(contestId, userId).getContent();
      return contestSession.getStatus() == ContestSessionStatus.ONGOING;
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


  public void updateScore(long contestSessionId, int score)
    throws RecordNotFoundException {
    this.contestSessionDao.update(
      contestSessionId,
      ContestSessionField.SCORE,
      score
    );
  }

}
