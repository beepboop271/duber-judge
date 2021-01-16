package services;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.Base64;

import dal.dao.RecordNotFoundException;
import dal.dao.SessionDao;
import entities.Session;
import entities.SessionInfo;
import entities.entity_fields.SessionField;

/**
 * [description]
 * <p>
 * Created on 2021.01.16.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class SessionService {
  private SessionDao sessionDao = new SessionDao();


  private String generateToken() {
    SecureRandom random = new SecureRandom();
    byte[] token = new byte[32];
    random.nextBytes(token);
    return Base64.getEncoder().encodeToString(token);
  }


  public String createSession(long userId) {
    String token = this.generateToken();
    SessionInfo sessionInfo = new SessionInfo(userId);
    this.sessionDao.add(new Session(token, sessionInfo, new Timestamp(System.currentTimeMillis())));
    return token;
  }

  public SessionInfo getSessionInfo(String token) throws RecordNotFoundException {
    return this.sessionDao.get(token).getContent().getSessionInfo();
  }

  public <V> void updateSessionInfo(String token, SessionField field, V value) {
    try {
      this.sessionDao.update(token, field, value);
    } catch (RecordNotFoundException e) {
      System.out.println("Session not found");
    }
  }
}
