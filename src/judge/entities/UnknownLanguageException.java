package judge.entities;

import entities.Language;

@SuppressWarnings("serial")
public class UnknownLanguageException extends UserException {
  
  private final Language language;
  
  public UnknownLanguageException(Language language) {
    this.language = language;
  }
  
  public Language getLanguage() {
    return this.language;
  }
}
