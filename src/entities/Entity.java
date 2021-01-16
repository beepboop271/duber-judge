package entities;

/**
 * A class that represents an entity in the database.
 * <p>
 * Created on 2021.01.07.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class Entity<T> implements Identifiable {
  /** A unique identifier for the entity. */
  private long id;
  /** What the entity contains. */
  private T content;

  /**
   * Constructs a new Entity.
   * 
   * @param id      a unique identifier for the entity.
   * @param content What the entity contains.
   */
  public Entity(long id, T content) {
    this.id = id;
    this.content = content;
  }

  /**
   * Retrieves this entity's id.
   * 
   * @return this entity's id.
   */
  public long getId() {
    return this.id;
  }

  /**
   * Retrieves this entity's content.
   * 
   * @return this entity's content.
   */
  public T getContent() {
    return this.content;
  }
}
