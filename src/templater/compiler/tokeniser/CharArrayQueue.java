package templater.compiler.tokeniser;

import java.util.NoSuchElementException;

import templater.compiler.TextFilePosition;

public class CharArrayQueue implements CharSequence {
  private static final int MIN_CAPACITY = 32;

  private char[] arr;
  private int capacity;
  private int length;

  private int head;
  private int tail;

  private final TextFilePosition position;

  public CharArrayQueue(int requestedCapacity) {
    this.capacity = CharArrayQueue.determineCapacity(requestedCapacity);
    this.length = 0;
    this.arr = new char[this.capacity];

    this.head = 0;
    this.tail = 0;

    this.position = new TextFilePosition();
  }

  public CharArrayQueue() {
    this(CharArrayQueue.MIN_CAPACITY);
  }

  public CharArrayQueue(CharSequence s) {
    this(s, 0);
  }

  public CharArrayQueue(CharSequence s, int requestedCapacity) {
    this(Math.max(s.length()+CharArrayQueue.MIN_CAPACITY, requestedCapacity));

    char[] arr = s.toString().toCharArray();
    System.arraycopy(arr, 0, this.arr, 0, arr.length);
    this.length = arr.length;
    this.tail = this.length;
  }

  private static int determineCapacity(int requestedCapacity) {
    if (requestedCapacity < 0) {
      throw new NegativeArraySizeException();
    }
    if (requestedCapacity <= CharArrayQueue.MIN_CAPACITY) {
      return CharArrayQueue.MIN_CAPACITY;
    }

    int highestBit = Integer.highestOneBit(requestedCapacity);
    if (highestBit == requestedCapacity) {
      return requestedCapacity;
    }
    return highestBit << 1;
  }

  private void growCapacityTo(int newCapacity) {
    char[] newArr = new char[newCapacity];

    if (this.head < this.tail) {
      // ...H#####T....
      //    ^copy^^
      System.arraycopy(this.arr, this.head, newArr, 0, this.length);
    } else {
      // #####T...H####
      // ^copy^   ^copy
      int trailingLength = this.capacity-this.head;
      System.arraycopy(this.arr, this.head, newArr, 0, trailingLength);
      System.arraycopy(this.arr, 0, newArr, trailingLength, this.tail);
    }

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
    this.growCapacityTo(CharArrayQueue.determineCapacity(capacity));
    return true;
  }

  public void add(char c) {
    this.arr[this.tail] = c;
    ++this.length;
    // equivalent to mod since capacity is always power of 2
    this.tail = (this.tail+1) & (this.capacity-1);
    // when tail wraps around the end and meets head, the
    // array is full
    if (this.tail == this.head) {
      this.doubleCapacity();
    }
  }

  public void add(CharSequence c) {
    this.add(c.toString().toCharArray());
  }

  public void add(char[] c) {
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

  public char remove() throws NoSuchElementException {
    char element = this.element();
    --this.length;
    this.head = (this.head+1) & (this.capacity-1);

    this.position.advanceCharacter();
    if (element == '\n') {
      this.position.advanceLine();
    }

    return element;
  }

  public char element() throws NoSuchElementException {
    if (this.head == this.tail) {
      throw new NoSuchElementException();
    }

    return this.arr[this.head];
  }

  public TextFilePosition getPosition() {
    return this.position.clone();
  }

  // methods to implement CharSequence

  @Override
  public int length() {
    return this.length;
  }

  @Override
  public char charAt(int index) {
    if ((index < 0) || (index >= this.length)) {
      throw new IndexOutOfBoundsException();
    }

    return this.arr[(this.head+index) & (this.capacity-1)];
  }

  @Override
  public String toString() {
    if (this.head < this.tail) {
      return String.valueOf(this.arr, this.head, this.length);
    } else {
      StringBuilder sb = new StringBuilder(this.length+16);
      sb.append(this.arr, this.head, this.capacity-this.head);
      sb.append(this.arr, 0, this.tail);
      return sb.toString();
    }
  }

  @Override
  public CharSequence subSequence(int start, int end) {
    int head = (this.head+start) & (this.capacity-1);
    int tail = (this.head+end) & (this.capacity-1);

    if (head < tail) {
      return String.valueOf(this.arr, head, tail-head);
    } else {
      StringBuilder sb = new StringBuilder(end-start+16);
      sb.append(this.arr, head, this.capacity-head);
      sb.append(this.arr, 0, tail);
      return sb.toString();
    }
  }
}
