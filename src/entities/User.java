package entities;

/**
 * An entity representing a user.
 * <p>
 * Created on 2021.01.07.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class User {
  /** The user's username. */
  private String username;
  /** The user's password. */
  private String password;
  /** The type of user this user is. */
  private UserType userType;
  /** The salt used for hashing. */
  private String salt;

  /**
   * Constructs a new User.
   *
   * @param username the user's username.
   * @param password the user's password.
   * @param userType the type of user this user is.
   * @param salt the salt used for hashing.
   */
  public User(String username, String password, UserType userType, String salt) {
    this.username = username;
    this.password = password;
    this.userType = userType;
    this.salt = salt;
  }

  /**
   * Retrieves the user's username.
   *
   * @return the user's username.
   */
  public String getUsername() {
    return this.username;
  }

  /**
   * Retrieves the user's password.
   *
   * @return the user's password.
   */
  public String getPassword() {
    return this.password;
  }

  /**
   * Retrieves the type of the user this user is.
   *
   * @return the type of the user this user is.
   */
  public UserType getUserType() {
    return this.userType;
  }

  /**
   * Retrieves the salt used to hash the password.
   *
   * @return the salt used to hash the password.
   */
  public String getSalt() {
    return this.salt;
  }

}
