package dal.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import dal.connection.ConnectDB;
import dal.connection.GlobalConnectionPool;
import entities.Entity;
import entities.ExecutionStatus;
import entities.Language;
import entities.Submission;

/**
 * [description]
 * <p>
 * Created on 2021.01.10.
 *
 * @author Shari Sun, Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */

public class SubmissionDao implements Dao<Submission> {

  @Override
  public long add(Submission data) {
    String sql = "INSERT INTO submissions"
                +"(problem_id, user_id, code, language, created_at, status, score, run_duration)"
                +" VALUES (" + DaoHelper.generateWildcardString(8) + ");";
    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet key = null;
    long id = -1;
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setLong(1, data.getProblemId());
      ps.setLong(2, data.getUserId());
      ps.setString(3, data.getCode());
      ps.setString(4, data.getLanguage().name());
      ps.setString(5, data.getCreatedAt().toString());
      ps.setString(6, data.getStatus().name());
      ps.setInt(7, data.getScore());
      ps.setLong(8, data.getRunDurationMillis());

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
  public Entity<Submission> get(long id) throws RecordNotFoundException {
    String sql = "SELECT * FROM submissions WHERE id = ?;";
    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet result = null;
    Entity<Submission> batch = null;
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setLong(1, id);

      result = ps.executeQuery();
      if (!result.next()) {
        throw new RecordNotFoundException();
      }

      batch = new Entity<Submission>(
        result.getLong("id"),
        new Submission(
          result.getLong("problem_id"),
          result.getLong("user_id"),
          result.getString("code"),
          Language.valueOf(result.getString("language")),
          Timestamp.valueOf(result.getString("created_at")),
          ExecutionStatus.valueOf(result.getString("status")),
          result.getInt("score"),
          result.getLong("run_duration")
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
  public ArrayList<Entity<Submission>> getList(long[] ids) {
    String sql = String.format(
      "SELECT * FROM submissions WHERE id IN (%s);",
      DaoHelper.generateWildcardString(ids.length)
    );

    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet results = null;
    ArrayList<Entity<Submission>> submissions = new ArrayList<>();
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      for (int i = 0; i < ids.length; i++) {
        ps.setLong(i+1, ids[i]);
      }

      results = ps.executeQuery();
      while (results.next()) {
        submissions.add(new Entity<Submission>(
          results.getLong("id"),
          new Submission(
            results.getLong("problem_id"),
            results.getLong("user_id"),
            results.getString("code"),
            Language.valueOf(results.getString("language")),
            Timestamp.valueOf(results.getString("created_at")),
            ExecutionStatus.valueOf(results.getString("status")),
            results.getInt("score"),
            results.getLong("run_duration")
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
    return submissions;
  }

  @Override
  public void delete(long id) {
    DaoHelper.deleteById("submissions", id);
  }

}
