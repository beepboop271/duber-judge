package templater.compiler.tokeniser;

public class UnknownTokenException extends Exception {
  private static final long serialVersionUID = 0L;

  public UnknownTokenException(String message) {
    super(message);
  }
}
