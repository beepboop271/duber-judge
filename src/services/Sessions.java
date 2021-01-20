package services;

import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;

import entities.Session;

/**
 * [description]
 * <p>
 * Created on 2021.01.19.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class Sessions {
  public static ConcurrentHashMap<String, Session> tokenToSessions =
    new ConcurrentHashMap<>();
  public static TreeSet<Session> sessions =
    new TreeSet<>();
}
