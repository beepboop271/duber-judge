package dal.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import dal.connection.ConnectDB;
import dal.connection.GlobalConnectionPool;
import entities.Batch;
import entities.Entity;
import entities.entity_fields.BatchField;

/**
 * [description]
 * <p>
 * Created on 2021.01.10.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class BatchDao implements Dao<Batch>, Updatable<BatchField> {

  @Override
  public long add(Batch batch) {
    String sql = "INSERT INTO batches(problem_id, sequence, points)"
                +" VALUES (?, ?, ?);";
    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet key = null;
    long id = -1;
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setLong(1, batch.getProblemId());
      ps.setInt(2, batch.getSequence());
      ps.setInt(3, batch.getPoints());

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
  public Entity<Batch> get(long id) throws RecordNotFoundException {
    String sql = "SELECT * FROM batches WHERE id = ?;";
    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet result = null;
    Entity<Batch> batch = null;
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setLong(1, id);

      result = ps.executeQuery();
      if (!result.next()) {
        throw new RecordNotFoundException();
      }

      batch = new Entity<Batch>(
        result.getLong("id"),
        new Batch(
          result.getLong("problem_id"),
          result.getInt("sequence"),
          result.getInt("points")
        )
      );

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectDB.close(ps);
      ConnectDB.close(result);
      GlobalConnectionPool.pool.releaseConnection(connection);
    }
    return batch;
  }

  @Override
  public ArrayList<Entity<Batch>> getList(long[] ids) {
    String sql = String.format(
      "SELECT * FROM batches WHERE id IN (%s);",
      DaoHelper.generateWildcardString(ids.length)
    );

    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet results = null;
    ArrayList<Entity<Batch>> batches = new ArrayList<>();
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      for (int i = 0; i < ids.length; i++) {
        ps.setLong(i+1, ids[i]);
      }

      results = ps.executeQuery();
      while (results.next()) {
        batches.add(new Entity<Batch>(
          results.getLong("id"),
          new Batch(
            results.getLong("problem_id"),
            results.getInt("sequence"),
            results.getInt("points")
          )
        ));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectDB.close(ps);
      ConnectDB.close(results);
      GlobalConnectionPool.pool.releaseConnection(connection);
    }
    return batches;
  }

  @Override
  public <V> void update(long id, BatchField field, V value) throws RecordNotFoundException {
    String sql = null;
    switch (field) {
      case SEQUENCE:
        sql = "UPDATE batches SET sequence = ? WHERE id = ?;";
        break;
      case POINTS:
        sql = "UPDATE batches SET points = ? WHERE id = ?;";
        break;
    }

    PreparedStatement ps = null;
    Connection connection = null;
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      switch (field) {
        case SEQUENCE:
          ps.setInt(1, (Integer)value);
          break;
        case POINTS:
          ps.setInt(1, (Integer)value);
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
  public void delete(long id) {
    DaoHelper.deleteById("batches", id);
  }

  public ArrayList<Entity<Batch>> getByProblem(long problemId) {
    String sql = "SELECT * FROM batches WHERE problem_id = ?;";

    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet results = null;
    ArrayList<Entity<Batch>> batches = new ArrayList<>();
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setLong(1, problemId);

      results = ps.executeQuery();
      while (results.next()) {
        batches.add(new Entity<Batch>(
          results.getLong("id"),
          new Batch(
            results.getLong("problem_id"),
            results.getInt("sequence"),
            results.getInt("points")
          )
        ));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectDB.close(ps);
      ConnectDB.close(results);
      GlobalConnectionPool.pool.releaseConnection(connection);
    }
    return batches;
  }

}
