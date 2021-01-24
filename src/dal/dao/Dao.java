package dal.dao;

import java.util.ArrayList;

import entities.Entity;

/**
 * A data access objects who's sole job is to interact with database.
 * There are no business logic in this layer, but rather simply just
 * create/read/edit/delete.
 * <p>
 * Created on 2021.01.10.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public interface Dao<T> {
  /** Adds a new record to the data table. */
  public long add(T data);

  /** Gets a record from the table. */
  public Entity<T> get(long id) throws RecordNotFoundException;

  /** Gets a list of record from a list of IDs. */
  public ArrayList<Entity<T>> getList(long[] ids);

  /** Delete the record by its ID. */
  public void deleteById(long id);
}
