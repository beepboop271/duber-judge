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
import entities.ContestSessionStatus;
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
          ps.setString(1, ((ContestSessionStatus)value).name());
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

  //TODO: figure out a way to clean expired contest sessions
  public <V> void updateByUser(long userId, ContestSessionField field, V value)
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
    String sql = "UPDATE contest_sessions SET " + element + " = ? WHERE user_id = ?;";
    PreparedStatement ps = null;
    Connection connection = null;
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      switch (field) {
        case STATUS:
          ps.setString(1, ((ContestSessionStatus)value).name());
          break;
        case SCORE:
          ps.setInt(1, (Integer)value);
          break;
      }

      ps.setLong(2, userId);
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
                +" VALUES (" + DaoHelper.getParamString(5) + ");";
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


      contestSession = this.getContestSessionFromResultSet(result);

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectDB.close(ps);
      ConnectDB.close(result);
      GlobalConnectionPool.pool.releaseConnection(connection);
    }
    return contestSession;
  }

  public Entity<ContestSession> get(long contestId, long userId)
    throws RecordNotFoundException {
    String sql = "SELECT * FROM contest_sessions WHERE contest_id = ?, user_id = ?;";
    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet result = null;
    Entity<ContestSession> contestSession = null;
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setLong(1, contestId);
      ps.setLong(2, userId);

      result = ps.executeQuery();
      if (!result.next()) {
        throw new RecordNotFoundException();
      }
      contestSession = this.getContestSessionFromResultSet(result);

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
      DaoHelper.getParamString(ids.length)
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
        sessions.add(this.getContestSessionFromResultSet(results));
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
  public void deleteById(long id) {
    DaoHelper.deleteById("contest_sessions", id);
  }

  private Entity<ContestSession> getContestSessionFromResultSet(ResultSet result)
    throws SQLException {
    return new Entity<ContestSession>(
      result.getLong("id"),
      new ContestSession(
        result.getLong("contest_id"),
        result.getLong("user_id"),
        Timestamp.valueOf(result.getString("started_at")),
        ContestSessionStatus.valueOf(result.getString("status")),
        result.getInt("score")
      )
    );
  }

  public int getNumSessions(long contestId) {
    String sql = "SELECT COUNT(*) FROM contest_sessions WHERE contest_id = ?;";
    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet result = null;
    int count = 0;
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setLong(1, contestId);

      result = ps.executeQuery();
      if (result.next()) {
        count = result.getInt(1);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectDB.close(ps);
      ConnectDB.close(result);
      GlobalConnectionPool.pool.releaseConnection(connection);
    }
    return count;
  }

  public int getNumContests(long userId) {
    String sql = "SELECT COUNT(*) FROM contest_sessions WHERE user_id = ?;";
    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet result = null;
    int count = 0;
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setLong(1, userId);

      result = ps.executeQuery();
      if (result.next()) {
        count = result.getInt(1);
      }

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectDB.close(ps);
      ConnectDB.close(result);
      GlobalConnectionPool.pool.releaseConnection(connection);
    }
    return count;
  }

  /**
   * Get all sessions of a contest ordered from latest to earliest.
   * The index indicates the offset of the record in the database.
   * If no results are found, it will return an empty array.
   *
   * @param contestId           the contest id
   * @param index               the offset of the session in the query result
   * @param numSessions         the number of session to fetch
   * @return                    the list of contest sessions
   */
  public ArrayList<Entity<ContestSession>>
    getByContest(long contestId, int index, int numSessions) {
    String sql = String.format(
                "SELECT * FROM contest_sessions"
                +"WHERE contest_id = ?"
                +"ORDER BY started_at DESC"
                +"LIMIT %s OFFSET %s", numSessions, index);
    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet result = null;
    ArrayList<Entity<ContestSession>> sessions = new ArrayList<>();
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setLong(1, contestId);

      result = ps.executeQuery();
      while (result.next()) {
        sessions.add(this.getContestSessionFromResultSet(result));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectDB.close(ps);
      ConnectDB.close(result);
      GlobalConnectionPool.pool.releaseConnection(connection);
    }
    return sessions;
  }

  /**
   * Get the contest sessions based on the user
   * (get all contests the user has participated in) ordered from latest to earliest.
   * If no results are found, it will return an empty list.
   *
   * @param userId          the user id
   * @param index           the offset of the sessions
   * @param numSessions     the number of sessions to retrieve
   * @return
   */
  public ArrayList<Entity<ContestSession>>
    getByUser(long userId, int index, int numSessions) {
    String sql = String.format(
                "SELECT * FROM contest_sessions"
                +"WHERE user_id = ?"
                +"ORDER BY started_at DESC"
                +"LIMIT %s OFFSET %s", numSessions, index);
    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet result = null;
    ArrayList<Entity<ContestSession>> sessions = new ArrayList<>();
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setLong(1, userId);

      result = ps.executeQuery();
      while (result.next()) {
        sessions.add(this.getContestSessionFromResultSet(result));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectDB.close(ps);
      ConnectDB.close(result);
      GlobalConnectionPool.pool.releaseConnection(connection);
    }
    return sessions;
  }



}
