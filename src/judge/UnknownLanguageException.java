package judge;

import entities.Language;

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
