package dubjhandlers;

import webserver.Request;
import webserver.Response;
import webserver.RouteTarget;

public class ProfileEditHandler implements RouteTarget {
  public Response accept(Request req) {

    String body =
      "<html><head><title>a</title></head><body>profiledit</body></html>";
    return Response.okHtml(body);
  }
}
