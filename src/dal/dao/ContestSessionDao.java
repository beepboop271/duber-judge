package dal.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import dal.connection.ConnectDB;
import dal.connection.GlobalConnectionPool;
import entities.ContestSession;
import entities.ContestStatus;
import entities.Entity;
import entities.entity_fields.ContestSessionField;

/**
 * [description]
 * <p>
 * Created on 2021.01.10.
 *
 * @author Shari Sun, Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class ContestSessionDao implements Dao<ContestSession>, Updatable<ContestSessionField> {

  @Override
  public <V> void update(long id, ContestSessionField field, V value)
    throws RecordNotFoundException {
    String element = "";
    switch (field) {
      case STATUS:
        element = "status";
        break;
      case SCORE:
        element = "score";
        break;
    }
    String sql = "UPDATE contest_sessions SET " + element + " = ? WHERE id = ?;";
    PreparedStatement ps = null;
    Connection connection = null;
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      switch (field) {
        case STATUS:
          ps.setString(1, ((ContestStatus)value).name());
          break;
        case SCORE:
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
  public long add(ContestSession data) {
    String sql = "INSERT INTO contest_sessions"
                +"(contest_id, user_id, started_at, status, score)"
                +" VALUES (" + DaoHelper.generateWildcardString(5) + ");";
    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet key = null;
    long id = -1;
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setLong(1, data.getContestId());
      ps.setLong(2, data.getUserId());
      ps.setString(3, data.getStartedAt().toString());
      ps.setString(4, data.getStatus().name());
      ps.setInt(5, data.getScore());

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
  public Entity<ContestSession> get(long id) throws RecordNotFoundException {
    String sql = "SELECT * FROM contest_sessions WHERE id = ?;";
    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet result = null;
    Entity<ContestSession> contestSession = null;
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setLong(1, id);

      result = ps.executeQuery();
      if (!result.next()) {
        throw new RecordNotFoundException();
      }


      contestSession = new Entity<ContestSession>(
        result.getLong("id"),
        new ContestSession(
          result.getLong("contest_id"),
          result.getLong("user_id"),
          Timestamp.valueOf(result.getString("started_at")),
          ContestStatus.valueOf(result.getString("status")),
          result.getInt("score")
        )
      );

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectDB.close(ps);
      ConnectDB.close(result);
      GlobalConnectionPool.pool.releaseConnection(connection);
    }
    return contestSession;
  }

  @Override
  public ArrayList<Entity<ContestSession>> getList(long[] ids) {
    String sql = String.format(
      "SELECT * FROM contest_sessions WHERE id IN (%s);",
      DaoHelper.generateWildcardString(ids.length)
    );

    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet results = null;
    ArrayList<Entity<ContestSession>> sessions = new ArrayList<>();
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      for (int i = 0; i < ids.length; i++) {
        ps.setLong(i+1, ids[i]);
      }

      results = ps.executeQuery();
      while (results.next()) {
        sessions.add(new Entity<ContestSession>(
          results.getLong("id"),
          new ContestSession(
          results.getLong("contest_id"),
          results.getLong("user_id"),
          Timestamp.valueOf(results.getString("started_at")),
          ContestStatus.valueOf(results.getString("status")),
          results.getInt("score")
          ))
        );
      }


    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectDB.close(ps);
      ConnectDB.close(results);
      GlobalConnectionPool.pool.releaseConnection(connection);
    }
    return sessions;
  }

  @Override
  public void delete(long id) {
    DaoHelper.deleteById("contest_sessions", id);
  }

}
