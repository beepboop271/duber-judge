package dubjhandlers;

import webserver.Request;
import webserver.Response;
import webserver.RouteTarget;

public class PracticeProblemSubmissionHandler implements RouteTarget {
  public Response accept(Request req) {
    return new Response(200);
  }
}
