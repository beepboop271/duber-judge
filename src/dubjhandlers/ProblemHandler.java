package dubjhandlers;

import webserver.RouteTarget;
import webserver.Response;
import webserver.Request;
public class ProblemHandler implements RouteTarget {
  public Response accept(Request req) {
    String body = "<html><head><TITLE>YAY!</TITLE></head><body>This is the problem handler!</body></html>";
    Response newResponse = new Response(200, body);
    newResponse.addHeader("Content-Type", "text/html");
    newResponse.addHeader("Content-Length", "86");

    return newResponse;
  }
}