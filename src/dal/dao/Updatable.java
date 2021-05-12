package dal.dao;

import entities.entity_fields.EntityField;

/**
 * Indicates an entity that is allowed to be updated.
 */
public interface Updatable<F extends EntityField> {

  /**
   * Updates a record given the column/field name and a new value.
   *
   * @param <V>     The type of the field.
   * @param id      The record ID.
   * @param field   The field to change.
   * @param value   The new value.
   * @throws RecordNotFoundException   If the record cannot be found.
   */
  public <V> void update(long id, F field, V value) throws RecordNotFoundException;
}
