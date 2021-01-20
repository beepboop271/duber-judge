package dal.connection;

import java.sql.Connection;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * [description]
 * <p>
 * Created on 2021.01.08.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class ConnectionPool {
  private CopyOnWriteArrayList<Connection> usedConnections;
  private CopyOnWriteArrayList<Connection> availableConnections;
  private ConcurrentLinkedQueue<Thread> waitingThreads;
  private int minConnections;
  private int maxConnections;
  private static final String FILE_PATH = System.getProperty("user.dir")+"/database/dubj.db";
  private int numConnections;
  private boolean isClosed;

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

  private void addConnection() {
    Connection connection = ConnectDB.getConnection(ConnectionPool.FILE_PATH);
    this.availableConnections.add(connection);
    this.numConnections++;
  }

  public Connection getConnection() throws IllegalStateException {
    if (this.isClosed) {
      throw new IllegalStateException("Connection pool closed");
    }
    if (this.availableConnections.size() == 0
      && this.numConnections < this.maxConnections) {
      addConnection();
      //enqueue the current thread if there are threads waiting
      //or if there are no available connections
      //the reason why I checked if there are waiting threads is
      //in case if a connection is released and this took the connection
      //rather than letting a queued thread to take it
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
   * Closes all connections.
   */
  public void close() {
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
