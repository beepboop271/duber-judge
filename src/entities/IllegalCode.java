package entities;

/**
 * [description]
 * <p>
 * Created on 2021.01.22.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class IllegalCode {
  private Language language;
  private String content;

  public IllegalCode(Language language, String content) {
    this.language = language;
    this.content = content;
  }

  public Language getLanguage() {
    return this.language;
  }

  public String getContent() {
    return this.content;
  }
}
