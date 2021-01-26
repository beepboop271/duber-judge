package dubjhandlers;

import webserver.Request;
import webserver.Response;
import webserver.RouteTarget;

/**
 * The class that handles requests regarding submissions to
 * contest problems.
 * <p>
 * Currently returns a static page to indicate the route is
 * working.
 * <p>
 * Created <b> 2020-01-25 </b>.
 *
 * @since 0.0.7
 * @version 0.0.7
 * @author Joseph Wang
 */
public class ContestSubmissionHandler implements RouteTarget {
  /**
   * {@inheritDoc}
   * <p>
   * Accepts requests regarding submitting to a contest
   * problem. Currently, only serves a static file to indicate
   * that the route is working.
   *
   * @param req The request to handle.
   * @return the response to the request.
   */
  public Response accept(Request req) {
    String body =
      "<html><head><title>a</title></head><body>contest submission</body></html>";
    return Response.okHtml(body);
  }
}