package dal.connection;

/**
 * A static class used for global access of the database
 * connection pool.
 * <p>
 * Created on 2021.01.09.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class GlobalConnectionPool {
  /** The connection pool. */
  public static ConnectionPool pool = new ConnectionPool(2, 5);
}
