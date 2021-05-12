package judge.checker;

import entities.Language;

/**
 * An interface for objects that checks if a given piece of code
 * contains language-specific illegal segments.
 * <p>
 * Created on 2021.01.08.
 *
 * @author Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */
public interface SourceChecker {
  /**
   * Tests whether a string of code is clean (not containing any illegal segment).
   *
   * @param source The string of code to test.
   */
  public boolean isClean(String source);

  /**
   * Returns the language of the code to be tested.
   */
  public Language getLanguage();
}