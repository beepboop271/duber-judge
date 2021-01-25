package templater.compiler;

import java.lang.reflect.Array;  // thanks type erasure
import java.util.Collection;
import java.util.NoSuchElementException;

/**
 * A random access queue. The implementation is very similar
 * to {@link java.util.ArrayDeque}, using a circular buffer
 * that is copied into a larger one once filled. Unlike
 * ArrayDeque, this class supports the get method to
 * retrive by index.
 *
 * @param <E> The type of elements to hold.
 * @author Kevin Qiao
 * @version 1.0
 */
public class ArrayListQueue<E> {
  /** The minimum capacity of an ArrayListQueue. */
  private static final int MIN_CAPACITY = 32;

  /** The circular array to store elements in. */
  private E[] arr;
  /**
   * The current capacity of the array. Always a power of 2.
   */
  private int capacity;
  /** The amount of elements currently stored in the array. */
  private int length;

  /**
   * The index of the array that is the head of the queue. The
   * index of the first element.
   */
  private int head;
  /**
   * The index of the array that is the tail of this queue.
   * The index right after the last filled index, or the index
   * that a new item would be inserted into.
   */
  private int tail;

  /**
   * Constructs an empty ArrayListQueue with at least as much
   * capacity as requested.
   *
   * @param requestedCapacity The minimum capacity.
   */
  @SuppressWarnings("unchecked")
  public ArrayListQueue(int requestedCapacity) {
    this.capacity = ArrayListQueue.determineCapacity(requestedCapacity);
    this.length = 0;
    this.arr = (E[])(new Object[this.capacity]);

    this.head = 0;
    this.tail = 0;
  }

  /**
   * Constructs an empty ArrayListQueue with the default
   * minimum capacity.
   */
  public ArrayListQueue() {
    this(ArrayListQueue.MIN_CAPACITY);
  }

  /**
   * Constructs an ArrayListQueue containing the elements from
   * the given collection, in the order they are returned by
   * the collection's iterator. The capacity will be greater
   * than or equal to the size of the collection.
   *
   * @param c The collection to initialize the queue with.
   */
  public ArrayListQueue(Collection<E> c) {
    this(c, 0);
  }

  /**
   * Constructs an ArrayListQueue containing the elements from
   * the given collection, in the order they are returned by
   * the collection's iterator. The capacity will be greater
   * than or equal to both the size of the collection and the
   * requestedCapacity.
   *
   * @param c                 The collection to initialize the
   *                          queue with.
   * @param requestedCapacity The minimum capacity.
   */
  @SuppressWarnings("unchecked")
  public ArrayListQueue(Collection<E> c, int requestedCapacity) {
    this((E[])(c.toArray()), requestedCapacity);
  }

  /**
   * Constructs an ArrayListQueue containing the elements from
   * the given array. The capacity will be greater than or
   * equal to the length of the array.
   *
   * @param arr The array to initialize the queue with.
   */
  public ArrayListQueue(E[] arr) {
    this(arr, 0);
  }

  /**
   * Constructs an ArrayListQueue containing the elements from
   * the given array. The capacity will be greater than or
   * equal to both the size of the collection and the
   * requestedCapacity.
   *
   * @param arr               The array to initialize the
   *                          queue with.
   * @param requestedCapacity The minimum capacity.
   */
  public ArrayListQueue(E[] arr, int requestedCapacity) {
    this(Math.max(arr.length+ArrayListQueue.MIN_CAPACITY, requestedCapacity));

    System.arraycopy(arr, 0, this.arr, 0, arr.length);
    this.length = arr.length;
    this.tail = this.length;
  }

  /**
   * Determine the smallest valid capacity which is greater
   * than or equal to the requested capacity.
   *
   * @param requestedCapacity The minimum capacity.
   * @return The smallest valid capacity which is greater than
   *         or equal to the requested capacity.
   */
  private static int determineCapacity(int requestedCapacity) {
    if (requestedCapacity < 0) {
      throw new NegativeArraySizeException();
    }
    if (requestedCapacity <= ArrayListQueue.MIN_CAPACITY) {
      return ArrayListQueue.MIN_CAPACITY;
    }

    int highestBit = Integer.highestOneBit(requestedCapacity);
    if (highestBit == requestedCapacity) {
      return requestedCapacity;
    }
    return highestBit << 1;
  }

  /**
   * Copies a section of this queue into the start of the
   * given array. Assumes all numbers provided are valid.
   *
   * @param newArr The array to copy into.
   * @param head   The index to start copying from, inclusive.
   * @param tail   The index to stop copying at, exclusive.
   * @param length The number of elements to copy.
   */
  private void straightenInto(E[] newArr, int head, int tail, int length) {
    if (length <= 0) {
      return;
    }
    if (head < tail) {
      // ...H#####T....
      //    ^copy^^
      System.arraycopy(this.arr, head, newArr, 0, length);
    } else {
      // #####T...H####  |  also if head == tail from being full
      // ^copy^   ^copy
      int trailingLength = this.capacity-head;
      System.arraycopy(this.arr, head, newArr, 0, trailingLength);
      System.arraycopy(this.arr, 0, newArr, trailingLength, tail);
    }
  }

  /**
   * Replaces the backing array with a new one of the
   * requested capacity, copying elements into the start of
   * the new array.
   *
   * @param newCapacity The new capacity of the queue.
   */
  @SuppressWarnings("unchecked")
  private void growCapacityTo(int newCapacity) {
    E[] newArr = (E[])(new Object[newCapacity]);

    this.straightenInto(newArr, this.head, this.tail, this.length);

    this.capacity = newCapacity;
    this.arr = newArr;
    this.head = 0;
    this.tail = this.length;
  }

  /**
   * Doubles the capacity of this queue.
   */
  private void doubleCapacity() {
    this.growCapacityTo(this.capacity << 1);
  }

  /**
   * Increases the capacity of this queue only if the
   * requested capacity exceeds the current capacity.
   *
   * @param capacity The minimum capacity.
   * @return Whether or not a resize and copy happened.
   */
  private boolean ensureCapacity(int capacity) {
    if (capacity <= this.capacity) {
      return false;
    }
    this.growCapacityTo(ArrayListQueue.determineCapacity(capacity));
    return true;
  }

  /**
   * Inserts an element into the end of the queue.
   *
   * @param e The element to insert.
   */
  public void add(E e) {
    this.arr[this.tail] = e;
    ++this.length;
    // equivalent to remainder since capacity is always power of 2
    this.tail = (this.tail+1) & (this.capacity-1);
    // when tail wraps around the end and meets head, the
    // array is full
    if (this.tail == this.head) {
      this.doubleCapacity();
    }
  }

  /**
   * Inserts the elements from the given collection into the
   * queue, in the order they are returned by the collection's
   * iterator.
   *
   * @param c The collection to insert elements from.
   */
  @SuppressWarnings("unchecked")
  public void add(Collection<E> c) {
    this.add((E[])(c.toArray()));
  }

  /**
   * Inserts the elements from the given array into the queue.
   *
   * @param c The array to insert elements from.
   */
  public void add(E[] c) {
    if (
      this.ensureCapacity(this.length + c.length + 1)
        || (this.tail < this.head)
        || (this.tail+c.length <= this.capacity)
    ) {
      // ensureCapacity returned true so the array must be
      // moved at the start with enough space at the end
      // OR
      // tail is below/equal to head but there is enough
      // capacity which means all the empty space must be
      // between tail and head (enough space)
      // OR
      // tail must be after head but there is enough capacity
      System.arraycopy(c, 0, this.arr, this.tail, c.length);
    } else {
      // only case here is when tail is equal to or after head,
      // but the char[] must be split up so that tail wraps
      // around to the left
      int trailingLength = this.capacity-this.tail;
      System.arraycopy(c, 0, this.arr, this.tail, trailingLength);
      System.arraycopy(c, trailingLength, this.arr, 0, c.length-trailingLength);
    }

    this.length += c.length;
    this.tail = (this.tail+c.length) & (this.capacity-1);
  }

  /**
   * Removes and retrieves a single element from the front of
   * the queue.
   *
   * @return The element removed.
   * @throws NoSuchElementException If the queue is empty.
   */
  public E remove() throws NoSuchElementException {
    E element = this.element();
    --this.length;
    this.head = (this.head+1) & (this.capacity-1);
    return element;
  }

  /**
   * Removes the requested number of elements from the front
   * of the queue.
   *
   * @param numToRemove The number of elements to remove.
   * @throws NoSuchElementException If the number of elements
   *                                to remove exceeds the
   *                                length of the queue. Note
   *                                that the queue will be
   *                                empty if this is thrown.
   */
  public void remove(int numToRemove) throws NoSuchElementException {
    for (int i = 0; i < numToRemove; ++i) {
      this.remove();
    }
  }

  /**
   * Retrieves, but does not remove, a single element from the
   * front of the queue.
   *
   * @return The first element in the queue.
   * @throws NoSuchElementException If the queue is empty.
   */
  public E element() throws NoSuchElementException {
    if (this.head == this.tail) {
      throw new NoSuchElementException();
    }

    return this.arr[this.head];
  }

  /**
   * Gets the element in the queue at the specified zero-based
   * index.
   *
   * @param index The index to access.
   * @return The element at the specified index.
   * @throws IndexOutOfBoundsException If the index is out of
   *                                   range of this queue.
   */
  public E get(int index) {
    if ((index < 0) || (index >= this.length)) {
      throw new IndexOutOfBoundsException();
    }

    return this.arr[(this.head+index) & (this.capacity-1)];
  }

  /**
   * Gets an ArrayListQueue.Iterator over the elements in this
   * queue.
   *
   * @return An ArrayListQueue.Iterator over the elements in
   *         this queue.
   */
  public ArrayListQueue<E>.Iterator iterator() {
    return this.new Iterator();
  }

  /**
   * Gets the number of elements in the queue.
   *
   * @return The number of elements in the queue.
   */
  public int size() {
    return this.length;
  }

  // i dislike type erasure
  // https://stackoverflow.com/questions/529085/how-to-create-a-generic-array-in-java
  // Internally, it is okay to create an array of E's upper
  // bound, i.e. (E[])(new Object[length]), but it is not safe
  // to pass out an array casted like this, since it will
  // compile as an E[] and can be passed to methods expecting
  // an array of a concrete type say Integer[], but is still
  // actually an Object[], causing ClassCastException. Thus,
  // reflection must be used to create a new array of the
  // runtime type of the given array - this is essentially a
  // copy of the way all java.util.Collection classes
  // implement toArray

  /**
   * Fills the given array with the elements in the specified
   * section of the queue, or creates a new array with the
   * runtime type of the given array if the array is not long
   * enough.
   *
   * @param arr    The array to fill or use as a runtime type
   *               reference to create a new array.
   * @param head   The index to start copying from, inclusive.
   * @param tail   The index to stop copying at, exclusive.
   * @param length The number of elements to copy.
   * @return Either the original array, if it was large enough
   *         to hold the copy, or a new array of the same
   *         runtime type as the given array containing a copy
   *         of the elements in the specified section of this
   *         queue.
   */
  @SuppressWarnings("unchecked")
  private E[] createOrFillArray(E[] arr, int head, int tail, int length) {
    E[] newArr = arr;
    if (newArr.length < length) {
      newArr = (E[])(Array.newInstance(arr.getClass(), length));
    }
    this.straightenInto(newArr, head, tail, length);
    if (newArr.length > length) {
      newArr[length] = null;
    }
    return newArr;
  }

  /**
   * Converts this queue into an array by either copying the
   * elements of this queue into the array provided, or by
   * copying elements into a new array with the same runtime
   * type of the given array.
   *
   * @param arr The array to fill or use as a runtime type
   *            reference to create a new array.
   * @return Either the original array, if it was large enough
   *         to hold the copy, or a new array of the same
   *         runtime type as the given array containing a copy
   *         of the elements in this queue.
   */
  public E[] toArray(E[] arr) {
    return this.createOrFillArray(arr, this.head, this.tail, this.length);
  }

  /**
   * Converts the specified section of this queue into an
   * array by either copying the elements of this queue into
   * the array provided, or by copying elements into a new
   * array with the same runtime type of the given array.
   *
   * @param arr    The array to fill or use as a runtime type
   *               reference to create a new array.
   * @param offset The index to start copying from, inclusive.
   * @param length The number of elements to copy.
   * @return Either the original array, if it was large enough
   *         to hold the copy, or a new array of the same
   *         runtime type as the given array containing a copy
   *         of the requested section of this queue.
   * @throws IndexOutOfBoundsException If the requested
   *                                   section was not a valid
   *                                   section (out of
   *                                   bounds).
   */
  public E[] toArray(E[] arr, int offset, int length) {
    if ((offset < 0) || (length < 0) || (offset+length > this.length)) {
      throw new IndexOutOfBoundsException();
    }
    int head = (this.head+offset) & (this.capacity-1);
    int tail = (head+length) & (this.capacity-1);

    return this.createOrFillArray(arr, head, tail, length);
  }

  /**
   * An iterator which supports removal of all elements up
   * until the current one, as well as getting the current
   * index. The queue this iterator is iterating over can be
   * added to whilst iterating but must not be removed from
   * (aside from using the provided method). No checks are
   * made for this, but the iterator will silently start
   * returning incorrect values.
   */
  public class Iterator implements java.util.Iterator<E> {
    /**
     * The index in the queue this iterator is at. The index of
     * the element that would be returned by the next call to
     * next.
     */
    private int index;

    /**
     * Creates a new iterator at the front of the queue.
     */
    public Iterator() {
      this.index = 0;
    }

    @Override
    public boolean hasNext() {
      return this.index < ArrayListQueue.this.length;
    }

    @Override
    public E next() {
      try {
        return ArrayListQueue.this.get(this.index++);
      } catch (IndexOutOfBoundsException e) {
        throw new NoSuchElementException();
      }
    }

    /**
     * Removes all elements in the queue up to and including
     * the last element returned by next.
     */
    public void consumeRead() {
      ArrayListQueue.this.remove(this.index);
      this.index = 0;
    }

    /**
     * Gets the index in the queue that this iterator is at. The
     * index of the element that would be returned by the next
     * call to next.
     *
     * @return The index in the queue this iterator is at, the
     *         index of the element returned by the next call to
     *         next.
     */
    public int getIndex() {
      return this.index;
    }

    /**
     * Sets the index in queue queue that this iterator is at.
     *
     * @param index The index of the element that would be
     *              returned by the next call to next.
     */
    protected void setIndex(int index) {
      this.index = index;
    }
  }
}
