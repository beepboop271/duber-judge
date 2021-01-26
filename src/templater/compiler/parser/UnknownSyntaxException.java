package templater.compiler.parser;

public class UnknownSyntaxException extends RuntimeException {
  private static final long serialVersionUID = 0L;

  public UnknownSyntaxException(String message) {
    super(message);
  }
}
