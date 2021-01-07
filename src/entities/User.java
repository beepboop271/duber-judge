package entities;


/**
 * [description]
 * <p>
 * Created on 2021.01.07.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class User {
  private String username;
  private String password;
  private UserType userType;
  private String salt;

  public User(String username, String password, UserType userType, String salt) {
    this.username = username;
    this.password = password;
    this.userType = userType;
    this.salt = salt;
  }

  public String getUsername() {
    return this.username;
  }

  public String getPassword() {
    return this.password;
  }

  public UserType getUserType() {
    return this.userType;
  }

  public String getSalt() {
    return this.salt;
  }

}
