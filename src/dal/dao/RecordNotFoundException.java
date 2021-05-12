package dal.dao;

/**
 * A record cannot be found in the database.
 * <p>
 * Created on 2021.01.10.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public class RecordNotFoundException extends Exception {
  /**
   * The serial version UID.
   */
  private static final long serialVersionUID = 1L;

  public RecordNotFoundException() {
    super("Record not found.");
  }

  public RecordNotFoundException(String type) {
    super(type + " not found.");
  }
}
