package dal.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import org.sqlite.SQLiteErrorCode;

import dal.connection.ConnectDB;
import dal.connection.GlobalConnectionPool;
import entities.Contest;
import entities.ContestStatus;
import entities.Entity;
import entities.PublishingState;
import entities.entity_fields.ContestField;
import services.InvalidArguments;

/**
 * {@code DAO} for {@link Contest}.
 * <p>
 * Created on 2021.01.10.
 *
 * @author Shari Sun, Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class ContestDao implements Dao<Contest>, Updatable<ContestField> {

  @Override
  public <V> void update(long id, ContestField field, V value)
    throws RecordNotFoundException {
    String element = "";
    switch (field) {
      case TITLE:
        element = "title";
        break;
      case DESCRIPTION:
        element = "description";
        break;
      case START_TIME:
        element = "start_time";
        break;
      case END_TIME:
        element = "end_time";
        break;
      case DURATION_MINUTES:
        element = "duration_minutes";
        break;
      case STATUS:
        element = "status";
        break;
      case PUBLISHING_STATE:
        element = "publishing_state";
    }
    String sql = "UPDATE contests SET " + element + " = ? WHERE id = ?;";
    PreparedStatement ps = null;
    Connection connection = null;
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      switch (field) {
        case TITLE:
          ps.setString(1, (String)value);
          break;
        case DESCRIPTION:
          ps.setString(1, (String)value);
          break;
        case START_TIME:
          ps.setString(1, ((Timestamp)value).toString());
          break;
        case END_TIME:
          ps.setString(1, ((Timestamp)value).toString());
          break;
        case DURATION_MINUTES:
          ps.setInt(1, (int)value);
          break;
        case STATUS:
          ps.setString(1, ((ContestStatus)value).toString());
          break;
        case PUBLISHING_STATE:
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
  public long add(Contest data) throws IllegalArgumentException {
    String sql = "INSERT INTO contests"
                +"(creator_id, description, title, start_time,"
                +" end_time, status, duration_minutes, publishing_state)"
                +" VALUES (" + DaoHelper.getParamString(8) + ");";
    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet key = null;
    long id = -1;
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setLong(1, data.getCreatorId());
      ps.setString(2, data.getDescription());
      ps.setString(3, data.getTitle());
      ps.setString(4, data.getStartTime().toString());
      ps.setString(5, data.getEndTime().toString());
      ps.setString(6, data.getStatus().toString());
      ps.setInt(7, data.getDurationMinutes());
      ps.setString(8, data.getPublishingState().toString());

      ps.executeUpdate();
      key = ps.getGeneratedKeys();
      key.next();
      id = key.getLong(1);
      ps.close();


    } catch (SQLException e) {
      if (SQLiteErrorCode.getErrorCode(e.getErrorCode())
          == SQLiteErrorCode.SQLITE_CONSTRAINT) {
        throw new IllegalArgumentException(InvalidArguments.TITLE_TAKEN.toString());
      } else {
        e.printStackTrace();
      }
    } finally {
      ConnectDB.close(ps);
      ConnectDB.close(key);
      GlobalConnectionPool.pool.releaseConnection(connection);
    }
    return id;
  }

  @Override
  public Entity<Contest> get(long id) throws RecordNotFoundException {
    String sql = "SELECT * FROM contests WHERE id = ?;";
    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet result = null;
    Entity<Contest> contest = null;
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setLong(1, id);

      result = ps.executeQuery();
      if (!result.next()) {
        throw new RecordNotFoundException();
      }

      contest = this.getContestFromResultSet(result);

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectDB.close(ps);
      ConnectDB.close(result);
      GlobalConnectionPool.pool.releaseConnection(connection);
    }
    return contest;
  }

  @Override
  public ArrayList<Entity<Contest>> getList(long[] ids) {
    String sql = String.format(
      "SELECT * FROM contests WHERE id IN (%s);",
      DaoHelper.getParamString(ids.length)
    );

    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet results = null;
    ArrayList<Entity<Contest>> contests = new ArrayList<>();
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      for (int i = 0; i < ids.length; i++) {
        ps.setLong(i+1, ids[i]);
      }

      results = ps.executeQuery();
      while (results.next()) {
        contests.add(this.getContestFromResultSet(results));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectDB.close(ps);
      ConnectDB.close(results);
      GlobalConnectionPool.pool.releaseConnection(connection);
    }
    return contests;
  }

  @Override
  public void deleteById(long id) {
    DaoHelper.deleteById("contests", id);
  }

  public ArrayList<Entity<Contest>> getContests(int index, int numContests, ContestStatus status) {
    String sql = String.format(
      "SELECT * FROM contests\n"
      +"WHERE status = ? AND publishing_state = '%s'\n"
      +"ORDER BY start_time ASC\n"
      +"LIMIT %s OFFSET %s",
      PublishingState.PUBLISHED, numContests, index
    );

    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet results = null;
    ArrayList<Entity<Contest>> contests = new ArrayList<>();
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setString(1, status.toString());

      results = ps.executeQuery();
      while (results.next()) {
        contests.add(this.getContestFromResultSet(results));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectDB.close(ps);
      ConnectDB.close(results);
      GlobalConnectionPool.pool.releaseConnection(connection);
    }
    return contests;
  }

  private Entity<Contest> getContestFromResultSet(ResultSet result) throws SQLException {
    return new Entity<Contest>(
      result.getLong("id"),
      new Contest(
        result.getLong("creator_Id"),
        result.getString("description"),
        result.getString("title"),
        Timestamp.valueOf(result.getString("start_time")),
        Timestamp.valueOf(result.getString("end_time")),
        ContestStatus.valueOf(result.getString("status")),
        result.getInt("duration_minutes"),
        PublishingState.valueOf(result.getString("publishing_state"))
      )
    );
  }

  public void updateStatus() {
    String sql = String.format(
      "UPDATE contests\n"
      +"SET status = '%s'\n"
      +"WHERE ('%s' >= start_time);",
      ContestStatus.ONGOING,
      new Timestamp(System.currentTimeMillis()).toString()
    );

    String sql2 = String.format(
      "UPDATE contests\n"
      +"SET status = '%s'\n"
      +"WHERE ('%s' >= end_time);",
      ContestStatus.PAST,
      new Timestamp(System.currentTimeMillis()).toString()
    );

    PreparedStatement ps = null;
    PreparedStatement ps2 = null;
    Connection connection = null;
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps2 = connection.prepareStatement(sql2);

      ps.executeUpdate();
      ps2.executeUpdate();

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectDB.close(ps);
      ConnectDB.close(ps2);
      GlobalConnectionPool.pool.releaseConnection(connection);
    }
  }


  public ArrayList<Entity<Contest>> getCreatedContests(long userId) {
    String sql = "SELECT * FROM contests WHERE creator_id = ?;";

    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet results = null;
    ArrayList<Entity<Contest>> problems = new ArrayList<>();
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setLong(1, userId);

      results = ps.executeQuery();
      while (results.next()) {
        problems.add(this.getContestFromResultSet(results));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectDB.close(ps);
      ConnectDB.close(results);
      GlobalConnectionPool.pool.releaseConnection(connection);
    }
    return problems;
  }
}
