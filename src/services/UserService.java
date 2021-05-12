package services;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;

import dal.dao.ContestSessionDao;
import dal.dao.RecordNotFoundException;
import dal.dao.SubmissionDao;
import dal.dao.UserDao;
import entities.ContestSession;
import entities.ContestSessionStatus;
import entities.Entity;
import entities.SubmissionResult;
import entities.User;
import entities.UserType;
import entities.entity_fields.UserField;

/**
 * Handles all the logic relating to users, such as registering
 * new users, logging in, getting user profile information, etc.
 * <p>
 * Created on 2021.01.11.
 *
 * @author Shari Sun, Candice Zhang, Joseph Wang
 * @version 1.0.0
 * @since 1.0.0
 */
public class UserService {
  private UserDao userDao;
  private SubmissionDao submissionDao;
  private ContestSessionDao contestSessionDao;

  /**
   * Creates a new user service.
   */
  public UserService() {
    this.userDao = new UserDao();
    this.submissionDao = new SubmissionDao();
    this.contestSessionDao = new ContestSessionDao();
  }

  /**
   * Generates a random 16bytes salt.
   *
   * @return    The base64 encoded salt.
   */
  private String generateSalt() {
    SecureRandom random = new SecureRandom();
    byte[] salt = new byte[16];
    random.nextBytes(salt);
    return Base64.getEncoder().encodeToString(salt);
  }

  /**
   * Appends a given salt to the end of the plain-text password
   * and hashes it using SHA-256.
   *
   * @param password     The plain-text password.
   * @param salt         The salt.
   * @return             The hashed password as a base64 string.
   */
  private String hashPassword(String password, String salt) {
    String hashed = null;
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hashedBytes = digest.digest((password+salt).getBytes());
      hashed = Base64.getEncoder().encodeToString(hashedBytes);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return hashed;
  }

  /**
   * Ensures that the username meets the requirements.
   * <p>
   * A valid username should be at least 3 characters long,
   * and should only contain letters, numbers, underscores,
   * and dashes.
   *
   * @param username the username to check for
   * @return whether or not it's valid
   */
  private boolean validateUsername(String username) {
    return username.matches("^([a-zA-Z0-9_-]{3,20})$");
  }

  /**
   * Ensures that the password meets the requirements.
   * <p>
   * A valid password should be at least 6 characters long and
   * include a mix of letters and numbers. It should not
   * contain any whitespace character.
   *
   * @param password the password to check for
   * @return whether or not the password's valid
   */
  private boolean validatePassword(String password) {
    return password.matches("^(?=.*[0-9])(?=.*[a-zA-Z])\\S{6,25}$");
  }

  /**
   * Validates the desired user credentials.
   *
   * @param username                    The username.
   * @param password                    The password.
   * @throws IllegalArgumentException   If the desired credentials are invalid.
   * @see #validateUsername(String)
   * @see #validatePassword(String)
   */
  private void validateUser(String username, String password)
    throws IllegalArgumentException {
    if (!this.validateUsername(username)) {
      throw new IllegalArgumentException(
        InvalidArguments.BAD_USERNAME.toString()
      );
    }
    if (!this.validatePassword(password)) {
      throw new IllegalArgumentException(
        InvalidArguments.INSECURE_PASSWORD.toString()
      );
    }
  }

  /**
   * Creates a new user.
   *
   * @param username                     The username.
   * @param password                     The password.
   * @return                             The user ID.
   * @throws IllegalArgumentException    If the desired username or password is invalid.
   */
  public long createUser(String username, String password)
    throws IllegalArgumentException {
    this.validateUser(username, password);
    String salt = this.generateSalt();
    String hashed = this.hashPassword(password, salt);
    return this.userDao
      .add(new User(username, hashed, UserType.STANDARD, salt));
  }


  /**
   * Creates a user with admin privileges.
   * Check {@link AdminService} to learn more about admin.
   *
   * @param username                  The username.
   * @param password                  The password.
   * @return                          The user ID.
   * @throws IllegalArgumentException If the desired username or password is invalid.
   */
  public long createAdmin(String username, String password)
    throws IllegalArgumentException {
    this.validateUser(username, password);
    String salt = this.generateSalt();
    String hashed = this.hashPassword(password, salt);
    return this.userDao.add(new User(username, hashed, UserType.ADMIN, salt));
  }

  /**
   * Attempts to log in the user given their username and password.
   * If the login is a success, it returns the user ID, otherwise,
   * it throws an exception.
   *
   * @param username                   The username.
   * @param password                   The password.
   * @return                           The user ID.
   * @throws IllegalArgumentException  Incorrect username or password.
   */
  public long login(String username, String password)
    throws IllegalArgumentException {
    long id = 0;
    try {
      Entity<User> user = this.userDao.getByUsername(username);
      String hashed = this.hashPassword(password, user.getContent().getSalt());
      if (!hashed.equals(user.getContent().getPassword())) {
        throw new IllegalArgumentException(
          InvalidArguments.INVALID_CREDENTIALS.toString()
        );
      }
      id = user.getId();
    } catch (RecordNotFoundException e) {
      throw new IllegalArgumentException(
        InvalidArguments.INVALID_CREDENTIALS.toString()
      );
    }
    return id;
  }

  /**
   * Given a user ID and a username, check if they refer to the same user.
   *
   * @param userId     The user ID.
   * @param username   The username.
   * @return           Whether or not they are the same user.
   */
  public boolean isSameUser(long userId, String username) {
    try {
      return this.userDao.get(userId).getContent().getUsername().equals(username);
    } catch (RecordNotFoundException e) {
      return false;
    }
  }

  /**
   * Updates a user profile field,
   * such as their username, description or profile picture.
   *
   * @param <T>                       The type of value the new field is.
   * @param userId                    The user's ID.
   * @param field                     The field that is being updated.
   * @param value                     The new value of the field.
   * @throws RecordNotFoundException  If the user is not found.
   * @throws IllegalArgumentException If the new value is invalid.
   * @see    InvalidArguments
   */
  public <T> void updateUserProfile(long userId, UserField field, T value)
    throws RecordNotFoundException, IllegalArgumentException {
    switch (field) {
      case USERNAME:
        this.validateUsername((String)value);
        break;
    }
    this.userDao.update(userId, field, value);
  }

  /**
   * Updates a user's password given their old password and new desired password.
   *
   * @param userId                    The user's ID.
   * @param oldPassword               Their old password.
   * @param newPassword               The new desired password.
   * @throws IllegalArgumentException If the user is not found.
   */
  public void updateUserPassword(
    long userId,
    String oldPassword,
    String newPassword
  ) throws IllegalArgumentException {
    try {
      Entity<User> user = this.userDao.get(userId);
      String hashed =
        this.hashPassword(oldPassword, user.getContent().getSalt());
      if (!hashed.equals(user.getContent().getPassword())) {
        throw new IllegalArgumentException(
          InvalidArguments.INVALID_CREDENTIALS.toString()
        );
      }

      String newSalt = this.generateSalt();
      String newHashed = this.hashPassword(newPassword, newSalt);

      this.userDao.updatePassword(userId, newSalt, newHashed);
    } catch (RecordNotFoundException e) {
      throw new IllegalArgumentException(
        InvalidArguments.INVALID_CREDENTIALS.toString()
      );
    }
  }

  /**
   * Checks whether or not this user is an admin.
   *
   * @param userId                    The user's ID.
   * @return                          Whether or not the user is an admin.
   * @throws RecordNotFoundException  If the user does not exist.
   * @see AdminService
   */
  public boolean isAdmin(long userId) throws RecordNotFoundException {
    User user = this.userDao.get(userId).getContent();
    return user.getUserType() == UserType.ADMIN;
  }

  /**
   * Gets the problems that the user has attempted in order
   * from greatest points to least points. These problems do
   * not include contest problems.
   *
   * @param userId      the user's ID
   * @param index       the index
   * @param numProblems the number of problems to retrieve
   * @return a list of problems
   */
  public ArrayList<Entity<SubmissionResult>> getProblems(
    long userId,
    int index,
    int numProblems
  ) {
    return this.submissionDao.getUniqueSubmissions(userId, index, numProblems);
  }

  /**
   * Get all the submissions the user has from latest to
   * earliest.
   *
   * @param userId         the user's ID
   * @param index          the index
   * @param numSubmissions the number of submissions to
   *                       retrieve
   * @return a list of submissions
   */
  public ArrayList<Entity<SubmissionResult>> getSubmissions(
    long userId,
    int index,
    int numSubmissions
  ) {
    return this.submissionDao.getByUser(userId, index, numSubmissions);
  }

  /**
   * Get the user's submissions by problem.
   *
   * @param userId         the user's ID
   * @param problemId      the problem's ID
   * @param index          the index
   * @param numSubmissions the number of submissions to
   *                       retrieve
   * @return a list of submissions
   */
  public ArrayList<Entity<SubmissionResult>> getProblemSubmissions(
    long userId,
    long problemId,
    int index,
    int numSubmissions
  ) {
    return this.submissionDao
      .getByUserAndProblem(userId, problemId, index, numSubmissions);
  }

  /**
   * Get all the contests that a user is currently
   * participating in.
   *
   * @param userId the user's ID
   * @return a list of active contests
   */
  public ArrayList<Entity<ContestSession>> getActiveContests(long userId) {
    return this.contestSessionDao.getContestsByStatus(userId, ContestSessionStatus.ONGOING);
  }

  /**
   * Gets the number of contests that the user is currently participating in.
   *
   * @param userId      The user's ID.
   * @return            The number of contests.
   */
  public int getNumActiveContests(long userId) {
    return this.contestSessionDao.getNumContests(userId);
  }

  /**
   * Gets the contests that the user is currently participating in
   * and their corresponding statuses.
   *
   * @param userId          The user's ID.
   * @param index           The index used for pagination.
   * @param numSessions     The number of contests to retrieve for pagination.
   * @return                A list of all contests.
   */
  public ArrayList<Entity<ContestSession>> getActiveSessions(
    long userId,
    int index,
    int numSessions
  ) {
    return this.contestSessionDao.getByUser(userId, index, numSessions);
  }

  /**
   * Get all the contests a user has participated in.
   *
   * @param userId the user's ID
   * @return a list of all contests a user participated in
   */
  public ArrayList<Entity<ContestSession>> getParticipatedContests(long userId) {
    return this.contestSessionDao.getContestsByStatus(userId, ContestSessionStatus.OVER);
  }

  /**
   * Get the number of times a user has submitted to a single
   * problem.
   *
   * @param userId    the user ID
   * @param problemId the problem ID
   * @return the number of times the user has submitted
   */
  public int getSubmissionCount(long userId, long problemId) {
    return this.submissionDao.countByUserAndProblem(userId, problemId);
  }

  /**
   * Gets the total number of points a user has.
   *
   * @param userId     The user's ID.
   * @return           The number of points a user has.
   */
  public int getPoints(long userId) {
    return this.userDao.getPoints(userId);
  }
}
