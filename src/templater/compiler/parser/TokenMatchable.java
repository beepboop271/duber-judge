package templater.compiler.parser;

import templater.compiler.Matchable;

/**
 * Matches objects off of a TokenQueue.Iterator. This class
 * manages the mark stack of the iterator so that failed
 * matches don't permanently consume any parts of the
 * iterator.
 *
 * @param <T> The type of object to produce from the
 *            TokenQueue.
 */
abstract class TokenMatchable<T> implements Matchable<TokenQueue.Iterator, T> {
  /**
   * {@inheritDoc} This method wraps tryMatchInternal and
   * manages the mark stack of the TokenQueue iterator.
   * Implementing classes fill in tryMatchInternal but always
   * attempt matches using tryMatch so that the mark stack is
   * always accurate. Before attempting a match, the position
   * in the iterator is saved. Upon finishing the match, the
   * position is popped, but if the match failed, the iterator
   * is also reset, so that another match attempt can be made
   * on the same section of the iterator.
   */
  @Override
  public final T tryMatch(TokenQueue.Iterator input) {
    input.mark();
    T result = this.tryMatchInternal(input);
    if (result == null) {
      input.reset();
      return null;
    }
    input.pop();
    return result;
  }

  /**
   * Attempts to match a new object of type T on the given
   * input, returning T if successful and null otherwise.
   *
   * @param input The TokenQueue input to match on.
   * @return T if a match was made, null otherwise.
   */
  protected abstract T tryMatchInternal(TokenQueue.Iterator input);
}
