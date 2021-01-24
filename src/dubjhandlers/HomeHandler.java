package dubjhandlers;

import webserver.Request;
import webserver.Response;
import webserver.RouteTarget;

public class HomeHandler implements RouteTarget {
  public Response accept(Request req) {
    String body =
      "<html><head><title>a</title></head><body>leadrboard</body></html>";
    return Response.okHtml(body);
  }
}
