package dal.dao;

import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import javax.sql.rowset.serial.SerialBlob;

import dal.connection.ConnectDB;
import dal.connection.GlobalConnectionPool;
import entities.Entity;
import entities.Session;
import entities.SessionInfo;
import entities.entity_fields.SessionField;

/**
 * [description]
 * <p>
 * Created on 2021.01.10.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class SessionDao implements Dao<Session>, Updatable<SessionField> {

  private SerialBlob serialize(SessionInfo sessionInfo) {
    ByteArrayOutputStream bout = new ByteArrayOutputStream();
    ObjectOutputStream out = null;
    SerialBlob blob = null;
    try {
      out = new ObjectOutputStream(bout);
      out.writeObject(sessionInfo);
      out.flush();
      byte[] data = bout.toByteArray();
      blob = new SerialBlob(data);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      ConnectDB.close(bout);
      ConnectDB.close(out);
    }
    return blob;
  }

  private SessionInfo deserialize(SerialBlob blob) {
    ObjectInputStream in = null;
    SessionInfo sessionInfo = null;
    try {
      in = new ObjectInputStream(blob.getBinaryStream());
      sessionInfo = (SessionInfo)in.readObject();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      ConnectDB.close(in);
    }
    return sessionInfo;
  }

  @Override
  public long add(Session session) {
    String sql = "INSERT INTO sessions(token, session_info, last_active)"
                +" VALUES (?, ?, ?);";
    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet key = null;
    long id = -1;
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setString(1, session.getToken());
      ps.setBlob(2, this.serialize(session.getSessionInfo()));
      ps.setString(3, session.getLastActive().toString());

      ps.executeUpdate();
      key = ps.getGeneratedKeys();
      key.next();
      id = key.getLong(1);
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
  public Entity<Session> get(long id) throws RecordNotFoundException {
    String sql = "SELECT * FROM sessions WHERE id = ?;";
    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet result = null;
    Entity<Session> user = null;
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setLong(1, id);

      result = ps.executeQuery();
      if (!result.next()) {
        throw new RecordNotFoundException();
      }

      user = this.getSessionFromResultSet(result);

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
  public ArrayList<Entity<Session>> getList(long[] ids) {
    String sql = String.format(
      "SELECT * FROM sessions WHERE id IN (%s);",
      DaoHelper.getParamString(ids.length)
    );

    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet results = null;
    ArrayList<Entity<Session>> sessions = new ArrayList<>();
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      for (int i = 0; i < ids.length; i++) {
        ps.setLong(i+1, ids[i]);
      }

      results = ps.executeQuery();
      while (results.next()) {
        sessions.add(this.getSessionFromResultSet(results));
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
  public <V> void update(long id, SessionField field, V value) throws RecordNotFoundException {
    String sql = null;
    switch (field) {
      case SESSION_INFO:
        sql = "UPDATE sessions SET session_info = ? WHERE id = ?;";
        break;
      case LAST_ACTIVE:
        sql = "UPDATE sessions SET last_active = ? WHERE id = ?;";
        break;
    }

    PreparedStatement ps = null;
    Connection connection = null;
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      switch (field) {
        case SESSION_INFO:
          ps.setBlob(1, this.serialize((SessionInfo)value));
          break;
        case LAST_ACTIVE:
          ps.setString(1, ((Timestamp)value).toString());
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
    DaoHelper.deleteById("sessions", id);
  }

  public void deleteSessionFromBefore(Timestamp time) {
    String sql = "DELETE FROM sessions WHERE last_active = ?;";

    PreparedStatement ps = null;
    Connection connection = null;
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setString(1, time.toString());
      ps.executeUpdate();

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectDB.close(ps);
      GlobalConnectionPool.pool.releaseConnection(connection);
    }
  }

  private Entity<Session> getSessionFromResultSet(ResultSet result) throws SQLException {
    return new Entity<Session>(
      result.getLong("id"),
      new Session(
        result.getString("token"),
        this.deserialize((SerialBlob)result.getBlob("session_info")),
        Timestamp.valueOf(result.getString("last_active"))
      )
    );
  }

}
