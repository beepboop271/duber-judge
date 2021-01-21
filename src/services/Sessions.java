package services;

import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import entities.Session;

/**
 * A static class where the global sessions are stored.
 * <p>
 * Created on 2021.01.19.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class Sessions {
  /** A map of {token: session}. */
  public static ConcurrentHashMap<String, Session> tokenToSessions =
    new ConcurrentHashMap<>();
  /** Sessions ordered by time. */
  public static TreeSet<Session> sessions =
    new TreeSet<>();
}
