package services;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.Base64;

import dal.dao.RecordNotFoundException;
import entities.Session;
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

  private String generateToken() {
    SecureRandom random = new SecureRandom();
    byte[] token = new byte[32];
    random.nextBytes(token);
    return Base64.getEncoder().encodeToString(token);
  }


  public String createSession(long userId) {
    String token = this.generateToken();
    Session session = new Session(userId, token);
    Sessions.tokenToSessions.put(token, session);
    Sessions.sessions.add(session);
    return token;
  }

  public String createSession() {
    String token = this.generateToken();
    Session session = new Session(token);
    Sessions.tokenToSessions.put(token, session);
    Sessions.sessions.add(session);
    return token;
  }

  public Session getSession(String token) throws RecordNotFoundException {
    if (!Sessions.tokenToSessions.containsKey(token)) {
      throw new RecordNotFoundException();
    }
    return Sessions.tokenToSessions.get(token);
  }

  public <T> void updateSession(String token, SessionField field, T value) {
    if (!Sessions.tokenToSessions.containsKey(token)) {
      return;
    }
    switch (field) {
      case USER_ID:
        Sessions.tokenToSessions.get(token).setUserId((long)value);;
    }
  }

  public void updateLastActive(String token) {
    if (!Sessions.tokenToSessions.containsKey(token)) {
      return;
    }
    Session session = Sessions.tokenToSessions.get(token);
    session.updateLastActive(new Timestamp(System.currentTimeMillis()));
    Sessions.sessions.remove(session);
    Sessions.sessions.add(session);
  }

  public void deleteFromBefore(Timestamp before) {
    while (!Sessions.sessions.isEmpty()
      && Sessions.sessions.first().getLastActive().compareTo(before) < 0
    ) {
      Session session = Sessions.sessions.pollFirst();
      Sessions.tokenToSessions.remove(session.getToken());
    }
  }

}
