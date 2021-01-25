package templater.compiler;

import java.lang.reflect.Array;  // thanks type erasure
import java.util.Collection;
import java.util.NoSuchElementException;

public class ArrayListQueue<E> {
  private static final int MIN_CAPACITY = 32;

  private E[] arr;
  private int capacity;
  private int length;

  private int head;
  private int tail;

  @SuppressWarnings("unchecked")
  public ArrayListQueue(int requestedCapacity) {
    this.capacity = ArrayListQueue.determineCapacity(requestedCapacity);
    this.length = 0;
    this.arr = (E[])(new Object[this.capacity]);

    this.head = 0;
    this.tail = 0;
  }

  public ArrayListQueue() {
    this(ArrayListQueue.MIN_CAPACITY);
  }

  public ArrayListQueue(Collection<E> c) {
    this(c, 0);
  }

  @SuppressWarnings("unchecked")
  public ArrayListQueue(Collection<E> c, int requestedCapacity) {
    this((E[])(c.toArray()), requestedCapacity);
  }

  public ArrayListQueue(E[] arr) {
    this(arr, 0);
  }

  public ArrayListQueue(E[] arr, int requestedCapacity) {
    this(Math.max(arr.length+ArrayListQueue.MIN_CAPACITY, requestedCapacity));

    System.arraycopy(arr, 0, this.arr, 0, arr.length);
    this.length = arr.length;
    this.tail = this.length;
  }

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

  @SuppressWarnings("unchecked")
  private void growCapacityTo(int newCapacity) {
    E[] newArr = (E[])(new Object[newCapacity]);

    this.straightenInto(newArr, this.head, this.tail, this.length);

    this.capacity = newCapacity;
    this.arr = newArr;
    this.head = 0;
    this.tail = this.length;
  }

  private void doubleCapacity() {
    this.growCapacityTo(this.capacity << 1);
  }

  private boolean ensureCapacity(int capacity) {
    if (capacity <= this.capacity) {
      return false;
    }
    this.growCapacityTo(ArrayListQueue.determineCapacity(capacity));
    return true;
  }

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

  @SuppressWarnings("unchecked")
  public void add(Collection<E> c) {
    this.add((E[])(c.toArray()));
  }

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

  public E remove() throws NoSuchElementException {
    E element = this.element();
    --this.length;
    this.head = (this.head+1) & (this.capacity-1);
    return element;
  }

  public void remove(int numToRemove) throws NoSuchElementException {
    for (int i = 0; i < numToRemove; ++i) {
      this.remove();
    }
  }

  public E element() throws NoSuchElementException {
    if (this.head == this.tail) {
      throw new NoSuchElementException();
    }

    return this.arr[this.head];
  }

  public E get(int index) {
    if ((index < 0) || (index >= this.length)) {
      throw new IndexOutOfBoundsException();
    }

    return this.arr[(this.head+index) & (this.capacity-1)];
  }

  public ArrayListQueue<E>.Iterator iterator() {
    return this.new Iterator();
  }

  public int size() {
    return this.length;
  }

  // i dislike type erasure
  // https://stackoverflow.com/questions/529085/how-to-create-a-generic-array-in-java
  // Internally, it is okay to create an array of E's upper
  // bound, i.e. (E[])(new Object[length]), but it is not safe
  // to pass out an array casted like this, since it will
  // compile as an E[] and can be passed to methods expecting
  // an E[], but is still actually an Object[], causing
  // ClassCastException. Thus, reflection must be used to
  // create a new array of the runtime type of the given array
  // - this is essentially a copy of the way all
  // java.util.Collection classes implement toArray

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

  public E[] toArray(E[] arr) {
    return this.createOrFillArray(arr, this.head, this.tail, this.length);
  }

  public E[] toArray(E[] arr, int offset, int length) {
    if (offset+length > this.length) {
      throw new IndexOutOfBoundsException();
    }
    int head = (this.head+offset) & (this.capacity-1);
    int tail = (head+length) & (this.capacity-1);

    return this.createOrFillArray(arr, head, tail, length);
  }

  public class Iterator implements java.util.Iterator<E> {
    private int index;

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

    public void consumeRead() {
      ArrayListQueue.this.remove(this.index);
      this.index = 0;
    }

    public int getIndex() {
      return this.index;
    }

    protected void setIndex(int index) {
      this.index = index;
    }
  }
}
