package judge;

import entities.Language;

/**
 * Thrown when the given {@code Language} is not supported by the judge.
 * <p>
 * Created on 2021.01.08.
 *
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */
public class UnknownLanguageException extends Exception {
  /** The serial version ID used for serialization. */
  private static final long serialVersionUID = 1L;

  /**
   * The {@code Language} that is not recognized by the judge.
   */
  private final Language language;

  /**
   * Creates a new instance of
   * {@code UnknownLanguageException} with the
   * {@code Language} that is not recognized by the judge.
   *
   * @param language The {@code Language} that is not
   *                 recognized by the judge.
   */
  public UnknownLanguageException(Language language) {
    this.language = language;
  }

  /**
   * Returns the unsupported {@code Language}.
   *
   * @return The unsupported {@code Language}.
   */
  public Language getLanguage() {
    return this.language;
  }
}
