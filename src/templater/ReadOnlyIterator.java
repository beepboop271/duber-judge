package templater;

import java.util.Iterator;

/**
 * A class which provides an {@code Iterator} that can only be
 * read from. The class wraps another {@code Iterator} but
 * throws an {@code UnsupportedOperationException} when
 * {@link java.util.Iterator#remove()} is called.
 *
 * @author Kevin Qiao
 * @version 1.0
 */
public class ReadOnlyIterator<E> implements Iterator<E>, Iterable<E> {
  /** The wrapped iterator to read from. */
  private final Iterator<E> iterator;

  /**
   * Creates a new {@code ReadOnlyIterator} which wraps the
   * given {@code Iterator}.
   *
   * @param iterator The {@code Iterator} to wrap in a
   *                 read-only class.
   */
  public ReadOnlyIterator(Iterator<E> iterator) {
    this.iterator = iterator;
  }

  @Override
  public boolean hasNext() {
    return this.iterator.hasNext();
  }

  @Override
  public E next() {
    return this.iterator.next();
  }

  /**
   * Throws an {@code UnsupportedOperationException}.
   */
  @Override
  public void remove() {
    throw new UnsupportedOperationException(
      "Cannot remove from a ReadOnlyIterator"
    );
  }

  @Override
  public ReadOnlyIterator<E> iterator() {
    return this;
  }
}
