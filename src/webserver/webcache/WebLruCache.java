package webserver.webcache;

import java.util.concurrent.ConcurrentHashMap;

/**
 * n implementationo of a Least Recently Used cache, used
 * for quick updates and retrieval of data that has already
 * been formatted and retrieved from the database.
 * <p>
 * This cache supports O(1) get, clear, and update for any
 * cached objects.
 * <p>
 * This cache will run a low-priority background thread to
 * clean up expired objects from the cache. The background
 * thread will sleep for 15 seconds after checking to
 * prevent starvation.
 * <p>
 * Created <b> 2020-01-08 </b>.
 *
 * @since 0.0.1
 * @version 0.0.2
 * @author Joseph Wang
 */
public class WebLruCache {
  /** The default max capacity for any WebLRUCache. */
  public static final int DEFAULT_MAX_CAPACITY = 100;
  /**
   * The head of the cache, for the most recently used object.
   */
  private TimedNode<String> head;
  /**
   * The tail of the cache, for the least recently used
   * object.
   */
  private TimedNode<String> tail;
  /**
   * The id-to-node lookup table to get O(1) get time for all
   * cached objects, concurrent for thread safety.
   */
  private ConcurrentHashMap<String, TimedNode<String>> lookup;
  /**
   * The node-to-id lookup table to get O(1) get time for
   * nodes to keys, concurrent for thread safety.
   */
  private ConcurrentHashMap<TimedNode<String>, String> reverseLookup;
  /** The total amount of capacity this cache has. */
  private int maxCapacity;
  /** If this web cache's cleanup thread should run. */
  private boolean runCleanup = true;
  /** A locking object for linked list synchronization. */
  private Object listLock = new Object();

  /**
   * Constructs a new WebLruCache, with a capacity of 100.
   */
  public WebLruCache() {
    this(WebLruCache.DEFAULT_MAX_CAPACITY);
  }

  /**
   * Constructs a new WebLruCache, with an declared capacity.
   *
   * @param maxCapacity The max capacity of this cache.
   * @throws IllegalArgumentException if the provided capacity
   *                                  is 0 or less.
   */
  public WebLruCache(int maxCapacity) {
    if (maxCapacity <= 0) {
      throw new IllegalArgumentException("Capacity cannot be 0 or less.");
    }

    this.maxCapacity = maxCapacity;
    this.lookup = new ConcurrentHashMap<>(maxCapacity);
    this.reverseLookup = new ConcurrentHashMap<>(maxCapacity);

    this.initializeCleanupThread();
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
  public void putCache(String item, String id, int secondsToLive) {
    TimedNode<String> newNode;

    synchronized (this.listLock) {
      if (this.lookup.size() >= this.maxCapacity) {
        // Remove the tail from the lookup index
        String key = reverseLookup.get(this.tail);
        this.lookup.remove(key);
        this.reverseLookup.remove(this.tail);

        // Remove other references
        if (this.tail.prev != null) {
          this.tail.prev.next = null;
        }

        this.tail = this.tail.prev;
      }

      // Create and insert the new node into the head
      newNode = new TimedNode<>(item, null, head, secondsToLive);
      this.head = newNode;
      if (this.lookup.size() == 0) {
        this.tail = newNode;
      }
    }

    this.lookup.put(id, newNode);
    this.reverseLookup.put(newNode, id);
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
  public void updateCache(String id, String newItem) {
    if (checkCache(id)) {
      synchronized (this.listLock) {
        TimedNode<String> node = this.lookup.get(id);
        // Push recently updated to top
        if (node.next != null) {
          node.next.prev = node.prev;
        }

        if (node.prev != null) {
          node.prev.next = node.next;
        }

        node.next = this.head;
        this.head = node;

        if (node == this.tail) {
          this.tail = node.prev;
        }

        node.prev = null;

        node.setData(newItem);
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
  public String getCachedObject(String id) {
    synchronized (this.listLock) {
      TimedNode<String> node = this.lookup.get(id);

      if (node != null) {
        // Push recently got to top
        if (node.next != null) {
          node.next.prev = node.prev;
        }

        if (node.prev != null) {
          node.prev.next = node.next;
        }

        node.next = this.head;
        this.head = node;

        if (node == this.tail) {
          this.tail = node.prev;
        }

        node.prev = null;

        return node.getData();
      } else {
        return null;
      }
    }
  }

  /**
   * Checks the cache for a specified id.
   *
   * @param id The id to check the cache for.
   * @return true if the cache has the specified id.
   */
  public boolean checkCache(String id) {
    return this.lookup.containsKey(id);
  }

  /**
   * Clears the cache and removes all cached objects.
   */
  public void clearCache() {
    synchronized (this.listLock) {
      this.lookup.clear();
      this.reverseLookup.clear();
      this.head = null;
      this.tail = null;
    }
  }

  /**
   * Initializes the clean-up service, which will run on a low
   * priority background thread, routinely cleaning up the
   * cache.
   */
  private void initializeCleanupThread() {
    Thread cleanupThread = new Thread(new CleanupRunnable());

    cleanupThread.setPriority(Thread.MIN_PRIORITY);
    cleanupThread.start();
  }

  /**
   * A runnable that handles cleanup of expired cached items
   * with a 15 second delay between each check.
   * <p>
   * Created <b> 2021-01-19 </b>.
   *
   * @since 0.0.2
   * @version 0.0.2
   * @author Joseph Wang
   */
  private final class CleanupRunnable implements Runnable {
    private static final int SLEEP_TIME_MS = 15_000;

    public void run() {
      while (runCleanup) {
        synchronized (WebLruCache.this.listLock) {
          for (String toCheck : WebLruCache.this.lookup.keySet()) {
            if (WebLruCache.this.lookup.get(toCheck).isExpired()) {
              // Remove expired cache objects
              TimedNode<String> toRemove = WebLruCache.this.lookup.get(toCheck);
              WebLruCache.this.lookup.remove(toCheck);
              WebLruCache.this.reverseLookup.remove(toRemove);

              if (toRemove.next != null) {
                toRemove.next.prev = toRemove.prev;
              }

              if (toRemove.prev != null) {
                toRemove.prev.next = toRemove.next;
              }

              if (toRemove == WebLruCache.this.head) {
                WebLruCache.this.head = toRemove.next;
              }

              if (toRemove == WebLruCache.this.tail) {
                WebLruCache.this.tail = toRemove.prev;
              }
            }
          }
        }

        // Since marking thread as MIN_PRIRORITY doesn't magically
        // make it pause, wait a delay
        try {
          Thread.sleep(CleanupRunnable.SLEEP_TIME_MS);
        } catch (InterruptedException e) {
          System.out.println("Cleanup thread interrupted while sleeping.");
        }
      }
    }
  }
}
