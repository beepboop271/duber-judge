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
import entities.entity_fields.TestcaseField;

/**
 * [description]
 * <p>
 * Created on 2021.01.10.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class TestcaseRunDao implements Dao<TestcaseRun> {

  @Override
  public long add(TestcaseRun testcase) {
    // TODO Auto-generated method stub
    return 0;
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
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void delete(long id) {

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
