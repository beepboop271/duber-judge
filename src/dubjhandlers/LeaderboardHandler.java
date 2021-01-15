package dubjhandlers;

import webserver.RouteTarget;
import webserver.Response;
import webserver.Request;

public class LeaderboardHandler implements RouteTarget {
  public Response accept(Request req) {
    String body = "<html><head><TITLE>YAY!</TITLE></head><body>This is the leaderboard handler!</body></html>";
    Response newResponse = new Response(200, body);
    newResponse.addHeader("Content-Type", "text/html");
    newResponse.addHeader("Content-Length", "90");

    return newResponse;
  }
}
