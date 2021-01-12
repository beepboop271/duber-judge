package entities;

/**
 * [description]
 * <p>
 * Created on 2021.01.07.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class Entity<T> implements Identifiable {
  private long id;
  private T content;

  public Entity(long id, T content) {
    this.id = id;
    this.content = content;
  }

  public long getId() {
    return this.id;
  }

  public T getContent() {
    return this.content;
  }
}
