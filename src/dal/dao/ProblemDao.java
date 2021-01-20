package dal.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;

import dal.connection.ConnectDB;
import dal.connection.GlobalConnectionPool;
import entities.Category;
import entities.ContestProblem;
import entities.Entity;
import entities.PracticeProblem;
import entities.Problem;
import entities.ProblemType;
import entities.entity_fields.ProblemField;

/**
 * [description]
 * <p>
 * Created on 2021.01.10.
 *
 * @author Shari Sun, Candice Zhang
 * @version 1.0.0
 * @since 1.0.0
 */
public class ProblemDao implements Dao<Problem>, Updatable<ProblemField> {

  private PracticeProblem convertToPracticeProblem(Problem problem) {
    return new PracticeProblem(
      problem.getCategory(),
      problem.getCreatorId(),
      problem.getCreatedAt(),
      problem.getLastModifiedAt(),
      problem.getTitle(),
      problem.getDescription(),
      problem.getPoints(),
      problem.getTimeLimitMillis(),
      problem.getMemoryLimitKb(),
      problem.getOutputLimitKb(),
      problem.getNumSubmissions(),
      ""
    );
  }

  @Override
  public <V> void update(long id, ProblemField field, V value)
    throws RecordNotFoundException, IllegalArgumentException {
    Problem problem = this.get(id).getContent();
    ProblemType problemType = problem.getProblemType();

    String element = "";
    switch (field) {
      case PROBLEM_TYPE:
        if (problemType == (ProblemType)value) {
          return;
        }
        if ((ProblemType)value == ProblemType.CONTEST) {
          throw new IllegalArgumentException(
            "Cannot convert a " + problemType.name() + " problem into a contest problem"
          );
        } else if ((ProblemType)value == ProblemType.PRACTICE) {
          this.add(this.convertToPracticeProblem(problem));
          this.deleteById(id);
        }
        return;

      case CATEGORY:
        element = "category";
        break;
      case CREATOR_ID:
        element = "creator_id";
        break;
      case CREATED_AT:
        element = "created_at";
        break;
      case LAST_MODIFIED_AT:
        element = "last_modified_at";
        break;
      case TITLE:
        element = "title";
        break;
      case DESCRIPTION:
        element = "description";
        break;
      case POINTS:
        element = "points";
        break;
      case TIME_LIMIT_MILLIS:
        element = "time_limit_millis";
        break;
      case MEMORY_LIMIT_KB:
        element = "memory_limit_kb";
        break;
      case OUTPUT_LIMIT_KB:
        element = "output_limit_kb";
        break;
      case NUM_SUBMISSIONS:
        element = "num_submissions";
        break;
      case CLEARED_SUBMISSIONS:
        element = "cleared_submissions";
        break;

      // contest problem exclusive fields
      case SUBMISSIONS_LIMIT:
        if (problemType != ProblemType.CONTEST) {
          throw new IllegalArgumentException("UnknownField");
        }
        element = "submissions_limit";
        break;
      case CONTEST_ID:
        if (problemType != ProblemType.CONTEST) {
          throw new IllegalArgumentException("UnknownField");
        }
        element = "contest_id";
        break;

      // practice problem exclusive fields
      case EDITORIAL:
        if (problemType != ProblemType.PRACTICE) {
          throw new IllegalArgumentException("UnknownField");
        }
        element = "editorial";
        break;
    }
    String sql = "UPDATE contests SET " + element + " = ? WHERE id = ?;";
    PreparedStatement ps = null;
    Connection connection = null;
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      switch (field) {
        case PROBLEM_TYPE:
          break;
        case CATEGORY:
          ps.setString(1, ((Category)value).toString());
          break;
        case CREATOR_ID:
          ps.setLong(1, (long)value);
          break;
        case CREATED_AT:
          ps.setString(1, ((Timestamp)value).toString());
          break;
        case LAST_MODIFIED_AT:
          ps.setString(1, ((Timestamp)value).toString());
          break;
        case TITLE:
          ps.setInt(1, (int)value);
          break;
        case DESCRIPTION:
          ps.setString(1, (String)value);
          break;
        case POINTS:
          ps.setInt(1, (int)value);
          break;
        case TIME_LIMIT_MILLIS:
          ps.setInt(1, (int)value);
          break;
        case MEMORY_LIMIT_KB:
          ps.setInt(1, (int)value);
          break;
        case OUTPUT_LIMIT_KB:
          ps.setInt(1, (int)value);
          break;
        case NUM_SUBMISSIONS:
          ps.setInt(1, (int)value);
          break;
        case CLEARED_SUBMISSIONS:
          ps.setInt(1, (int)value);
          break;

        case SUBMISSIONS_LIMIT:
          ps.setInt(1, (int)value);
          break;
        case CONTEST_ID:
          ps.setLong(1, (long)value);
          break;

        case EDITORIAL:
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
  public long add(Problem data) {
    String sql = "INSERT INTO problems"
                +"(problem_type, category, creator_id, created_at, last_modified_at,"
                +" title, description, points, time_limit_millis, memory_limit_kb, output_limit_kb,"
                +" num_submissions, cleared_submissions,"
                +" contest_id, submissions_limit, editorial)"
                +" VALUES (" + DaoHelper.getParamString(16) + ");";
    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet key = null;
    long id = -1;
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setString(1, data.getProblemType().name()); // problem_type
      ps.setString(2, data.getCategory().name()); // category
      ps.setLong(3, data.getCreatorId()); // creator_id
      ps.setString(4, data.getCreatedAt().toString()); // created_at
      ps.setString(5, data.getLastModifiedAt().toString()); // last_modified_at
      ps.setString(6, data.getTitle()); // title
      ps.setString(7, data.getDescription()); // description
      ps.setInt(8, data.getPoints()); // points
      ps.setInt(9, data.getTimeLimitMillis()); // time_limit
      ps.setInt(10, data.getMemoryLimitKb()); // memory_limit
      ps.setInt(11, data.getOutputLimitKb()); // output_limit
      ps.setInt(12, data.getNumSubmissions()); // num_submissions
      ps.setInt(13, data.getClearedSubmissions()); // cleared_submissions

      // contest_id, submissions_limit
      switch (data.getProblemType()) {
        case CONTEST:
          ContestProblem cp = (ContestProblem)data;
          ps.setLong(14, cp.getContestId());
          ps.setInt(15, cp.getSubmissionsLimit());
          break;
        default:
          ps.setNull(14, Types.NULL);
          ps.setNull(15, Types.NULL);
          break;
      }
      // editorial
      switch (data.getProblemType()) {
        case PRACTICE:
          PracticeProblem pp = (PracticeProblem)data;
          ps.setString(16, pp.getEditorial());
          break;
        default:
          ps.setNull(16, Types.NULL);
          break;
      }

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
  public Entity<Problem> get(long id) throws RecordNotFoundException {
    String sql = "SELECT * FROM problems WHERE id = ?;";
    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet result = null;
    Entity<Problem> problem = null;
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setLong(1, id);

      result = ps.executeQuery();
      if (!result.next()) {
        throw new RecordNotFoundException();
      }

      problem = this.getProblemFromResultSet(result);

    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectDB.close(ps);
      ConnectDB.close(result);
      GlobalConnectionPool.pool.releaseConnection(connection);
    }
    return problem;
  }

  @Override
  public ArrayList<Entity<Problem>> getList(long[] ids) {
    String sql = String.format(
      "SELECT * FROM contests WHERE id IN (%s);",
      DaoHelper.getParamString(ids.length)
    );

    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet results = null;
    ArrayList<Entity<Problem>> problems = new ArrayList<>();
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      for (int i = 0; i < ids.length; i++) {
        ps.setLong(i+1, ids[i]);
      }

      results = ps.executeQuery();
      while (results.next()) {
        problems.add(this.getProblemFromResultSet(results));
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

  @Override
  public void deleteById(long id) {
    DaoHelper.deleteById("problems", id);
  }

  private Entity<Problem> getProblemFromResultSet(ResultSet result) throws SQLException {
    Entity<Problem> problem = null;
    ProblemType type = ProblemType.valueOf(result.getString("problem_type"));
    switch (type) {
      case CONTEST:
        problem = new Entity<Problem>(
          result.getLong("id"),
          new ContestProblem(
            Category.valueOf(result.getString("category")),
            result.getLong("creator_id"),
            Timestamp.valueOf(result.getString("created_at")),
            Timestamp.valueOf(result.getString("last_modified_at")),
            result.getString("title"),
            result.getString("description"),
            result.getInt("points"),
            result.getInt("time_limit_millis"),
            result.getInt("memory_limit_kb"),
            result.getInt("output_limit_kb"),
            result.getInt("num_submissions"),
            result.getInt("submissions_limit"),
            result.getLong("contest_id")
          )
        );
        break;

      case PRACTICE:
        problem = new Entity<Problem>(
          result.getLong("id"),
          new PracticeProblem(
            Category.valueOf(result.getString("category")),
            result.getLong("creator_id"),
            Timestamp.valueOf(result.getString("created_at")),
            Timestamp.valueOf(result.getString("last_modified_at")),
            result.getString("title"),
            result.getString("description"),
            result.getInt("points"),
            result.getInt("time_limit_millis"),
            result.getInt("memory_limit_kb"),
            result.getInt("output_limit_kb"),
            result.getInt("num_submissions"),
            result.getString("editorial")
          )
        );
        break;
    }
    return problem;
  }

  public ArrayList<Entity<Problem>> getAllByContest(long contestId) {
    String sql = "SELECT * FROM problems WHERE contest_id = ?;";
    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet results = null;
    ArrayList<Entity<Problem>> problems = new ArrayList<>();
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setLong(1, contestId);

      results = ps.executeQuery();
      while (results.next()) {
        problems.add(this.getProblemFromResultSet(results));
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

  public ArrayList<Entity<Problem>> getPracticeProblems(int index, int numProblems) {
    String sql = String.format(
                "SELECT * FROM problems"
                +"WHERE problem_type = %s"
                +"ORDER BY created_at DESC"
                +"LIMIT %s OFFSET %s",
      ProblemType.PRACTICE.toString(), numProblems, index
    );
    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet results = null;
    ArrayList<Entity<Problem>> problems = new ArrayList<>();
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);

      results = ps.executeQuery();
      while (results.next()) {
        problems.add(this.getProblemFromResultSet(results));
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

  public ArrayList<Entity<Problem>>
    getPracticeProblemsByCategory(Category category, int index, int numProblems) {
    String sql = String.format(
                "SELECT * FROM problems"
                +"WHERE problem_type = %s, category = ?"
                +"ORDER BY created_at DESC"
                +"LIMIT %s OFFSET %s",
      ProblemType.PRACTICE.toString(), numProblems, index
    );
    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet results = null;
    ArrayList<Entity<Problem>> problems = new ArrayList<>();
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setString(1, category.toString());

      results = ps.executeQuery();
      while (results.next()) {
        problems.add(this.getProblemFromResultSet(results));
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

  public ArrayList<Entity<Problem>>
    getPracticeProblemsByCreator(long creatorId, int index, int numProblems) {
    String sql = String.format(
                "SELECT * FROM problems"
                +"WHERE problem_type = %s, creator_id = ?"
                +"ORDER BY created_at DESC"
                +"LIMIT %s OFFSET %s",
      ProblemType.PRACTICE.toString(), numProblems, index
    );
    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet results = null;
    ArrayList<Entity<Problem>> problems = new ArrayList<>();
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setLong(1, creatorId);

      results = ps.executeQuery();
      while (results.next()) {
        problems.add(this.getProblemFromResultSet(results));
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

  public ArrayList<Entity<Problem>>
    getPracticeProblemsByPoints(int min, int max, int index, int numProblems) {
    String sql = String.format(
                "SELECT * FROM problems"
                +"WHERE problem_type = %s, points > ?, points < ?"
                +"ORDER BY created_at DESC"
                +"LIMIT %s OFFSET %s",
      ProblemType.PRACTICE.toString(), numProblems, index
    );
    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet results = null;
    ArrayList<Entity<Problem>> problems = new ArrayList<>();
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setInt(1, min);
      ps.setInt(2, max);

      results = ps.executeQuery();
      while (results.next()) {
        problems.add(this.getProblemFromResultSet(results));
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

  public ArrayList<Entity<Problem>>
    getPracticeProblemsByNumSubmissions(int min, int max, int index, int numProblems) {
    String sql = String.format(
                "SELECT * FROM problems"
                +"WHERE problem_type = %s, num_submissions > ?, num_submissions < ?"
                +"ORDER BY created_at DESC"
                +"LIMIT %s OFFSET %s",
      ProblemType.PRACTICE.toString(), numProblems, index
    );
    PreparedStatement ps = null;
    Connection connection = null;
    ResultSet results = null;
    ArrayList<Entity<Problem>> problems = new ArrayList<>();
    try {
      connection = GlobalConnectionPool.pool.getConnection();
      ps = connection.prepareStatement(sql);
      ps.setInt(1, min);
      ps.setInt(2, max);

      results = ps.executeQuery();
      while (results.next()) {
        problems.add(this.getProblemFromResultSet(results));
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
