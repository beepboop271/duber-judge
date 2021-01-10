package dal.connection;

/**
 * [description]
 * <p>
 * Created on 2021.01.09.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class GlobalConnectionPool {
  public static ConnectionPool pool = new ConnectionPool(2, 5);
}
