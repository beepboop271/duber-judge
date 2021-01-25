package dal.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import dal.connection.ConnectDB;
import dal.connection.GlobalConnectionPool;

/**
 * A DAO helper that
 * <p>
 * Created on 2021.01.14.
 *
 * @author Shari Sun, Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class DaoHelper {

  /**
   * Gets a string representing {@code PreparedStatement} parameters({@code ?}) given
   * the number of parameters needed.
   *
   * @param count   The number of times it should be repeated.
   * @return        The constructed string.
   */
  public static String getParamString(int count) {
    if (count < 0) {
      return "";
    }

    if (count == 1) {
      return "?";
    }

    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < count - 1; i++) {
      sb.append("?,");
    }
    sb.append("?");
    return sb.toString();
  }

  /**
   * Deletes a record from a table given the ID.
   *
   * @param tableName   The table to delete the record from.
   * @param id          The record's ID.
   */
  public static void deleteById(String tableName, long id) {
    String sql = "DELETE FROM " + tableName + " WHERE id = ?;";

    PreparedStatement ps = null;
    Connection connection = null;
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setLong(1, id);

      ps.executeUpdate();

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectDB.close(ps);
      GlobalConnectionPool.pool.releaseConnection(connection);
    }
  }
}
