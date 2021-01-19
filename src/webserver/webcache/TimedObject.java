package webserver.webcache;

/**
 * A timed class that holds an object, as well as an associated expiry time.
 * <p>
 * After the current system time as retrieved by
 * {@code System.currentTimeMillis()} is past than the expiry time, this class
 * object will have expired and can be acted upon.
 * <p>
 * Created <b> 2021-01-08</b>.
 *
 * @since 0.0.1
 * @version 0.0.2
 * @author Joseph Wang
 * 
 */
public class TimedObject<T> {
  /** The time when this object expires. */
  private long expirationTime;
  /** The object represented by this TimedObject. */
  private T object;

  /**
   * Constructs a new TimedObject, with the default expiration time of 1 second.
   *
   * @param object The object represented by this timed object.
   */
  public TimedObject(T object) {
    this(object, 60);
  }

  /**
   * Constructs a new TimedObject, which expires at the specified expiration time.
   *
   * @param object         The object represented by this timed object.
   * @param expirationTime The time when this object expires.
   */
  public TimedObject(T object, long expirationTime) {
    this.object = object;
    this.expirationTime = expirationTime;
  }

  /**
   * Constructs a new TimedObject, which expires after the specified amount of
   * seconds to live.
   *
   * @param object        The object represented by this timed object.
   * @param secondsToLive The amount of seconds this object has to live.
   */
  public TimedObject(T object, int secondsToLive) {
    this.object = object;
    this.expirationTime = System.currentTimeMillis() + (secondsToLive * 1000);
  }

  /**
   * Checks if this object is already expired.
   *
   * @return true if this object is expired.
   */
  public boolean isExpired() {
    return this.getRemainingTime() <= 0;
  }

  /**
   * Gets this object's remaining time to live.
   * <p>
   * If this object is already expired, the time will be returned as a negative
   * long.
   *
   * @return this object's remaining time to live.
   */
  public long getRemainingTime() {
    return this.expirationTime - System.currentTimeMillis();
  }

  /**
   * Retrieves the object stored in this timed object.
   *
   * @return the object stored.
   */
  public T getObject() {
    return this.object;
  }

  /**
   * Sets this object to a new object without updating the
   * expiration time.
   *
   * @param update The new object.
   */
  public void setObject(T update) {
    this.object = update;
  }
}
