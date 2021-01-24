package services;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.Base64;

import dal.dao.RecordNotFoundException;
import entities.Session;
import entities.entity_fields.SessionField;

/**
 * A service deals with {@link Session} creation, update, and removal.
 * <p>
 * Created on 2021.01.16.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class SessionService {

  /**
   * Generates a unique 32 bytes token for the session.
   *
   * @return    The generated token.
   */
  private String generateToken() {
    SecureRandom random = new SecureRandom();
    byte[] token = new byte[32];
    random.nextBytes(token);
    return Base64.getEncoder().encodeToString(token);
  }

  /**
   * Creates a new session given a user ID.
   *
   * @param userId         The user ID.
   * @return               The session's token.
   */
  public String createSession(long userId) {
    String token = this.generateToken();
    Session session = new Session(userId, token);
    Sessions.tokenToSessions.put(token, session);
    Sessions.sessions.add(session);
    return token;
  }

  /**
   * Creates a new session without a user ID (non-logged-in user).
   *
   * @return            The session's token.
   */
  public String createSession() {
    String token = this.generateToken();
    Session session = new Session(token);
    Sessions.tokenToSessions.put(token, session);
    Sessions.sessions.add(session);
    return token;
  }

  /**
   * Gets the session given a session token.
   *
   * @param token                       The token.
   * @return                            The session.
   * @throws RecordNotFoundException    If the session is invalid.
   */
  public Session getSession(String token) throws RecordNotFoundException {
    if (!Sessions.tokenToSessions.containsKey(token)) {
      throw new RecordNotFoundException();
    }
    return Sessions.tokenToSessions.get(token);
  }

  /**
   * Updates the session data.
   *
   * @param <T>         The type of the value that is being updated.
   * @param token       The session token.
   * @param field       The field that is being updated.
   * @param value       The new value.
   * @see               SessionField
   */
  public <T> void updateSession(String token, SessionField field, T value) {
    if (!Sessions.tokenToSessions.containsKey(token)) {
      return;
    }
    switch (field) {
      case USER_ID:
        Sessions.tokenToSessions.get(token).setUserId((long)value);;
    }
  }

  /**
   * Update the last active time for the user.
   *
   * @param token        The token.
   */
  public void updateLastActive(String token) {
    if (!Sessions.tokenToSessions.containsKey(token)) {
      return;
    }
    Session session = Sessions.tokenToSessions.get(token);
    Sessions.sessions.remove(session);
    session.updateLastActive(System.currentTimeMillis());
    Sessions.sessions.add(session);
  }

  /**
   * Delete all sessions from before a {@code Timestamp}.
   *
   * @param before        The time.
   */
  public void deleteFromBefore(Timestamp before) {
    while (!Sessions.sessions.isEmpty()
      && Sessions.sessions.first().getLastActive().compareTo(before) < 0
    ) {
      Session session = Sessions.sessions.pollFirst();
      Sessions.tokenToSessions.remove(session.getToken());
    }
  }

}
