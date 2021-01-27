package dal.connection;

import java.sql.Connection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * {@code ConnectionPool} keep a fixed range of number of {@code Connection}
 * It will create a minimum number of connections and if all connections are
 * in use, it will create a new connection unless the max number of connections
 * are used. If so, it will queue the resource that needs the connection,
 * and let the thread {@code wait} until a connection is freed up.
 * <p>
 * This pool is backed by a queue so the resources that need a connection
 * serves in a FIFO pattern.
 * <p>
 * Created on 2021.01.08.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class ConnectionPool {
  /** The file path to the database. */
  private static final String FILE_PATH = System.getProperty("user.dir")+"/database/dubj.db";
  /** A list of used connections. */
  private CopyOnWriteArrayList<Connection> usedConnections;
  /** A list of available/free connections. */
  private CopyOnWriteArrayList<Connection> availableConnections;
  /** The queue of waiting threads/resources. */
  private ConcurrentLinkedQueue<Thread> waitingThreads;
  /** The min number of connection this pool should maintain at all times. */
  private int minConnections;
  /** The max number of connection this pool should maintain at all times. */
  private int maxConnections;
  /** The number of connections this pool has. */
  private int numConnections;
  /** Whether the pool is closed or not. */
  private boolean isClosed;


  /**
   * Constructs a new {@link ConnectionPool} given a
   * minimum and maximum number of connections the pool should maintain
   * at all times.
   *
   * @param minConnections       The minimum number of connections.
   * @param maxConnections       The maximum number of connections.
   */
  public ConnectionPool(int minConnections, int maxConnections) {
    this.minConnections = minConnections;
    this.maxConnections = maxConnections;
    this.numConnections = 0;
    this.usedConnections = new CopyOnWriteArrayList<>();
    this.availableConnections = new CopyOnWriteArrayList<>();
    this.waitingThreads = new ConcurrentLinkedQueue<>();
    this.isClosed = false;
    for (int i = 0; i < this.minConnections; i++) {
      this.addConnection();
    }
    ConnectDB.initialize(this.availableConnections.get(0));
  }

  /**
   * Adds a new connection to {@link ConnectionPool#availableConnections}.
   */
  private void addConnection() {
    Connection connection = ConnectDB.getConnection(ConnectionPool.FILE_PATH);
    this.availableConnections.add(connection);
    this.numConnections++;
  }


  /**
   * This returns an available connection and if none are found,
   * this is a blocking method that waits until a free connection
   * is released.
   * <p>
   * It enqueue the current thread if there are threads waiting
   * or if there are no available connections.
   * The reason why the number of waiting threads is checked is because
   * in case a connection is released and this took the connection
   * rather than letting a queued thread (that waited longer) take it.
   *
   * @return                             A database connection.
   * @throws IllegalStateException       If the database is closed.
   */
  public Connection getConnection() throws IllegalStateException {
    if (this.isClosed) {
      throw new IllegalStateException("Connection pool closed");
    }
    if (this.availableConnections.size() == 0
      && this.numConnections < this.maxConnections) {
      addConnection();

    } else if (this.waitingThreads.size() > 0 || this.availableConnections.size() == 0) {
      this.waitingThreads.add(Thread.currentThread());
      try {
        Thread.currentThread().wait();

      } catch (Exception e) {
        e.printStackTrace();
      }
    }


    Connection connection = this.availableConnections.get(0);
    this.availableConnections.remove(connection);
    this.usedConnections.add(connection);
    return connection;
  }

  /**
   * Releases a connection that becomes available for
   * a waiting thread or future thread to use.
   *
   * @param connection                  The connection to release.
   * @throws IllegalStateException      If the connection pool is already closed.
   */
  public void releaseConnection(Connection connection) throws IllegalStateException {
    if (this.isClosed) {
      throw new IllegalStateException("Connection pool closed");
    }
    if (connection == null) {
      return;
    }
    this.availableConnections.add(connection);
    this.usedConnections.remove(connection);
    if (this.waitingThreads.size() > 0) {
      this.waitingThreads.poll().notify();
    }
  }


  /**
   * Upon the closure of the database, it releases all connections.
   *
   * @exception IllegalStateException   If there are threads still awaiting for a connection.
   */
  public void close() throws IllegalStateException {
    if (!this.waitingThreads.isEmpty()) {
      throw new IllegalStateException("Waiting Threads not empty");
    }
    this.isClosed = true;
    for (Connection connection : this.availableConnections) {
      try {
        connection.close();
      } catch (Exception e) {
        System.out.println("Error closing connection in connection pool");
        e.printStackTrace();
      }
    }
    for (Connection connection : this.usedConnections) {
      try {
        connection.close();
      } catch (Exception e) {
        System.out.println("Error closing non-released connection in connection pool");
        e.printStackTrace();
      }
    }
  }

}
