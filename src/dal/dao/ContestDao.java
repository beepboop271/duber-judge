package dal.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import dal.connection.ConnectDB;
import dal.connection.GlobalConnectionPool;
import entities.Contest;
import entities.Entity;
import entities.entity_fields.ContestField;

/**
 * [description]
 * <p>
 * Created on 2021.01.10.
 *
 * @author Shari Sun, Candice
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
  public long add(Contest data) {
    String sql = "INSERT INTO contests"
                +"(creator_id, description, title, start_time, end_time, duration)"
                +" VALUES (" + DaoHelper.generateWildcardString(6) + ");";
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
      ps.setInt(6, data.getDurationMinutes());

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

      contest = new Entity<Contest>(
        result.getLong("id"),
        new Contest(
          result.getLong("creator_Id"),
          result.getString("description"),
          result.getString("title"),
          Timestamp.valueOf(result.getString("start_time")),
          Timestamp.valueOf(result.getString("end_time")),
          result.getInt("duration")
        )
      );

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
      DaoHelper.generateWildcardString(ids.length)
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
        contests.add(new Entity<Contest>(
          results.getLong("id"),
          new Contest(
            results.getLong("creator_Id"),
            results.getString("description"),
            results.getString("title"),
            Timestamp.valueOf(results.getString("start_time")),
            Timestamp.valueOf(results.getString("end_time")),
            results.getInt("duration")
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
    return contests;
  }

  @Override
  public void delete(long id) {
    DaoHelper.deleteById("contests", id);
  }

}
