package dal.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.sqlite.SQLiteErrorCode;

import dal.connection.ConnectDB;
import dal.connection.GlobalConnectionPool;
import entities.Entity;
import entities.User;
import entities.UserType;
import entities.entity_fields.UserField;
import services.InvalidArguments;

/**
 * [description]
 * <p>
 * Created on 2021.01.10.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class UserDao implements Dao<User>, Updatable<UserField> {

  @Override
  public long add(User user) throws IllegalArgumentException {
    String sql = "INSERT INTO users(username, password, user_type, salt)"
                +" VALUES (?, ?, ?, ?);";
    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet key = null;
    long id = -1;
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setString(1, user.getUsername());
      ps.setString(2, user.getPassword());
      ps.setString(3, user.getUserType().toString());
      ps.setString(4, user.getSalt());

      ps.executeUpdate();
      key = ps.getGeneratedKeys();
      key.next();
      id = key.getLong(1);
    } catch (SQLException e) {
      if (SQLiteErrorCode.getErrorCode(e.getErrorCode())
          == SQLiteErrorCode.SQLITE_CONSTRAINT) {
        throw new IllegalArgumentException(InvalidArguments.USERNAME_TAKEN.toString());
      }
    } finally {
      GlobalConnectionPool.pool.releaseConnection(connection);
    }
    return id;
  }

  @Override
  public Entity<User> get(long id) throws RecordNotFoundException {

    String sql = "SELECT * FROM users WHERE id = ?;";
    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet result = null;
    Entity<User> user = null;
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setLong(1, id);

      result = ps.executeQuery();
      if (!result.next()) {
        throw new RecordNotFoundException();
      }

      user = this.getUserFromResultSet(result);

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectDB.close(ps);
      ConnectDB.close(result);
      GlobalConnectionPool.pool.releaseConnection(connection);
    }
    return user;
  }

  @Override
  public ArrayList<Entity<User>> getList(long[] ids) {
    String sql = String.format(
      "SELECT * FROM users WHERE id IN (%s);",
      DaoHelper.getParamString(ids.length)
    );

    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet results = null;
    ArrayList<Entity<User>> users = new ArrayList<>();
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      for (int i = 0; i < ids.length; i++) {
        ps.setLong(i+1, ids[i]);
      }

      results = ps.executeQuery();
      while (results.next()) {
        users.add(this.getUserFromResultSet(results));
      }

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectDB.close(ps);
      ConnectDB.close(results);
      GlobalConnectionPool.pool.releaseConnection(connection);
    }
    return users;
  }

  @Override
  public <T> void update(long id, UserField field, T value) throws RecordNotFoundException {
    String sql = null;
    switch (field) {
      case USERNAME:
        sql = "UPDATE users SET username = ? WHERE id = ?;";
        break;
    }

    PreparedStatement ps = null;
    Connection connection = null;
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      switch (field) {
        case USERNAME:
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

  public void updatePassword(long id, String salt, String password) {
    String sql = "UPDATE users SET salt = ?, password = ? WHERE id = ?;";

    PreparedStatement ps = null;
    Connection connection = null;
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setString(1, salt);
      ps.setString(2, password);
      ps.setLong(3, id);
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
    DaoHelper.deleteById("users", id);
  }

  /**
   * Get the user by its username.
   *
   * @param username                    the username
   * @return                            the user
   * @throws RecordNotFoundException    if no user exists
   */
  public Entity<User> getByUsername(String username) throws RecordNotFoundException {
    String sql = "SELECT * FROM users WHERE username = ?;";
    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet result = null;
    Entity<User> user = null;
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setString(1, username);

      result = ps.executeQuery();
      if (!result.next()) {
        throw new RecordNotFoundException();
      }

      user = this.getUserFromResultSet(result);

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectDB.close(ps);
      ConnectDB.close(result);
      GlobalConnectionPool.pool.releaseConnection(connection);
    }
    return user;
  }


  /**
   * Get the users ordered from highest points to lowest.
   * If no results are found, it will return an empty array.
   *
   * @param index         the offset of the user in query results
   * @param numUsers      the number of users to get after the index
   * @return              the list of users
   */
  public ArrayList<UserPoints> getByPoints(int index, int numUsers) {
    String sql = String.format(
                 "SELECT users.*, total_score\n"
                +"FROM users\n"
                +"  INNER JOIN (\n"
                +"    SELECT user_id, SUM(score) AS total_score\n"
                +"    FROM (\n"
                +"        SELECT user_id, MAX(score) AS score\n"
                +"        FROM submissions\n"
                +"        GROUP BY user_id, problem_id\n"
                +"      )\n"
                +"    GROUP BY user_id\n"
                +"  ) ON users.id = user_id\n"
                +"ORDER BY total_score DESC\n"
                +"LIMIT %s OFFSET %s;", numUsers, index);

    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet result = null;
    ArrayList<UserPoints> users = new ArrayList<>();
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);

      result = ps.executeQuery();
      while (result.next()) {

        Entity<User> user = this.getUserFromResultSet(result);
        users.add(new UserPoints(user, result.getInt("total_score")));
      }



    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectDB.close(ps);
      ConnectDB.close(result);
      GlobalConnectionPool.pool.releaseConnection(connection);
    }
    return users;
  }

  public ArrayList<Entity<User>> getUsers(int index, int numUsers) {
    String sql = String.format(
                "SELECT * FROM users\n"
                +"ORDER BY username ASC\n"
                +"LIMIT %s OFFSET %s", numUsers, index);
    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet results = null;
    ArrayList<Entity<User>> users = new ArrayList<>();
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);

      results = ps.executeQuery();
      while (results.next()) {
        users.add(this.getUserFromResultSet(results));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectDB.close(ps);
      ConnectDB.close(results);
      GlobalConnectionPool.pool.releaseConnection(connection);
    }
    return users;
  }

  public int getPoints(long userId) {
    String sql =
      "SELECT SUM(score) AS points\n"
      +"FROM (\n"
      +"  SELECT MAX(score) AS score\n"
      +"  FROM submissions s\n"
      +"  WHERE s.user_id = ?\n"
      +"  GROUP BY s.problem_id\n"
      +");";
    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet result = null;
    int points = 0;
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setLong(1, userId);

      result = ps.executeQuery();
      if (!result.next()) {
        return points;
      }

      points = result.getInt("points");

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectDB.close(ps);
      ConnectDB.close(result);
      GlobalConnectionPool.pool.releaseConnection(connection);
    }
    return points;
  }

  private Entity<User> getUserFromResultSet(ResultSet result) throws SQLException {
    return new Entity<User>(
      result.getLong("id"),
      new User(
        result.getString("username"),
        result.getString("password"),
        UserType.valueOf(result.getString("user_type")),
        result.getString("salt")
      )
    );
  }
}
