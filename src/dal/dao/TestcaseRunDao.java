package dal.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import dal.connection.ConnectDB;
import dal.connection.GlobalConnectionPool;
import entities.Entity;
import entities.ExecutionStatus;
import entities.TestcaseRun;

/**
 * [description]
 * <p>
 * Created on 2021.01.10.
 *
 * @author Shari Sun, Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */
public class TestcaseRunDao implements Dao<TestcaseRun> {

  @Override
  public long add(TestcaseRun testcaseRun) {
    String sql = "INSERT INTO testcase_runs"
                +"(submission_id, batch_id, run_duration_millis, memory_usage, status, output)"
                +" VALUES (" + DaoHelper.generateWildcardString(6) + ");";
    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet key = null;
    long id = -1;
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setLong(1, testcaseRun.getSubmissionId());
      ps.setLong(2, testcaseRun.getBatchId());
      ps.setLong(3, testcaseRun.getRunDurationMillis());
      ps.setLong(4, testcaseRun.getMemoryUsageKb());
      ps.setString(5, testcaseRun.getStatus().name());
      ps.setString(6, testcaseRun.getOutput());

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
  public Entity<TestcaseRun> get(long id) throws RecordNotFoundException {
    String sql = "SELECT * FROM testcase_runs WHERE id = ?;";
    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet result = null;
    Entity<TestcaseRun> testcaseRun = null;
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setLong(1, id);

      result = ps.executeQuery();
      if (!result.next()) {
        throw new RecordNotFoundException();
      }

      testcaseRun = new Entity<TestcaseRun>(
        result.getLong("id"),
        new TestcaseRun(
          result.getLong("submission_id"),
          result.getLong("batch_id"),
          result.getLong("run_duration_millis"),
          result.getLong("memory_usage"),
          ExecutionStatus.valueOf(result.getString("status")),
          result.getString("output")
        )
      );

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectDB.close(ps);
      ConnectDB.close(result);
      GlobalConnectionPool.pool.releaseConnection(connection);
    }
    return testcaseRun;
  }

  @Override
  public ArrayList<Entity<TestcaseRun>> getList(long[] ids) {
    String sql = String.format(
      "SELECT * FROM testcase_runs WHERE id IN (%s);",
      DaoHelper.generateWildcardString(ids.length)
    );

    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet results = null;
    ArrayList<Entity<TestcaseRun>> testcaseRuns = new ArrayList<>();
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      for (int i = 0; i < ids.length; i++) {
        ps.setLong(i+1, ids[i]);
      }

      results = ps.executeQuery();
      while (results.next()) {
        testcaseRuns.add(new Entity<TestcaseRun>(
          results.getLong("id"),
          new TestcaseRun(
            results.getLong("submission_id"),
            results.getLong("batch_id"),
            results.getInt("run_duration_millis"),
            results.getInt("memory_usage"),
            ExecutionStatus.valueOf(results.getString("status")), 
            results.getString("output")
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
    return testcaseRuns;
  }

  @Override
  public void delete(long id) {
    DaoHelper.deleteById("testcase_runs", id);
  }


  public ArrayList<Entity<TestcaseRun>> getByBatch(long submissionId, long batchId) {
    String sql = "SELECT * FROM testcase_runs WHERE submission_id = ?, batch_id = ?;";
    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet results = null;
    ArrayList<Entity<TestcaseRun>> testcaseRuns = new ArrayList<>();
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setLong(1, submissionId);
      ps.setLong(2, batchId);

      results = ps.executeQuery();
      while (results.next()) {
        testcaseRuns.add(new Entity<TestcaseRun>(
          results.getLong("id"),
          new TestcaseRun(
            results.getLong("submission_id"),
            results.getLong("batch_id"),
            results.getLong("run_duration_millis"),
            results.getLong("memory_usage"),
            ExecutionStatus.valueOf(results.getString("status")),
            results.getString("output")
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
    return testcaseRuns;
  }

  public void deleteBySubmission(long submissionId) {

  }


}
