package judge.entities;

/**
 * [description]
 * <p>
 * Created on 2021.01.08.
 *
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

@SuppressWarnings("serial")
public class CompileErrorException extends UserException {
  public CompileErrorException(String message) {
    super(message);
  }
}