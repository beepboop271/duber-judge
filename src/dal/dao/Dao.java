package dal.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import dal.connection.ConnectDB;
import dal.connection.GlobalConnectionPool;
import entities.Entity;

/**
 * [description]
 * <p>
 * Created on 2021.01.10.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public interface Dao<T> {
  public long add(T data);

  public Entity<T> get(long id) throws RecordNotFoundException;

  public ArrayList<Entity<T>> getList(long[] ids);

  public void deleteById(long id);
}
