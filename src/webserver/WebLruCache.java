package webserver;

import java.util.HashMap;

/**
 * 
 * n implementationo of a Least Recently Used cache, used for quick updates and
 * retrieval of data that has already been formatted and retrieved from the
 * database.
 * <p>
 * This cache supports O(1) get, clear, and update for any cached objects.
 * <p>
 * This cache will run a low-priority background thread to clean up expired
 * objects from the cache.
 * <p>
 * Created <b> 2020-01-08 </b>.
 * 
 * @since 0.0.1
 * @version 0.0.1
 * @author Joseph Wang
 */
public class WebLruCache {
  /** The head of the cache, for the most recently used object. */
  private Node<TimedObject<String>> head;
  /** The tail of the cache, for the least recently used object. */
  private Node<TimedObject<String>> tail;
  /** The id-to-node lookup table to get O(1) get time for all cached objects. */
  private HashMap<String, Node<TimedObject<String>>> lookup;
  /** The total amount of capacity this cache has. */
  private int capacity;

  /**
   * Constructs a new WebLruCache, with a capacity of 100.
   */
  public WebLruCache() {
    this(100);
  }

  /**
   * Constructs a new WebLruCache, with an declared capacity.
   * 
   * @param capacity the capacity of this cache.
   */
  public WebLruCache(int capacity) {
    this.capacity = capacity;
    this.lookup = new HashMap<>(capacity);
  }

  /**
   * Puts an object in the cache.
   * <p>
   * The object will be stored under the specified id, for later retrieval. If
   * duplicate ids are provide, the old id and object are overwritten.
   * <p>
   * The cached object will only live for the specified amount of time, after
   * which it expires and is removed from the cache.
   * 
   * @param item          the item to cache.
   * @param id            the id associated with this item.
   * @param secondsToLive the amount of seconds this item has to live.
   */
  public void putCache(String item, String id, int secondsToLive) {

  }

  /**
   * Updates a cached item.
   * <p>
   * The object stored under the existing id will be swapped with the new item. If
   * the id does not exist, nothing happens.
   * <p>
   * This method is similar to calling {@link #putCache(String, String, int)} with
   * a replacement object, but using this method indicates an update. This cached
   * object will also retain the existing amount of seconds to live from the old
   * object.
   * 
   * @param id      the id to update.
   * @param newItem the new item to replace the old item.
   */
  public void updateCache(String id, String newItem) {

  }

  /**
   * Checks the cache for a specified id.
   * 
   * @param id the id to check the cache for.
   * @return true if the cache has the specified id.
   */
  public boolean checkCache(String id) {
    return this.lookup.containsKey(id);
  }

  /**
   * Clears the cache and removes all cached objects.
   */
  public void clearCache() {
    this.lookup.clear();
  }

  /**
   * Initializes the clean-up service, which will run on a low priority background
   * thread, routinely cleaning up the cache.
   */
  private void initializeCleanupThread() {

  }
}
