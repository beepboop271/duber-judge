package database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import database.entities.ContestsTable;
import database.entities.ProblemsTable;
import database.entities.Tables;
import database.entities.UsersTable;

/**
 * [description]
 * <p>
 * Created on 2021.01.02.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class Database {
  private Connection connection;

  public Database() {
    this.connect();
    this.initializeTables();
  }

  private void connect() {
    try {
      String filePath = System.getProperty("user.dir")+"/database/dubj.db";
      String url = "jdbc:sqlite:"+filePath;
      new File(filePath).getParentFile().mkdirs();

      this.connection = DriverManager.getConnection(url);
      System.out.println("database successfully connected");
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  private void initializeTables() {
    String[] sql = new String[] {
      "CREATE TABLE IF NOT EXISTS" + Tables.users + "(\n"
        + UsersTable.id            + " INTEGER PRIMARY KEY,\n"
        + UsersTable.user_type     + " TEXT,\n" // admin/classic
        + UsersTable.username      + " TEXT,\n"
        + UsersTable.salt          + " TEXT,\n"
        + UsersTable.password      + " TEXT,\n"
        + UsersTable.description   + " TEXT,\n"
        + UsersTable.profile_photo + " BLOB\n"
        + ");",
      "CREATE TABLE IF NOT EXISTS"  + Tables.contests + "(\n"
        + ContestsTable.id          + " INTEGER PRIMARY KEY,\n"
        + ContestsTable.title       + " TEXT,\n" // admin/classic
        + ContestsTable.description + " TEXT,\n"
        + ContestsTable.start_time  + " TEXT,\n"
        + ContestsTable.end_time    + " TEXT\n"
        +");",
      "CREATE TABLE IF NOT EXISTS"      + Tables.problems + "(\n"
        + ProblemsTable.id              + " INTEGER PRIMARY KEY,\n"
        + ProblemsTable.problem_type    + " TEXT,\n" // contest/practice
        + ProblemsTable.contest         + " INTEGER,\n"
        + ProblemsTable.description     + " TEXT,\n"
        + ProblemsTable.editorial       + " TEXT,\n"
        + ProblemsTable.category        + " TEXT,\n" //graph/string/etc
        + ProblemsTable.title           + " TEXT,\n"
        + ProblemsTable.points          + " INTEGER,\n"
        + ProblemsTable.num_submissions + " INTEGER,\n"
        + ProblemsTable.clear_rate      + " REAL\n"
        +");",
      "CREATE TABLE IF NOT EXISTS batches (\n"
        +"id INTEGER PRIMARY KEY,\n"
        +"problem INTEGER,\n"
        +"sequence INTEGER,\n"
        +"score INTEGER\n"
        +");",
      "CREATE TABLE IF NOT EXISTS testcases (\n"
        +"id INTEGER PRIMARY KEY,\n"
        +"batch INTEGER,\n"
        +"sequence INTEGER,\n"
        +"input TEXT,\n"
        +"output TEXT\n"
        +");",
      "CREATE TABLE IF NOT EXISTS submissions (\n"
        +"id INTEGER PRIMARY KEY,\n"
        +"problem INTEGER,\n"
        +"user INTEGER,\n"
        +"code TEXT,\n"
        +"language TEXT,\n"
        +"submission_time TEXT,\n"
        +"status TEXT,\n" // ac/wa/tle/etc
        +"score INTEGER\n"
        +");",
      "CREATE TABLE IF NOT EXISTS testcase_runs (\n"
        +"id INTEGER PRIMARY KEY,\n"
        +"submission INTEGER,\n"
        +"testcase INTEGER,\n"
        +"run_duration REAL,\n"
        +"memory_usage REAL,\n"
        +"status TEXT,\n"
        +"output TEXT\n" // user output
        +");"
      };

    Statement statement = null;
    try {
      statement = this.connection.createStatement();
      for (int i = 0; i < sql.length; i++) {
        statement.execute(sql[i]);
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      this.close(statement);
    }
  }

  public int count(Tables table, Enum column, String value) {
    int num = 0;
    String sql = String.format("SELECT COUNT(*) FROM %s WHERE %s = ?", table, column);
    PreparedStatement ps = null;
    ResultSet results = null;
    try {
      ps = this.connection.prepareStatement(sql);
      ps.setString(1, value);

      results = ps.executeQuery(sql);
      results.last();
      num = results.getRow();
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      this.close(results);
      this.close(ps);
    }
    return num;
  }

  private void close(AutoCloseable resource) {
    if (resource != null) {
      try {
        resource.close();
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }




  // public void insert()

}
