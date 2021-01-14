package dal.dao;

import entities.Entity;
import entities.User;

/**
 * [description]
 * <p>
 * Created on 2021.01.12.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class UserPoints {
  public Entity<User> user;
  public int points;

  public UserPoints(Entity<User> user, int points) {
    this.user = user;
    this.points = points;
  }
}
