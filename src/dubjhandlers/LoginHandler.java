package dubjhandlers;

import webserver.Request;
import webserver.Response;
import webserver.RouteTarget;

public class LoginHandler implements RouteTarget {
  public Response accept(Request req) {
    String body =
      "<html><head><title>a</title></head><body>login</body></html>";
    return Response.okHtml(body);
  }
}
