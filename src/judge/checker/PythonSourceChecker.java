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

public class PythonSourceChecker implements SourceChecker {

  public PythonSourceChecker() {
  }

  @Override
  public boolean isClean(String source) {
    //TODO: get list externally
    String[] illegalSegments = new String[] {
      "import os"
    };
    for (String segment : illegalSegments) {
      if (source.contains(segment)) {
        return false;
      }
    }
    return true;
  }
  
  @Override
  public Language getLanguage() {
    return Language.PYTHON;
  }
}