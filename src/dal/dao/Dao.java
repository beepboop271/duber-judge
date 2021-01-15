package dal.dao;

import java.util.ArrayList;

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

  public void delete(long id);
}
