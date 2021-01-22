package judge;

/**
 * [description]
 * <p>
 * Created on 2021.01.08.
 *
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class CompileErrorException extends Exception {
  private static final long serialVersionUID = 1L;

  public CompileErrorException(String message) {
    super(message);
  }
}
