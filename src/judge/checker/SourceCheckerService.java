package judge.checker;

import entities.Submission;
import judge.UnknownLanguageException;

/**
 * This class consists exclusively of static methods that check if
 * a submission's code is clean (does not contain any illegal segment).
 * <p>
 * Created on 2021.01.08.
 *
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */
public class SourceCheckerService {
  // prevent instantiation of class
  private SourceCheckerService() {
  }

  /**
   * Checks if a submission's code is clean (does not contain any illegal segment).
   *
   * @param submission The submission to be checked.
   * @throws UnknownLanguageException if the submission's language is does not match
   *                                  any of the supported languages.
   */
  public static boolean isClean(Submission submission) throws UnknownLanguageException {
    SourceChecker checker = SourceCheckerService.getChecker(submission);
    return checker.isClean(submission.getCode());
  }

  /**
   * Returns the corresponding {@code SourceChecker} of the given submission,
   * according to the submission's language.
   *
   * @param submission The submission containing the language information.
   * @throws UnknownLanguageException if the submission's language is does not match
   *                                  any of the supported languages.
   */
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
