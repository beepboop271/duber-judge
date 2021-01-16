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
      +"(problem_id, user_id, code, language, created_at, status, score, run_duration_millis)"
      +" VALUES (" + DaoHelper.getParamString(8) + ");";
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
    Entity<Submission> submission = null;
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setLong(1, id);

      result = ps.executeQuery();
      if (!result.next()) {
        throw new RecordNotFoundException();
      }

      submission = this.getSubmissionByResultSet(result);

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectDB.close(ps);
      ConnectDB.close(result);
      GlobalConnectionPool.pool.releaseConnection(connection);
    }
    return submission;
  }

  @Override
  public ArrayList<Entity<Submission>> getList(long[] ids) {
    String sql = String.format(
      "SELECT * FROM submissions WHERE id IN (%s);",
      DaoHelper.getParamString(ids.length)
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
        submissions.add(this.getSubmissionByResultSet(results));
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
  public void deleteById(long id) {
    DaoHelper.deleteById("submissions", id);
  }

  private Entity<Submission> getSubmissionByResultSet(ResultSet result) throws SQLException {
    return new Entity<Submission>(
      result.getLong("id"),
      new Submission(
        result.getLong("problem_id"),
        result.getLong("user_id"),
        result.getString("code"),
        Language.valueOf(result.getString("language")),
        Timestamp.valueOf(result.getString("created_at")),
        ExecutionStatus.valueOf(result.getString("status")),
        result.getInt("score"),
        result.getLong("run_duration_millis")
      )
    );
  }

  /**
   * Get all the submissions of a problem ordered from highest point to lowest.
   * The index indicates the offset of the record in the database.
   * If no results are found, it will return an empty array.
   *
   * @param problemId        the user id
   * @param index            the offset of the submission in the query result
   * @param numSubmissions   the number of submissions to retrieve
   * @return                 the list of submissions
   */
  public ArrayList<Entity<Submission>> getByProblem(long problemId, int index, int numSubmissions) {
    String sql = String.format(
                "SELECT * FROM submissions"
                +"WHERE problem_id = ?"
                +"ORDER BY created_at DESC"
                +"LIMIT %s OFFSET %s", numSubmissions, index);
    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet results = null;
    ArrayList<Entity<Submission>> submissions = new ArrayList<>();
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setLong(1, problemId);

      results = ps.executeQuery();
      while (results.next()) {
        submissions.add(this.getSubmissionByResultSet(results));
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

  /**
   * Gets the unique submission of a user.
   * Meaning that it selects the highest submission for each problem
   * and returns only that result.
   *
   * @param userId           the user Id
   * @param index            the offset of the submission in the query result
   * @param numSubmissions   the number of submissions to retrive
   * @return                 the list of submissions
   */
  public ArrayList<Entity<Submission>>
    getUniqueSubmissions(long userId, int index, int numSubmissions) {
    String sql = String.format(
                "SELECT *, MAX(score) AS highest_score"
                +"FROM submissions"
                +"WHERE user_id = ?"
                +"GROUP BY problem_id"
                +"ORDER BY score DESC"
                +"LIMIT %s OFFSET %s", numSubmissions, index);
    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet results = null;
    ArrayList<Entity<Submission>> submissions = new ArrayList<>();
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setLong(1, userId);

      results = ps.executeQuery();
      while (results.next()) {
        submissions.add(this.getSubmissionByResultSet(results));
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

  /**
   * Get all the submissions made by a user ordered from latest to earliest.
   * The index indicates the offset of the redord in the database.
   * If no results are found, it will return an empty array.
   *
   * @param userId           the user id
   * @param index            the offset of the submission in the query result
   * @param numSubmissions   the number of submissions to retrieve
   * @return                 the list of submissions
   */
  public ArrayList<Entity<Submission>> getByUser(long userId, int index, int numSubmissions) {
    String sql = String.format(
                "SELECT * FROM submissions"
                +"WHERE user_id = ?"
                +"ORDER BY created_at DESC"
                +"LIMIT %s OFFSET %s", numSubmissions, index);
    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet results = null;
    ArrayList<Entity<Submission>> submissions = new ArrayList<>();
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setLong(1, userId);

      results = ps.executeQuery();
      while (results.next()) {
        submissions.add(this.getSubmissionByResultSet(results));
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

  public ArrayList<Entity<Submission>>
    getByProblemAndStatus(long problemId, ExecutionStatus status, int index, int numProblems) {
    String sql = "SELECT * FROM submissions WHERE problem_id = ?, status = ?;";
    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet results = null;
    ArrayList<Entity<Submission>> submissions = new ArrayList<>();
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setLong(1, problemId);
      ps.setString(2, status.toString());

      results = ps.executeQuery();
      while (results.next()) {
        submissions.add(this.getSubmissionByResultSet(results));
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

  public void deleteByProblem(long problemId) {
    String sql = "DELETE FROM submissions WHERE problem_id = ?;";

    PreparedStatement ps = null;
    Connection connection = null;
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setLong(1, problemId);

      ps.executeUpdate();

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectDB.close(ps);
      GlobalConnectionPool.pool.releaseConnection(connection);
    }
  }

  public int countByUserAndProblem(long userId, long problemId) {
    String sql = "SELECT COUNT(*) FROM submissions WHERE user_id = ?, problem_id = ?;";
    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet result = null;
    int count = 0;
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setLong(1, userId);
      ps.setLong(2, problemId);

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
}
