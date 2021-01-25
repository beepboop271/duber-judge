package templater.compiler;

/**
 * Represents something that can be matched from an input.
 *
 * @param <I> The type of object to read from for matches.
 * @param <O> The type of object to match and produce.
 */
public interface Matchable<I, O> {
  /**
   * Attempts to match an object from the input.
   *
   * @param input The input object to match from.
   * @return An object of type O if the match succeeded, null
   *         otherwise.
   */
  public O tryMatch(I input);
}
