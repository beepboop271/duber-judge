package dubjhandlers;

import webserver.Request;
import webserver.Response;
import webserver.RouteTarget;

public class PublicProblemHandler implements RouteTarget {
  public Response accept(Request req) {
    String body =
      "<html><head><title>a</title></head><body>publicproblem</body></html>";
    return Response.okHtml(body);
  }
}
