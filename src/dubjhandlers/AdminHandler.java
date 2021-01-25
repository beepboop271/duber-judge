package dubjhandlers;

import webserver.Request;
import webserver.Response;
import webserver.RouteTarget;

public class AdminHandler implements RouteTarget {
  /**
   * {@inheritDoc}
   * <p>
   * Accepts requests regarding general admin content.
   *
   * @param req The request to handle.
   * @return the response to the request.
   */
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

  /**
   * Handles a retrieve request, like a GET or HEAD, for a
   * resource.
   * <p>
   * This handler will attempt to retrieve the resource, or
   * return an HTTP error if unsuccessful. The error will
   * depend on the reason for failure.
   * <p>
   * For now, returns internal error.
   *
   * @param req     The request to handle.
   * @param hasBody Whether the body should be included in the
   *                response or not.
   * @return a response to the retrieval request.
   */
  private Response handleRetrievalRequest(Request req, boolean hasBody) {
    return Response.internalError();
  }

  /**
   * Handles a POST request to a practice problem, currently
   * only editing general admin information.
   * <p>
   * If a post is submitted to another path, a Forbidden
   * request will be returned.
   * <p>
   * For now, returns internal error.
   *
   * @param req The request to handle.
   * @return a response to the POST request provided.
   */
  private Response handlePostRequest(Request req) {
    return Response.internalError();
  }
}
