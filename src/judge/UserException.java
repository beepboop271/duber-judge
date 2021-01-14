package judge;

@SuppressWarnings("serial")
public abstract class UserException extends Exception {

  public UserException() {
  }

  public UserException(String message) {
    super(message);
  }

  public UserException(String message, Throwable cause) {
    super(message, cause);
  }
  

}
