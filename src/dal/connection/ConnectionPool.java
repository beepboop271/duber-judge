package dal.connection;

import java.sql.Connection;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
  private ExecutorService executor = Executors.newCachedThreadPool(); //pool that handles futures
  private CopyOnWriteArrayList<Connection> usedConnections;
  private CopyOnWriteArrayList<Connection> availableConnections;
  private ConcurrentLinkedQueue<Thread> waitingThreads;
  private int minConnections;
  private int maxConnections;
  private static final String FILE_PATH = System.getProperty("user.dir")+"/database/dubj.db";
  private ConnectDB connectDB;
  private int numConnections;
  private boolean isClosed;

  public ConnectionPool(int minConnections, int maxConnections) {
    this.minConnections = minConnections;
    this.maxConnections = maxConnections;
    this.connectDB = new ConnectDB();
    this.numConnections = 0;
    this.isClosed = false;
    for (int i = 0; i < this.minConnections; i++) {
      this.addConnection();
    }
    this.connectDB.initialize(this.availableConnections.get(0));
  }

  private void addConnection() {
    Connection connection = this.connectDB.getConnection(ConnectionPool.FILE_PATH);
    this.availableConnections.add(connection);
    this.numConnections++;
  }

  private Future<Connection> getFutureConnection() {
    return this.executor.submit(new Callable<Connection>() {
      public Connection call() {
        if (availableConnections.size() == 0 && numConnections < maxConnections) {
          addConnection();
          //enqueue the current thread if there are threads waiting
          //or if there are no available connections
          //the reason why I checked if there are waiting threads is
          //in case if a connection is released and this took the connection
          //rather than letting a queued thread to take it
        } else if (waitingThreads.size() > 0 || availableConnections.size() == 0) {
          waitingThreads.add(Thread.currentThread());
          try {
            Thread.currentThread().wait();

          } catch (Exception e) {
            e.printStackTrace();
          }
        }


        Connection connection = availableConnections.get(0);
        availableConnections.remove(connection);
        usedConnections.add(connection);
        return connection;

      }
    });
  }

  public Connection getConnection() throws IllegalStateException {
    if (this.isClosed) {
      throw new IllegalStateException("Connection pool closed");
    }
    Future<Connection> connection = this.getFutureConnection();
    try {
      return connection.get();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  public void releaseConnection(Connection connection) throws IllegalStateException {
    if (this.isClosed) {
      throw new IllegalStateException("Connection pool closed");
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
    this.executor.shutdown();
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
