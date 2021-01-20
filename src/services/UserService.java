package services;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;

import dal.dao.ContestDao;
import dal.dao.RecordNotFoundException;
import dal.dao.SubmissionDao;
import dal.dao.UserDao;
import entities.Contest;
import entities.ContestStatus;
import entities.Entity;
import entities.Submission;
import entities.User;
import entities.UserType;
import entities.entity_fields.UserField;

/**
 * [description]
 * <p>
 * Created on 2021.01.11.
 *
 * @author Shari Sun, Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */
public class UserService {

  private UserDao userDao;
  private SubmissionDao submissionDao;
  private ContestDao contestDao;

  public UserService() {
    this.userDao = new UserDao();
    this.submissionDao = new SubmissionDao();
    this.contestDao = new ContestDao();
  }

  private String generateSalt() {
    SecureRandom random = new SecureRandom();
    byte[] salt = new byte[16];
    random.nextBytes(salt);
    return Base64.getEncoder().encodeToString(salt);
  }

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
   * and should only contain letters, numbers, underscores, and dashes.
   *
   * @param username       the username to check for
   * @return               whether or not it's valid
   */
  private boolean validateUsername(String username) {
    return username.matches("^(?=.*[a-zA-Z_-])(?=\\S+$).{3,}$");
  }

  /**
   * Ensures that the password meets the requirements.
   * <p>
   * A valid password should be at least 6 characters long and include
   * a mix of letters and numbers. It should not contain any whitespace character.
   *
   * @param password       the password to check for
   * @return               whether or not the password's valid
   */
  private boolean validatePassword(String password) {
    return password.matches("^(?=.*[0-9])(?=.*[a-zA-Z])(?=\\S+$).{6,}$");
  }

  private void validateUser(String username, String password) throws IllegalArgumentException {
    if (!this.validateUsername(username)) {
      throw new IllegalArgumentException(InvalidArguments.BAD_USERNAME.toString());
    }
    if (!this.validatePassword(password)) {
      throw new IllegalArgumentException(InvalidArguments.INSECURE_PASSWORD.toString());
    }
  }

  public long createUser(String username, String password)
    throws IllegalArgumentException {
    this.validateUser(username, password);
    String salt = this.generateSalt();
    String hashed = this.hashPassword(password, salt);
    return this.userDao.add(new User(username, hashed, UserType.STANDARD, salt));
  }

  public long createAdmin(String username, String password) throws IllegalArgumentException {
    this.validateUser(username, password);
    String salt = this.generateSalt();
    String hashed = this.hashPassword(password, salt);
    return this.userDao.add(new User(username, hashed, UserType.ADMIN, salt));
  }

  public long login(String username, String password) throws IllegalArgumentException {
    long id = 0;
    try {
      Entity<User> user = this.userDao.getByUsername(username);
      String hashed = this.hashPassword(password, user.getContent().getSalt());
      if (!hashed.equals(user.getContent().getPassword())) {
        throw new IllegalArgumentException(InvalidArguments.INVALID_CREDENTIALS.toString());
      }
      id = user.getId();
    } catch (RecordNotFoundException e) {
      throw new IllegalArgumentException(InvalidArguments.INVALID_CREDENTIALS.toString());
    }
    return id;
  }

  public <T> void updateUserProfile(
    long userId,
    UserField field,
    T value
  ) throws RecordNotFoundException {
    this.userDao.update(userId, field, value);
  }

  public void updateUserPassword(
    long userId,
    String oldPassword,
    String newPassword
  ) throws IllegalArgumentException {
    try {
      Entity<User> user = this.userDao.get(userId);
      String hashed = this.hashPassword(oldPassword, user.getContent().getSalt());
      if (!hashed.equals(user.getContent().getPassword())) {
        throw new IllegalArgumentException(InvalidArguments.INVALID_CREDENTIALS.toString());
      }

      String newSalt = this.generateSalt();
      String newHashed = this.hashPassword(newPassword, newSalt);

      this.userDao.updatePassword(userId, newSalt, newHashed);
    } catch (RecordNotFoundException e) {
      throw new IllegalArgumentException(InvalidArguments.INVALID_CREDENTIALS.toString());
    }
  }


  public boolean isAdmin(long userId) throws RecordNotFoundException {
    User user = this.userDao.get(userId).getContent();
    return user.getUserType() == UserType.ADMIN;
  }

  /**
   * Gets the problems that the user has attempted in order from greatest points to least points.
   * These problems do not include contest problems.
   *
   * @param userId           the user's ID
   * @param index            the index
   * @param numProblems      the number of problems to retrieve
   * @return                 a list of problems
   */
  public ArrayList<Entity<Submission>> getProblems(
    long userId,
    int index,
    int numProblems
  ) {
    return this.submissionDao.getUniqueSubmissions(userId, index, numProblems);
  }


  /**
   * Get all the submissions the user has from latest to earliest.
   *
   * @param userId               the user's ID
   * @param index                the index
   * @param numSubmissions       the number of submissions to retrieve
   * @return                     a list of submissions
   */
  public ArrayList<Entity<Submission>> getSubmissions(
    long userId,
    int index,
    int numSubmissions
  ) {
    return this.submissionDao.getByUser(userId, index, numSubmissions);
  }


  /**
   * Get the user's submissions by problem.
   *
   * @param userId              the user's ID
   * @param problemId           the problem's ID
   * @param index               the index
   * @param numSubmissions      the number of submissions to retrieve
   * @return                    a list of submissions
   */
  public ArrayList<Entity<Submission>> getProblemSubmissions(
    long userId,
    long problemId,
    int index,
    int numSubmissions
  ) {
    return this.submissionDao.getByUserAndProblem(userId, problemId, index, numSubmissions);
  }

  /**
   * Get all the contests that a user is currently participating in.
   *
   * @param userId           the user's ID
   * @return                 a list of active contests
   */
  public ArrayList<Entity<Contest>> getActiveContests(long userId) {
    return this.contestDao.getContestsByStatus(userId, ContestStatus.ONGOING);
  }

  /**
   * Get all the contests a user has participated in.
   *
   * @param userId        the user's ID
   * @return              a list of all contests a user participated in
   */
  public ArrayList<Entity<Contest>> getParticipatedContests(long userId) {
    return this.contestDao.getContestsByStatus(userId, ContestStatus.OVER);
  }

  /**
   * Get the number of times a user has submitted to a single problem.
   *
   * @param userId                the user ID
   * @param problemId             the problem ID
   * @return                      the number of times the user has submitted
   */
  public int getSubmissionCount(long userId, long problemId) {
    return this.submissionDao.countByUserAndProblem(userId, problemId);
  }
}
