package services;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

import dal.dao.RecordNotFoundException;
import dal.dao.UserDao;
import entities.Entity;
import entities.User;
import entities.UserType;

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


  public boolean isAdmin(long userId) throws RecordNotFoundException {
    User user = this.userDao.get(userId).getContent();
    return user.getUserType() == UserType.ADMIN;
  }
}
