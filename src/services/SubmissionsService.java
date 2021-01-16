package services;

import dal.dao.RecordNotFoundException;
import entities.Language;
import entities.Submission;

/**
 * [description]
 * <p>
 * Created on 2021.01.16.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class SubmissionsService {

  private boolean validateContestSession(long userId, long contestSessionId) {

  }

  private boolean didExceedSubmissionsLimit(long userId, long problemId) {

  }

  public Submission executeCode(long userId, long problemId, String code, Language language) {

  }

  public void startContest(long userId, long contestId)
    throws InsufficientPermissionException, RecordNotFoundException {

  }

  public void requestClarification(long userId, long problemId, String message)
    throws InsufficientPermissionException, RecordNotFoundException {

  }
}
