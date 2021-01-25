package dal.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import dal.connection.ConnectDB;
import dal.connection.GlobalConnectionPool;
import entities.Entity;
import entities.IllegalCode;
import entities.Language;
import entities.entity_fields.IllegalCodeField;

/**
 * {@code DAO} for {@link IllegalCode}.
 * <p>
 * Created on 2021.01.22.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class IllegalCodeDao implements Dao<IllegalCode>, Updatable<IllegalCodeField> {

  @Override
  public <V> void update(long id, IllegalCodeField field, V value)
    throws RecordNotFoundException {
    String sql = null;
    switch (field) {
      case LANGUAGE:
        sql = "UPDATE illegal_codes SET language = ? WHERE id = ?;";
        break;
      case CONTENT:
        sql = "UPDATE illegal_codes SET content = ? WHERE id = ?;";
        break;
    }

    PreparedStatement ps = null;
    Connection connection = null;
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      switch (field) {
        case LANGUAGE:
          ps.setString(1, ((Language)value).toString());
          break;
        case CONTENT:
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
  public long add(IllegalCode data) {
    String sql = "INSERT INTO illegal_codes"
                +"(language, content)"
                +" VALUES (?, ?);";
    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet key = null;
    long id = -1;
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setString(1, data.getLanguage().toString());
      ps.setString(2, data.getContent());

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
  public Entity<IllegalCode> get(long id) throws RecordNotFoundException {
    String sql = "SELECT * FROM illegal_codes WHERE id = ?;";
    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet result = null;
    Entity<IllegalCode> illegalCode = null;
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setLong(1, id);

      result = ps.executeQuery();
      if (!result.next()) {
        throw new RecordNotFoundException();
      }

      illegalCode = this.getIllegalCodeFromResultSet(result);

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectDB.close(ps);
      ConnectDB.close(result);
      GlobalConnectionPool.pool.releaseConnection(connection);
    }
    return illegalCode;
  }

  @Override
  public ArrayList<Entity<IllegalCode>> getList(long[] ids) {
    String sql = String.format(
      "SELECT * FROM illegal_codes WHERE id IN (%s);",
      DaoHelper.getParamString(ids.length)
    );

    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet results = null;
    ArrayList<Entity<IllegalCode>> illegalCodes = new ArrayList<>();
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      for (int i = 0; i < ids.length; i++) {
        ps.setLong(i+1, ids[i]);
      }

      results = ps.executeQuery();
      while (results.next()) {
        illegalCodes.add(this.getIllegalCodeFromResultSet(results));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectDB.close(ps);
      ConnectDB.close(results);
      GlobalConnectionPool.pool.releaseConnection(connection);
    }
    return illegalCodes;
  }

  @Override
  public void deleteById(long id) {
    DaoHelper.deleteById("illegal_codes", id);
  }

  private Entity<IllegalCode> getIllegalCodeFromResultSet(ResultSet result)
    throws SQLException {
    return new Entity<IllegalCode>(
      result.getLong("id"),
      new IllegalCode(
        Language.valueOf(result.getString("language")),
        result.getString("content")
      )
    );
  }



}
