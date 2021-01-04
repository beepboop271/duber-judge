package database;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * [description]
 * <p>
 * Created on 2021.01.03.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class UserService {
  public UserService() {

  }


  public boolean newUser(String username, String password) {
    String hashed = null;
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hashedBytes = digest.digest(password.getBytes());
      hashed = Base64.getEncoder().encodeToString(hashedBytes);
    } catch (Exception e) {
      e.printStackTrace();
    }




    return true;
  }

  private String generateSalt() {
    SecureRandom random = new SecureRandom();
    byte[] salt = new byte[16];
    random.nextBytes(salt);
    return Base64.getEncoder().encodeToString(salt);
  }
}
