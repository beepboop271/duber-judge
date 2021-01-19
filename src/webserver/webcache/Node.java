package webserver.webcache;

/**
 * A simple node class designed to work alongside a doubly
 * linked list implementation.
 * <p>
 * This node class stores references to both the previous
 * and next node. If no node is provided, the values will be
 * {@code null}.
 * <p>
 * Created <b> 2021-01-08</b>.
 *
 * @since 0.0.1
 * @version 0.0.1
 * @author Joseph Wang
 * @param <T> the type of the object stored.
 **/
class Node<T> {
  /** The data stored in this node. */
  public T data;
  /** A pointer to the previous node. */
  public Node<T> prev;
  /** A pointer to the next node. */
  public Node<T> next;

  /**
   * Constructs a new Node.
   * <p>
   * This constructor will create a Node with data but no
   * previous or next node. Those will be initialized as
   * {@code null}.
   *
   * @param data The data to save in this node.
   */
  public Node(T data) {
    this(data, null, null);
  }

  /**
   * Constructs a new Node.
   * <p>
   * This constructor will create a Node with data and a
   * previous node but no next node. That will be initialized
   * as {@code null}.
   *
   * @param data The data to save in this node.
   * @param prev A pointer to the previous node.
   */
  public Node(T data, Node<T> prev) {
    this(data, prev, null);
  }

  /**
   * Constructs a new Node.
   * <p>
   * This constructor will create a Node with data and both a
   * previous and next node.
   * <p>
   * If the next node or both nodes do not exist, consider
   * using {@link #Node(Object, Node)} or
   * {@link #Node(Object)} respectively, instead.
   *
   * @param data The data to save in this node.
   * @param prev A pointer to the previous node.
   * @param next A pointer to the next node.
   */
  public Node(T data, Node<T> prev, Node<T> next) {
    this.data = data;
    this.prev = prev;
    this.next = next;
  }
}
