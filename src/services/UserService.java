package services;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Base64;

import dal.dao.RecordNotFoundException;
import dal.dao.UserDao;
import entities.Contest;
import entities.Entity;
import entities.Problem;
import entities.Submission;
import entities.User;
import entities.UserType;
import entities.entity_fields.UserField;

/**
 * [description]
 * <p>
 * Created on 2021.01.11.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class UserService {
  private UserDao userDao = new UserDao();

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
   * No special characters allowed in uesr name and it must be at least 3 characters.
   *
   * @param username       the username to check for
   * @return               whether or not it's valid
   */
  private boolean validateUsername(String username) {

  }

  /**
   * Ensures that the password is at least 6 characters long and includes
   * a mix of letters and numbers.
   *
   * @param password       the password to check for
   * @return               whether or not the password's valid
   */
  private boolean validatePassword(String password) {

  }


  public long createUser(String username, String password) throws IllegalArgumentException {

  }

  public long createAdmin(String username, String password) throws IllegalArgumentException {

  }

  public long login(String username, String password) throws IllegalArgumentException {

  }

  public <T> void updateUserProfile(long userId, UserField field, T value) {

  }

  public void updateUserPassword(
    long userId,
    String oldPassword,
    String newPassword
  ) throws IllegalArgumentException {

  }


  public boolean isAdmin(long userId) throws RecordNotFoundException {
    User user = this.userDao.get(userId).getContent();
    return user.getUserType() == UserType.ADMIN;
  }



  public ArrayList<Entity<Submission>> getSolvedProblemsByTime(
    long userId,
    Timestamp before,
    int numProblems
  ) {

  }


  public ArrayList<Entity<Submission>> getSolvedProblemsByPoints(
    long userId,
    int index,
    int numProblems
  ) {

  }


  public ArrayList<Entity<Submission>> getSubmissions(long userId, Timestamp before, int numSubmissions) {

  }


  public ArrayList<Entity<Submission>> getProblemSubmissions(
    long userId,
    long problemId,
    int index,
    int numSubmissions
  ) {

  }

  public ArrayList<Entity<Contest>> getACtiveContests(long userId) {

  }

  public ArrayList<Entity<Contest>> getParticipatedContests(long userId) {

  }

  public ArrayList<Entity<Problem>> getSolvedContestProblems(long usrId, long contestId) {

  }

}
