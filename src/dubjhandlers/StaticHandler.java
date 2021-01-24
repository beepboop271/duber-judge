package dubjhandlers;

import webserver.Request;
import webserver.Response;
import webserver.RouteTarget;

public class StaticHandler implements RouteTarget {
  public Response accept(Request req) {
    return new Response(200);
  }
}
