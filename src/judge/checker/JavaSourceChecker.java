package judge.checker;

/**
 * [description]
 * <p>
 * Created on 2021.01.08.
 *
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class JavaSourceChecker implements SourceChecker {

  public JavaSourceChecker() {
  }

  public boolean isClean(String source) {
    //TODO: get list from db
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
  
}
