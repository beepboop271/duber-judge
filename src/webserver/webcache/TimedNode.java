package webserver.webcache;

/**
 * A simple node class designed to work alongside a cache
 * doubly linked list implementation.
 * <p>
 * This node class stores references to both the previous
 * and next node, or {@code null} if not provided a node.
 * <p>
 * This node stores an expiration time for caches,
 * representing how long this node should live for.
 * <p>
 * Created <b> 2021-01-19</b>.
 *
 * @since 0.0.3
 * @version1.0.0
 * @author Joseph Wang
 * @param <T> the type of the object stored.
 **/
class TimedNode<T> {
  public static int DEFAULT_LIVE_SECS = 60;
  /** The data stored in this node. */
  private T data;
  /** A pointer to the previous node. */
  public TimedNode<T> prev;
  /** A pointer to the next node. */
  public TimedNode<T> next;
  /** The time when this object expires. */
  private long expirationTime;

  /**
   * Constructs a new TimedNode.
   * <p>
   * This constructor will create a Node with data but no
   * previous or next node. Those will be initialized as
   * {@code null}.
   * <p>
   * This constructor will also set the time to live to the
   * default time, or {@link #DEFAULT_LIVE_SECS}.
   *
   * @param data The data to save in this node.
   */
  public TimedNode(T data) {
    this(data, null, null, TimedNode.DEFAULT_LIVE_SECS);
  }

  /**
   * Constructs a new TimedNode.
   * <p>
   * This constructor will create a Node with data and the
   * amount of seconds to live but no previous or next node.
   * Those will be initialized as {@code null}.
   *
   * @param data          The data to save in this node.
   * @param secondsToLive The amount of seconds for this node
   *                      to live.
   * @throws IllegalArgumentException if the seconds to live
   *                                  is less than 0.
   */
  public TimedNode(T data, int secondsToLive) {
    this(data, null, null, secondsToLive);
  }

  /**
   * Constructs a new TimedNode.
   * <p>
   * This constructor will create a Node with data, a previous
   * node, and the amount of seconds to live but no next node.
   * That will be initialized as {@code null}.
   *
   * @param data          The data to save in this node.
   * @param prev          A pointer to the previous node.
   * @param secondsToLive The amount of seconds for this node
   *                      to live.
   * @throws IllegalArgumentException if the seconds to live
   *                                  is less than 0.
   */
  public TimedNode(T data, TimedNode<T> prev, int secondsToLive) {
    this(data, prev, null, secondsToLive);
  }

  /**
   * Constructs a new Node.
   * <p>
   * This constructor will create a Node with data and both a
   * previous and next node, as well as the amount of seconds
   * to live.
   * <p>
   * If the next node or both nodes do not exist, consider
   * using {@link #Node(Object, Node)} or
   * {@link #Node(Object)} respectively, instead.
   *
   * @param data          The data to save in this node.
   * @param prev          A pointer to the previous node.
   * @param next          A pointer to the next node.
   * @param secondsToLive The amount of seconds for this node
   *                      to live.
   * @throws IllegalArgumentException if the seconds to live
   *                                  is less than 0.
   */
  public TimedNode(
    T data,
    TimedNode<T> prev,
    TimedNode<T> next,
    int secondsToLive
  ) {
    if (secondsToLive < 0) {
      throw new IllegalArgumentException("Seconds cannot be negative.s");
    }
    this.data = data;
    this.prev = prev;
    this.next = next;
    this.expirationTime = System.currentTimeMillis()+(secondsToLive*1000);
  }

  /**
   * Retrieves the stored data in this node.
   *
   * @return the stored data in this node.
   */
  public T getData() {
    return this.data;
  }

  /**
   * Sets the stored data to a specified object.
   *
   * @param data the new data to be stored.
   */
  public void setData(T data) {
    this.data = data;
  }

  /**
   * Checks if this node is already expired.
   *
   * @return true if this node is expired.
   */
  public boolean isExpired() {
    return this.getRemainingTime() <= 0;
  }

  /**
   * Gets this node's remaining time to live.
   * <p>
   * If this node's is already expired, the time will be
   * returned as a negative long.
   *
   * @return this node's remaining time to live.
   */
  public long getRemainingTime() {
    return this.expirationTime-System.currentTimeMillis();
  }

  @Override
  public int hashCode() {
    return data.hashCode();
  }
}
