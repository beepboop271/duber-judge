package dubjhandlers;

import webserver.InvalidHeaderException;
import webserver.Request;
import webserver.Response;
import webserver.RouteTarget;

public class HomeHandler implements RouteTarget {
  public Response accept(Request req) {
    String body =
      "<html><head><TITLE>HOME!</TITLE></head><body>This is the home page!</body></html>";
    Response newResponse = new Response(200, body);
    try {
      newResponse.addHeader("Content-Type", "text/html");
      newResponse.addHeader("Content-Length", "81");
    } catch (InvalidHeaderException e) {
      return Response.badRequestHtml(e.getMessage());
    }

    return newResponse;
  }
}
