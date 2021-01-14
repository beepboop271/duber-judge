package dal.dao;

import entities.entity_fields.EntityField;

public interface Updatable<F extends EntityField> {
  public <V> void update(long id, F field, V value) throws RecordNotFoundException;
}
