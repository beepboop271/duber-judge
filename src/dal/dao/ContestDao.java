package dal.dao;

import java.util.ArrayList;

import entities.Contest;
import entities.Entity;
import entities.entity_fields.ContestField;

/**
 * [description]
 * <p>
 * Created on 2021.01.10.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class ContestDao implements Dao<Contest>, Updatable<ContestField> {

  @Override
  public <V> void update(long id, ContestField field, V value)
    throws RecordNotFoundException {
    // TODO Auto-generated method stub

  }

  @Override
  public long add(Contest data) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Entity<Contest> get(long id) throws RecordNotFoundException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ArrayList<Entity<Contest>> getList(long[] ids) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void delete(long id) {
    // TODO Auto-generated method stub

  }

}
