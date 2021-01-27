package judge;

/**
 * An {@code Exception} thrown when a submitted program
 * fails to compile.
 * <p>
 * Created on 2021.01.08.
 *
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */
public class CompileErrorException extends Exception {
  /** The serial version ID used for serialization. */
  private static final long serialVersionUID = 1L;

  /**
   * Creates a new {@code CompileErrorException} instance with
   * an error message.
   *
   * @param message The error message.
   */
  public CompileErrorException(String message) {
    super(message);
  }
}
