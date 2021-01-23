package judge;

/**
 * [description]
 * <p>
 * Created on 2021.01.22.
 *
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class ProcessNotFoundException extends Exception {
  private static final long serialVersionUID = 1L;

  public ProcessNotFoundException() {
  }

  public ProcessNotFoundException(String message) {
    super(message);
  }
}
