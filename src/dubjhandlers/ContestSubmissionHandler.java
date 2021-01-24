package dubjhandlers;

import webserver.Request;
import webserver.Response;
import webserver.RouteTarget;

public class ContestSubmissionHandler implements RouteTarget {
  public Response accept(Request req) {
    String body =
      "<html><head><title>a</title></head><body>contestsubmission</body></html>";
    return Response.okHtml(body);
  }
}
