package dubjhandlers;

import webserver.RouteTarget;
import webserver.Response;
import webserver.Request;

public class HomeHandler implements RouteTarget {
  public Response accept(Request req) {
    String body = "<html><head><TITLE>HOME!</TITLE></head><body>This is the home page!</body></html>";
    Response newResponse = new Response(200, body);
    newResponse.addHeader("Content-Type", "text/html");
    newResponse.addHeader("Content-Length", "81");

    return newResponse;
  }
}