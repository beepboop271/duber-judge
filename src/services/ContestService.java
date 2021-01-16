package services;

import java.sql.Timestamp;

import dal.dao.ContestDao;
import dal.dao.ContestSessionDao;
import dal.dao.RecordNotFoundException;
import entities.ContestSession;
import entities.ContestStatus;

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
  private ContestDao contestDao;
  private ContestSessionDao contestSessionDao;

  public ContestService() {
    this.contestDao = new ContestDao();
    this.contestSessionDao = new ContestSessionDao();
  }

  private boolean validateContestSession(long userId, long contestSessionId) {
    try {
      ContestSession contestSession = this.contestSessionDao.get(contestSessionId).getContent();
      return contestSession.getStatus() == ContestStatus.ONGOING;
    } catch (RecordNotFoundException e) {
      return false;
    }
  }

  public void startContest(long userId, long contestId)
    throws InsufficientPermissionException, RecordNotFoundException {
    this.contestDao.get(contestId); // check if record exists
    //TODO: permission checking for InsufficientPermissionException?
    this.contestSessionDao.add(
      new ContestSession(
        contestId,
        userId,
        new Timestamp(System.currentTimeMillis()),
        ContestStatus.ONGOING,
        0
      )
    );
  }

}
