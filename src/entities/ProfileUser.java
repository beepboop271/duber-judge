package entities;

/**
 * A testcase entity used inside the templater, which stores
 * relevant information about a user.
 * <p>
 * Created <b>2021-01-25</b>.
 *
 * @since   1.0.0
 * @version 1.0.0
 * @author Shari Sun
 */
public class ProfileUser {
  private String link;
  private String username;
  private int points;
  private int getProblemsCount;

  public ProfileUser(String link, String username, int points, int getProblemsCount) {
    this.link = link;
    this.username = username;
    this.points = points;
    this.getProblemsCount = getProblemsCount;
  }

  public String getLink() {
    return this.link;
  }

  public String getUsername() {
    return this.username;
  }

  public int getPoints() {
    return this.points;
  }

  public int getProblemsCount() {
    return this.getProblemsCount;
  }



}
