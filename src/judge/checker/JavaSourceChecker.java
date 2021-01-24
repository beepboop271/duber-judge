package judge.checker;

import entities.Language;

/**
 * An {@code SourceChecker} that checks if a given piece of java code
 * contains language-specific illegal segments.
 * <p>
 * Created on 2021.01.08.
 *
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */
public class JavaSourceChecker implements SourceChecker {
  // prevent instantiation of class
  public JavaSourceChecker() {
  }

  @Override
  public boolean isClean(String source) {
    //TODO: get list externally
    String[] illegalSegments = new String[] {
      "ProcessBuilder",
      "Process"
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
    return Language.JAVA;
  }
}
