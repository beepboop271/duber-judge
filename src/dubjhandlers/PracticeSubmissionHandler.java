package dubjhandlers;

import webserver.Request;
import webserver.Response;
import webserver.RouteTarget;

public class PracticeSubmissionHandler implements RouteTarget {
  public Response accept(Request req) {
    String body =
      "<html><head><title>a</title></head><body>practicesubmission</body></html>";
    return Response.okHtml(body);
  }
}
