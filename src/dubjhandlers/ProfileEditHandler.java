package dubjhandlers;

import webserver.Request;
import webserver.Response;
import webserver.RouteTarget;

/**
 * The class that handles requests to anything regarding
 * editing a profile.
 * <p>
 * Created <b> 2020-01-25 </b>.
 *
 * @since 0.0.7
 * @version 1.0.0
 * @author Joseph Wang
 */
public class ProfileEditHandler implements RouteTarget {
  /**
   * {@inheritDoc}
   * <p>
   * Accepts requests regarding profile editing. Currently,
   * only serves a static file to indicate that the route is
   * working.
   *
   * @param req The request to handle.
   * @return the response to the request.
   */
  public Response accept(Request req) {

    String body =
      "<html><head><title>a</title></head><body>profiledit</body></html>";
    return Response.okHtml(body);
  }
}
