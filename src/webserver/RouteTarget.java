package webserver;

/**
 * A class designed to represent a destination object from a
 * route to handle an HTTP request.
 * <p>
 * Objects and classes implementing this interface must
 * override the {@link #accept(Request)} method and define
 * logic to properly handle an HTTP request and return a
 * proper HTTP response to the user.
 * <p>
 * Created <b>2020-01-07</b>.
 *
 * @since 0.0.1
 * @version 0.0.1
 * @author Joseph Wang
 * @see Response
 * @see Request
 */
public interface RouteTarget {
  /**
   * Accepts and handles an HTTP request.
   * <p>
   * Serves a new page depending on the HTTP request and the
   * route.
   *
   * @param req The HTTP Request to handle.
   * @return an HTTP response.
   * @see Response
   * @see Request
   */
  public Response accept(Request req);
}
