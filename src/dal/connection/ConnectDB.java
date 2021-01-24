package dal.connection;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;


/**
 * A static class used for creating new {@code Database connections}
 * and initializing the database given a {@code Connection}.
 * It also provides a method to conveniently close resources
 * such as {@code PreparedStatment, ResultSet, etc}.
 * <p>
 * Created on 2021.01.07.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class ConnectDB {

  /**
   * Gets a new {@code Connection} to the database given a specified file path.
   *
   * @param filePath         The path to the .db file.
   * @return                 The connection to the database.
   */
  public static Connection getConnection(String filePath) {
    Connection connection = null;
    try {
      String url = "jdbc:sqlite:"+filePath;
      new File(filePath).getParentFile().mkdirs();

      Properties properties = new Properties();
      properties.setProperty("foreign_keys", "ON");
      connection = DriverManager.getConnection(url, properties);
      System.out.println("new connection established");
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return connection;
  }

  /**
   * Closes a resource.
   *
   * @param resource       The resource to close.
   */
  public static void close(AutoCloseable resource) {
    if (resource != null) {
      try {
        resource.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Create all required table upon first connecting to database.
   *
   * @param connection         The database connection.
   */
  public static void initialize(Connection connection) {
    String[] createTableSql = new String[] {
      "CREATE TABLE IF NOT EXISTS users ("
        +"id         INTEGER PRIMARY KEY,"
        +"username   TEXT UNIQUE NOT NULL,"
        +"password   TEXT NOT NULL,"
        +"user_type  TEXT NOT NULL,"
        +"salt       TEXT NOT NULL"
        +");",
      "CREATE TABLE IF NOT EXISTS contests ("
        +"id                    INTEGER PRIMARY KEY,"
        +"creator_id            INTEGER NOT NULL,"
        +"description           TEXT NOT NULL,"
        +"title                 TEXT UNIQUE NOT NULL,"
        +"start_time            TEXT NOT NULL,"
        +"end_time              TEXT NOT NULL,"
        +"status                TEXT NOT NULL,"
        +"duration_minutes      INTEGER NOT NULL,"
        +"publishing_state      TEXT NOT NULL,"
        +"FOREIGN KEY(creator_id) REFERENCES users(id) ON DELETE CASCADE"
        +");",
      "CREATE TABLE IF NOT EXISTS contest_sessions ("
        +"id          INTEGER PRIMARY KEY,"
        +"contest_id  INTEGER NOT NULL,"
        +"user_id     INTEGER NOT NULL,"
        +"created_at  TEXT NOT NULL,"
        +"status      TEXT NOT NULL,"
        +"score       INTEGER NOT NULL,"
        +"FOREIGN KEY(contest_id) REFERENCES contests(id) ON DELETE CASCADE,"
        +"FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE"
        +");",
      "CREATE TABLE IF NOT EXISTS problems ("
        +"id                    INTEGER PRIMARY KEY,"
        +"problem_type          TEXT NOT NULL,"
        +"category              TEXT NOT NULL,"
        +"creator_id            INTEGER NOT NULL,"
        +"created_at            TEXT NOT NULL,"
        +"last_modified_at      TEXT NOT NULL,"
        +"title                 TEXT UNIQUE NOT NULL,"
        +"description           TEXT NOT NULL,"
        +"points                INTEGER NOT NULL,"
        +"time_limit_millis     INTEGER NOT NULL,"
        +"memory_limit_kb       INTEGER NOT NULL,"
        +"output_limit_kb       INTEGER NOT NULL,"
        +"num_submissions       INTEGER NOT NULL,"
        +"cleared_submissions   INTEGER NOT NULL,"
        +"contest_id            INTEGER,"
        +"submissions_limit     INTEGER,"
        +"editorial             TEXT,"
        +"publishing_state      TEXT NOT NULL,"
        +"FOREIGN KEY(contest_id) REFERENCES contests(id) ON DELETE CASCADE,"
        +"FOREIGN KEY(creator_id) REFERENCES users(id)"
        +");",
      "CREATE TABLE IF NOT EXISTS clarifications ("
        +"id            INTEGER PRIMARY KEY,"
        +"created_at    TEXT NOT NULL,"
        +"problem_id    INTEGER NOT NULL,"
        +"user_id       INTEGER NOT NULL,"
        +"message       TEXT NOT NULL,"
        +"response      TEXT,"
        +"FOREIGN KEY(problem_id) REFERENCES problems(id) ON DELETE CASCADE,"
        +"FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE"
        +");",
      "CREATE TABLE IF NOT EXISTS batches ("
        +"id            INTEGER PRIMARY KEY,"
        +"creator_id    INTEGER NOT NULL,"
        +"problem_id    INTEGER NOT NULL,"
        +"sequence      INTEGER NOT NULL,"
        +"points        INTEGER NOT NULL,"
        +"UNIQUE(problem_id, sequence),"
        +"FOREIGN KEY(creator_id) REFERENCES users(id),"
        +"FOREIGN KEY(problem_id) REFERENCES problems(id) ON DELETE CASCADE"
        +");",
      "CREATE TABLE IF NOT EXISTS testcases ("
        +"id          INTEGER PRIMARY KEY,"
        +"creator_id  INTEGER NOT NULL,"
        +"batch_id    INTEGER NOT NULL,"
        +"sequence    INTEGER NOT NULL,"
        +"input       TEXT NOT NULL,"
        +"output      TEXT NOT NULL,"
        +"UNIQUE(batch_id, sequence),"
        +"FOREIGN KEY(creator_id) REFERENCES users(id),"
        +"FOREIGN KEY(batch_id) REFERENCES batches(id) ON DELETE CASCADE"
        +");",
      "CREATE TABLE IF NOT EXISTS submissions ("
        +"id                  INTEGER PRIMARY KEY,"
        +"problem_id          INTEGER NOT NULL,"
        +"user_id             INTEGER NOT NULL,"
        +"code                TEXT NOT NULL,"
        +"language            TEXT NOT NULL,"
        +"created_at          TEXT NOT NULL,"
        +"status              TEXT NOT NULL,"
        +"score               INTEGER NOT NULL,"
        +"run_duration_millis INTEGER NOT NULL,"
        +"memory_usage_b      INTEGER NOT NULL,"
        +"FOREIGN KEY(problem_id) REFERENCES problems(id) ON DELETE CASCADE,"
        +"FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE"
        +");",
      "CREATE TABLE IF NOT EXISTS testcase_runs ("
        +"id                   INTEGER PRIMARY KEY,"
        +"submission_id        INTEGER NOT NULL,"
        +"batch_id             INTEGER NOT NULL,"
        +"run_duration_millis  INTEGER NOT NULL,"
        +"memory_usage_b       INTEGER NOT NULL,"
        +"status               TEXT NOT NULL,"
        +"output               TEXT NOT NULL,"
        +"FOREIGN KEY(submission_id) REFERENCES submissions(id) ON DELETE CASCADE,"
        +"FOREIGN KEY(batch_id) REFERENCES batches(id) ON DELETE CASCADE"
        +");",
      "CREATE TABLE IF NOT EXISTS illegal_codes ("
        +"id                   INTEGER PRIMARY KEY,"
        +"language             TEXT NOT NULL,"
        +"content              TEXT NOT NULL"
        +");",
      };
    String[] createIdxSql = new String[]{
      "CREATE INDEX IF NOT EXISTS idx_username      ON users(username)",
      "CREATE INDEX IF NOT EXISTS idx_user_id       ON contest_sessions(user_id)",
      "CREATE INDEX IF NOT EXISTS idx_created_at    ON contest_sessions(created_at)",
      "CREATE INDEX IF NOT EXISTS idx_title         ON problems(title)",
      "CREATE INDEX IF NOT EXISTS idx_contest_id    ON problems(contest_id)",
      "CREATE INDEX IF NOT EXISTS idx_problem_user  ON clarifications(problem_id, user_id)",
      "CREATE INDEX IF NOT EXISTS idx_problem_id    ON batches(problem_id)",
      "CREATE INDEX IF NOT EXISTS idx_batch_id      ON testcases(batch_id)",
      "CREATE INDEX IF NOT EXISTS idx_problem_id    ON submissions(problem_id)",
      "CREATE INDEX IF NOT EXISTS idx_user_id       ON submissions(user_id)",
      "CREATE INDEX IF NOT EXISTS idx_submissions   ON testcase_runs(submission_id, batch_id)",
    };

    Statement statement = null;
    try {
      statement = connection.createStatement();
      for (int i = 0; i < createTableSql.length; i++) {
        statement.execute(createTableSql[i]);
      }

      for (int i = 0; i < createIdxSql.length; i++) {
        statement.execute(createIdxSql[i]);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      ConnectDB.close(statement);
    }
  }
}
