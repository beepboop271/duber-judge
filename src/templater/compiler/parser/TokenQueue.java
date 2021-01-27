package templater.compiler.parser;

import java.util.ArrayDeque;
import java.util.Collection;

import templater.compiler.ArrayListQueue;
import templater.compiler.TextFilePosition;
import templater.language.Token;

/**
 * An ArrayListQueue of {@code Token}s.
 */
public class TokenQueue extends ArrayListQueue<Token> {
  /**
   * Constructs an TokenQueue containing the elements from
   * the given collection, in the order they are returned by
   * the collection's iterator.
   *
   * @param tokens The collection to initialize the queue with.
   */
  public TokenQueue(Collection<Token> tokens) {
    super(tokens);
  }

  /**
   * Gets an TokenQueue.Iterator over the elements in this
   * queue.
   *
   * @return A TokenQueue.Iterator over the elements in this
   *         queue.
   */
  @Override
  public Iterator iterator() {
    return this.new Iterator();
  }

  /**
   * An ArrayListQueue.Iterator that also supports mark and
   * reset/pop operations in a stack. The queue this iterator
   * is iterating over can be added to whilst iterating but
   * must not be removed from (aside from using the provided
   * method). No checks are made for this, but the iterator
   * will silently start returning incorrect values.
   */
  public class Iterator extends ArrayListQueue<Token>.Iterator {
    /** The marked indices that the Iterator can be reset to. */
    private final ArrayDeque<Integer> marks;

    /**
     * Creates a new Iterator at the front of the queue.
     */
    public Iterator() {
      super();
      this.marks = new ArrayDeque<>();
    }

    /**
     * Pushes the current location of the Iterator onto the
     * stack of marks.
     */
    public void mark() {
      this.marks.push(this.getIndex());
    }

    /**
     * Pops the top mark on the stack of marks and resets this
     * Iterator's position to that marked index.
     */
    public void reset() {
      this.setIndex(this.marks.pop());
    }

    /**
     * Pops and discards the mark on the top of the stack.
     */
    public void pop() {
      this.marks.pop();
    }

    /**
     * Gets the position of the Token the iterator is currently
     * at. Returns TextFilePosition.Eof if the iterator is
     * exhausted.
     *
     * @return The position of the Token the iterator is
     *         currently.
     */
    public TextFilePosition getPosition() {
      if (this.hasNext()) {
        return TokenQueue.this.get(this.getIndex()).getPosition();
      }
      return new TextFilePosition.Eof();
    }
  }
}
