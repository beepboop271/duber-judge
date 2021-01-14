package dal.dao;

import java.util.ArrayList;

import entities.Clarification;
import entities.Entity;
import entities.entity_fields.ClarificationField;

/**
 * [description]
 * <p>
 * Created on 2021.01.10.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class ClarificationDao implements Dao<Clarification>, Updatable<ClarificationField> {

  @Override
  public <V> void update(long id, ClarificationField field, V value)
    throws RecordNotFoundException {
    // TODO Auto-generated method stub

  }

  @Override
  public long add(Clarification data) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Entity<Clarification> get(long id) throws RecordNotFoundException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ArrayList<Entity<Clarification>> getList(long[] ids) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void delete(long id) {
    // TODO Auto-generated method stub

  }

}
