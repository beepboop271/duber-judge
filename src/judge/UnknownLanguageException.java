package judge;

import entities.Language;

/**
 * [description]
 * <p>
 * Created on 2021.01.08.
 *
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class UnknownLanguageException extends UserException {
  private static final long serialVersionUID = 1L;
  
  private final Language language;
  
  public UnknownLanguageException(Language language) {
    this.language = language;
  }
  
  public Language getLanguage() {
    return this.language;
  }
}
