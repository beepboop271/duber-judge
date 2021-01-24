package dubjhandlers;

import webserver.Request;
import webserver.Response;
import webserver.RouteTarget;

public class HomeHandler implements RouteTarget {
  public Response accept(Request req) {
    String body =
      "<html><head><TITLE>HOME!</TITLE></head><body>This is the home page!</body></html>";
    Response newResponse = Response.okHtml(body);

    return newResponse;
  }
}
