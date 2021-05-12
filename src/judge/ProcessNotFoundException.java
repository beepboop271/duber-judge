package judge;

/**
 * Thrown when a child process cannot be found when
 * searching by its process id.
 * <p>
 * Created on 2021.01.22.
 *
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */
public class ProcessNotFoundException extends Exception {
  /** The serial version ID used for serialization. */
  private static final long serialVersionUID = 1L;

  /**
   * Creates a new instance of {@code ProcessNotFoundException}.
   */
  public ProcessNotFoundException() {
  }

  /**
   * Creates a new instance of
   * {@code ProcessNotFoundException} with an error message.
   *
   * @param message The error message.
   */
  public ProcessNotFoundException(String message) {
    super(message);
  }
}
