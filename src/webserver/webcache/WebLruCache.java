package webserver.webcache;

import java.util.HashMap;

/**
 * n implementationo of a Least Recently Used cache, used
 * for quick updates and retrieval of data that has already
 * been formatted and retrieved from the database.
 * <p>
 * This cache supports O(1) get, clear, and update for any
 * cached objects.
 * <p>
 * This cache will run a low-priority background thread to
 * clean up expired objects from the cache.
 * <p>
 * Created <b> 2020-01-08 </b>.
 *
 * @since 0.0.1
 * @version 0.0.1
 * @author Joseph Wang
 */
public class WebLruCache {
  /** The default max capacity for any WebLRUCache. */
  public static int DEFAULT_MAX_CAPACITY = 100;
  /**
   * The head of the cache, for the most recently used object.
   */
  private Node<TimedObject<String>> head;
  /**
   * The tail of the cache, for the least recently used
   * object.
   */
  private Node<TimedObject<String>> tail;
  /**
   * The id-to-node lookup table to get O(1) get time for all
   * cached objects.
   */
  private HashMap<String, Node<TimedObject<String>>> lookup;
  /** The total amount of capacity this cache has. */
  private int maxCapacity;
  /** If this web cache's cleanup thread should run. */
  private boolean runCleanup = true;

  /**
   * Constructs a new WebLruCache, with a capacity of 100.
   */
  public WebLruCache() {
    this(DEFAULT_MAX_CAPACITY);
  }

  /**
   * Constructs a new WebLruCache, with an declared capacity.
   *
   * @param maxCapacity The max capacity of this cache.
   */
  public WebLruCache(int maxCapacity) {
    this.maxCapacity = maxCapacity;
    this.lookup = new HashMap<>(maxCapacity);

    initializeCleanupThread();
  }

  /**
   * Puts an object in the cache.
   * <p>
   * The object will be stored under the specified id, for
   * later retrieval. If duplicate ids are provide, the old id
   * and object are overwritten.
   * <p>
   * The cached object will only live for the specified amount
   * of time, after which it expires and is removed from the
   * cache.
   *
   * @param item          The item to cache.
   * @param id            The id associated with this item.
   * @param secondsToLive The amount of seconds this item has
   *                      to live.
   */
  public synchronized void putCache(String item, String id, int secondsToLive) {
    synchronized (lookup) {
      if (this.lookup.size() >= this.maxCapacity) {
        // Remove the tail from the lookup index in O(n) worst time
        for (String key : this.lookup.keySet()) {
          if (this.lookup.get(key).equals(tail)) {
            this.lookup.remove(key);
            break;
          }
        }

        // Remove other references
        if (tail.prev != null) {
          tail.prev.next = null;
        }

        tail = tail.prev;
      }

      // Create and insert the new node into the proper areas
      Node<TimedObject<String>> newNode =
        new Node<TimedObject<String>>(
          new TimedObject<String>(item, secondsToLive),
          null,
          head
        );
      this.head = newNode;
      if (this.lookup.size() == 0) {
        this.tail = newNode;
      }

      this.lookup.put(id, newNode);
    }
  }

  /**
   * Updates a cached item.
   * <p>
   * The object stored under the existing id will be swapped
   * with the new item. If the id does not exist, nothing
   * happens.
   * <p>
   * This method is similar to calling
   * {@link #putCache(String, String, int)} with a replacement
   * object, but using this method indicates an update. This
   * cached object will also retain the existing amount of
   * seconds to live from the old object.
   *
   * @param id      The id to update.
   * @param newItem The new item to replace the old item.
   */
  public synchronized void updateCache(String id, String newItem) {
    synchronized (this.lookup) {
      if (checkCache(id)) {
        this.lookup.get(id).data.setObject(newItem);
      }
    }
  }

  /**
   * Attempts to retrieve a cached object using a specified
   * key.
   * <p>
   * If the cached object and key does not exist, {@code null}
   * will be returned.
   *
   * @param id The id of the cached object to fetch.
   * @return the cached object, or {@code null}.
   */
  public synchronized String getCachedObject(String id) {
    synchronized (this.lookup) {
      return this.lookup.get(id).data.getObject();
    }
  }

  /**
   * Checks the cache for a specified id.
   *
   * @param id The id to check the cache for.
   * @return true if the cache has the specified id.
   */
  public synchronized boolean checkCache(String id) {
    synchronized (this.lookup) {
      return this.lookup.containsKey(id);
    }
  }

  /**
   * Clears the cache and removes all cached objects.
   */
  public void clearCache() {
    this.lookup.clear();
  }

  /**
   * Initializes the clean-up service, which will run on a low
   * priority background thread, routinely cleaning up the
   * cache.
   */
  private void initializeCleanupThread() {
    Thread cleanupThread = new Thread(new Runnable() {
      public synchronized void run() {
        while (runCleanup) {
          synchronized (lookup) {
            for (String toCheck : lookup.keySet()) {
              if (lookup.get(toCheck).data.isExpired()) {
                // Remove expired cache objects
                Node<TimedObject<String>> toRemove = lookup.get(toCheck);
                lookup.remove(toCheck);

                if (toRemove.next != null) {
                  toRemove.next.prev = toRemove.prev;
                }

                if (toRemove.prev != null) {
                  toRemove.prev.next = toRemove.next;
                }

                if (toRemove == head) {
                  head = toRemove.next;
                }

                if (toRemove == tail) {
                  tail = toRemove.prev;
                }
              }
            }
          }
        }
      }
    });

    cleanupThread.setPriority(Thread.MIN_PRIORITY);
    cleanupThread.start();
  }
}
