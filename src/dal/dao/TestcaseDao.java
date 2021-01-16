package dal.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import dal.connection.ConnectDB;
import dal.connection.GlobalConnectionPool;
import entities.Entity;
import entities.Testcase;
import entities.entity_fields.TestcaseField;

/**
 * [description]
 * <p>
 * Created on 2021.01.10.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class TestcaseDao implements Dao<Testcase>, Updatable<TestcaseField> {



  @Override
  public long add(Testcase testcase) {
    String sql = "INSERT INTO testcases(batch_id, sequence, input, output)"
                +" VALUES (?, ?, ?, ?);";
    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet key = null;
    long id = -1;
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setLong(1, testcase.getBatchId());
      ps.setInt(2, testcase.getSequence());
      ps.setString(3, testcase.getInput());
      ps.setString(4, testcase.getOutput());

      ps.executeUpdate();
      key = ps.getGeneratedKeys();
      key.next();
      id = key.getLong(1);
      ps.close();


    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectDB.close(ps);
      ConnectDB.close(key);
      GlobalConnectionPool.pool.releaseConnection(connection);
    }
    return id;
  }

  @Override
  public Entity<Testcase> get(long id) throws RecordNotFoundException {
    String sql = "SELECT * FROM testcases WHERE id = ?;";
    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet result = null;
    Entity<Testcase> testcase = null;
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setLong(1, id);

      result = ps.executeQuery();
      if (!result.next()) {
        throw new RecordNotFoundException();
      }

      testcase = this.getTestcaseFromResultSet(result);

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectDB.close(ps);
      ConnectDB.close(result);
      GlobalConnectionPool.pool.releaseConnection(connection);
    }
    return testcase;
  }

  @Override
  public ArrayList<Entity<Testcase>> getList(long[] ids) {
    String sql = String.format(
      "SELECT * FROM testcases WHERE id IN (%s);",
      DaoHelper.getParamString(ids.length)
    );

    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet results = null;
    ArrayList<Entity<Testcase>> testcases = new ArrayList<>();
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      for (int i = 0; i < ids.length; i++) {
        ps.setLong(i+1, ids[i]);
      }

      results = ps.executeQuery();
      while (results.next()) {
        testcases.add(this.getTestcaseFromResultSet(results));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectDB.close(ps);
      ConnectDB.close(results);
      GlobalConnectionPool.pool.releaseConnection(connection);
    }
    return testcases;
  }

  @Override
  public <V> void update(long id, TestcaseField field, V value) throws RecordNotFoundException {
    String sql = null;
    switch (field) {
      case BATCH_ID:
        sql = "UPDATE testcases SET batch_id = ? WHERE id = ?;";
        break;
      case SEQUENCE:
        sql = "UPDATE testcases SET sequence = ? WHERE id = ?;";
        break;
      case INPUT:
        sql = "UPDATE testcases SET input = ? WHERE id = ?;";
        break;
      case OUTPUT:
        sql = "UPDATE testcases SET output = ? WHERE id = ?;";
        break;
    }

    PreparedStatement ps = null;
    Connection connection = null;
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      switch (field) {
        case BATCH_ID:
          ps.setLong(1, (Long)value);
          break;
        case SEQUENCE:
          ps.setInt(1, (Integer)value);
          break;
        case INPUT:
          ps.setString(1, (String)value);
          break;
        case OUTPUT:
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
  public void deleteById(long id) {
    DaoHelper.deleteById("testcases", id);
  }

  public ArrayList<Entity<Testcase>> getByBatch(long batchId) {
    String sql = "SELECT * FROM testcases WHERE batch_id = ?;";

    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet results = null;
    ArrayList<Entity<Testcase>> testcases = new ArrayList<>();
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setLong(1, batchId);

      results = ps.executeQuery();
      while (results.next()) {
        testcases.add(this.getTestcaseFromResultSet(results));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectDB.close(ps);
      ConnectDB.close(results);
      GlobalConnectionPool.pool.releaseConnection(connection);
    }
    return testcases;

  }

  public void deleteByBatch(long batchId) {
    String sql = "DELETE FROM testcases WHERE batch_id = ?;";

    PreparedStatement ps = null;
    Connection connection = null;
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setLong(1, batchId);

      ps.executeUpdate();

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectDB.close(ps);
      GlobalConnectionPool.pool.releaseConnection(connection);
    }
  }

  private Entity<Testcase> getTestcaseFromResultSet(ResultSet result) throws SQLException {
    return new Entity<Testcase>(
      result.getLong("id"),
      new Testcase(
        result.getLong("batch_id"),
        result.getInt("sequence"),
        result.getString("input"),
        result.getString("output")
      )
    );
  }

}
