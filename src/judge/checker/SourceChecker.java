package judge.checker;

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

public interface SourceChecker {
  public boolean isClean(String source);
  
  public Language getLanguage();
}