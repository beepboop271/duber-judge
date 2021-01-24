package entities;

/**
 * An interface for objects that have a specific, unique identifier.
 * <p>
 * Created on 2021.01.07.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public interface Identifiable {
  /**
   * Retrieves this object's id.
   *
   * @return this object's id.
   */
  public long getId();
}
