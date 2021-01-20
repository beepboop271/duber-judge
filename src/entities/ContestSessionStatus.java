package entities;

/**
 * An enum containing the possible current contest statuses a user could have.
 * <p>
 * Created on 2021.01.07.
 *
 * @author Shari Sun
 * @version 1.0.0
 * @since 1.0.0
 */
public enum ContestSessionStatus {
  /** To indicate that the user is still writing the contest. */
  ONGOING,
  /** To indicate that the user is finished writing the contest. */
  OVER,
}
