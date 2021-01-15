package dal.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import dal.connection.ConnectDB;
import dal.connection.GlobalConnectionPool;
import entities.Clarification;
import entities.Entity;
import entities.entity_fields.ClarificationField;

/**
 * [description]
 * <p>
 * Created on 2021.01.10.
 *
 * @author Shari Sun, Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class ClarificationDao implements Dao<Clarification>, Updatable<ClarificationField> {

  @Override
  public <V> void update(long id, ClarificationField field, V value)
    throws RecordNotFoundException {
    String sql = null;
    switch (field) {
      case MESSAGE:
        sql = "UPDATE clarifications SET message = ? WHERE id = ?;";
        break;
      case RESPONSE:
        sql = "UPDATE clarifications SET response = ? WHERE id = ?;";
        break;
    }

    PreparedStatement ps = null;
    Connection connection = null;
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      switch (field) {
        case MESSAGE:
          ps.setString(1, (String)value);
          break;
        case RESPONSE:
          ps.setString(1, (String)value);
          break;
      }
      ps.setLong(2, id);
      ps.executeUpdate();

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectDB.close(ps);
      GlobalConnectionPool.pool.releaseConnection(connection);
    }
  }

  @Override
  public long add(Clarification data) {
    String sql = "INSERT INTO clarifications(problem_id, user_id, message, response)"
                +" VALUES (?, ?, ?, ?);";
    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet key = null;
    long id = -1;
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setLong(1, data.getProblemId());
      ps.setLong(2, data.getUserId());
      ps.setString(3, data.getMessage());
      ps.setString(4, data.getResponse());

      ps.executeUpdate();
      key = ps.getGeneratedKeys();
      key.next();
      id = key.getLong(1);
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      GlobalConnectionPool.pool.releaseConnection(connection);
    }
    return id;
  }

  @Override
  public Entity<Clarification> get(long id) throws RecordNotFoundException {
    String sql = "SELECT * FROM batches WHERE id = ?;";
    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet result = null;
    Entity<Clarification> clarification = null;
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setLong(1, id);

      result = ps.executeQuery();
      if (!result.next()) {
        throw new RecordNotFoundException();
      }

      clarification = this.getClarificationFromResultSet(result);

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectDB.close(ps);
      ConnectDB.close(result);
      GlobalConnectionPool.pool.releaseConnection(connection);
    }
    return clarification;
  }

  @Override
  public ArrayList<Entity<Clarification>> getList(long[] ids) {
    String sql = String.format(
      "SELECT * FROM clarifications WHERE id IN (%s);",
      DaoHelper.generateWildcardString(ids.length)
    );

    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet results = null;
    ArrayList<Entity<Clarification>> clarifications = new ArrayList<>();
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      for (int i = 0; i < ids.length; i++) {
        ps.setLong(i+1, ids[i]);
      }

      results = ps.executeQuery();
      while (results.next()) {
        clarifications.add(this.getClarificationFromResultSet(results));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectDB.close(ps);
      ConnectDB.close(results);
      GlobalConnectionPool.pool.releaseConnection(connection);
    }
    return clarifications;
  }

  @Override
  public void deleteById(long id) {
    DaoHelper.deleteById("clarifications", id);
  }

  private Entity<Clarification> getClarificationFromResultSet(ResultSet result)
    throws SQLException {
    return new Entity<Clarification>(
      result.getLong("id"),
      new Clarification(
        result.getLong("problem_id"),
        result.getLong("user_id"),
        result.getString("message"),
        result.getString("response")
      )
    );
  }
}
