package dubjhandlers;

import webserver.RouteTarget;
import webserver.Response;
import webserver.Request;

public class LeaderboardHandler implements RouteTarget {
  public Response accept(Request req) {
    return new Response(200);
  }
}
