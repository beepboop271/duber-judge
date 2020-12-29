package dubjhandlers;

import webserver.RouteTarget;
import webserver.Response;
import webserver.Request;

public class StaticHandler implements RouteTarget {
  public Response accept(Request req) {
    return new Response(200);
  }
}
