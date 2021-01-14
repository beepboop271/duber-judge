package dal.dao;

import java.util.ArrayList;

import entities.Entity;
import entities.Submission;

/**
 * [description]
 * <p>
 * Created on 2021.01.10.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class SubmissionDao implements Dao<Submission> {

  @Override
  public long add(Submission data) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public Entity<Submission> get(long id) throws RecordNotFoundException {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public ArrayList<Entity<Submission>> getList(long[] ids) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void delete(long id) {
    // TODO Auto-generated method stub

  }

}
