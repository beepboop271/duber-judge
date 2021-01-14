package dal.dao;

import java.util.ArrayList;

import entities.Entity;
import entities.Problem;
import entities.entity_fields.ProblemField;

/**
 * [description]
 * <p>
 * Created on 2021.01.10.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class ProblemDao implements Dao<Problem>, Updatable<ProblemField> {

  @Override
  public <V> void update(long id, ProblemField field, V value)
    throws RecordNotFoundException {
    // TODO Auto-generated method stub

  }

  @Override
  public long add(Problem data) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Entity<Problem> get(long id) throws RecordNotFoundException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ArrayList<Entity<Problem>> getList(long[] ids) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void delete(long id) {
    // TODO Auto-generated method stub

  }

}
