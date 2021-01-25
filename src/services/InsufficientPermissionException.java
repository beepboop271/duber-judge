package services;

/**
 * The user lacks permission to perform certain tasks.
 * <p>
 * Created on 2021.01.16.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */

public class InsufficientPermissionException extends Exception {

  public InsufficientPermissionException() {
    super("Insufficient permission");
  }
}
