package judge.entities;

import entities.Submission;

/**
 * [description]
 * <p>
 * Created on 2021.01.08.
 *
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class ProgramChecker {

  public static boolean isClean(Submission submission) throws UnknownLanguageException {
    SourceChecker checker = ProgramChecker.getChecker(submission);
    return checker.isClean(submission.getCode());
  }

  private static SourceChecker getChecker(Submission submission) throws UnknownLanguageException {
    switch (submission.getLanguage()) {
      case PYTHON:
        return new PythonSourceChecker();
      case JAVA:
        return new JavaSourceChecker();
      default:
        throw new UnknownLanguageException(submission.getLanguage());
    }
  }

}
