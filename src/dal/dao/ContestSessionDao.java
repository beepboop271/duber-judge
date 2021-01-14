package dal.dao;

import java.util.ArrayList;

import entities.ContestSession;
import entities.Entity;
import entities.entity_fields.ContestSessionField;

/**
 * [description]
 * <p>
 * Created on 2021.01.10.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class ContestSessionDao implements Dao<ContestSession>, Updatable<ContestSessionField> {

  @Override
  public <V> void update(long id, ContestSessionField field, V value)
    throws RecordNotFoundException {
    // TODO Auto-generated method stub

  }

  @Override
  public long add(ContestSession data) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Entity<ContestSession> get(long id) throws RecordNotFoundException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ArrayList<Entity<ContestSession>> getList(long[] ids) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void delete(long id) {
    // TODO Auto-generated method stub

  }

}
