package dubjhandlers;

import webserver.Request;
import webserver.Response;
import webserver.RouteTarget;

public class AdminHandler implements RouteTarget {
  public Response accept(Request req) {
    switch (req.getMethod()) {
      case "GET":
        return this.handleRetrievalRequest(req, true);
      case "HEAD":
        return this.handleRetrievalRequest(req, false);
      case "POST":
        return this.handlePostRequest(req);
      default:
        return Response.methodNotAllowed("POST");
    }
  }

  private Response handleRetrievalRequest(Request req, boolean hasBody) {
    return Response.internalError();
  }

  private Response handlePostRequest(Request req) {
    return Response.internalError();
  }
}
