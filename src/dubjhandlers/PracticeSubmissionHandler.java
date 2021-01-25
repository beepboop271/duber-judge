package dubjhandlers;

import dal.dao.RecordNotFoundException;
import entities.Session;
import services.ProblemService;
import services.SessionService;
import webserver.Request;
import webserver.Response;
import webserver.RouteTarget;

/**
 * - /problem/:problemId/submit -
 * /problem/:problemId/submissions -
 * /problem/:problemId/submissions/:submissionId -
 * /problem/:problemId/submissions?userid=id
 */
public class PracticeSubmissionHandler implements RouteTarget {
  private ProblemService prs;
  private SessionService ss;

  public PracticeSubmissionHandler() {
    this.prs = new ProblemService();
    this.ss = new SessionService();
  }

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
    switch (req.getEndResource()) {
      case "submit":
        return this.getSubmitPage(req, hasBody);
      case "submissions":
        return this.getProblemSubmissions(req, hasBody);
      default:
        return this.getSubmission(req, hasBody);
    }
  }

  /**
   * Handles a POST request to a practice problem, only used
   * in submit.
   *
   * @param req The request to handle.
   * @return a response to the POST request provided.
   */
  private Response handlePostRequest(Request req) {
    switch (req.getEndResource()) {
      case "submit":
        return this.handleNewSubmission(req);
      default:
        return Response.forbidden();
    }
  }

  // TODO: hasSession would be nice on db
  private Session getActiveSession(Request req) {
    if (!req.hasCookie("token")) {
      return null;
    }

    try {
      return this.ss.getSession(req.getCookie("token"));
    } catch (RecordNotFoundException e) {
      return null;
    }
  }

  private Response getSubmitPage(Request req, boolean hasBody) {
    if (this.getActiveSession(req) == null) {
      return Response.temporaryRedirect("/login");
    }
    return Response.internalError();
  }

  private Response getProblemSubmissions(Request req, boolean hasBody) {
    if (this.getActiveSession(req) == null) {
      return Response.temporaryRedirect("/login");
    }
    return Response.internalError();
  }

  private Response getSubmission(Request req, boolean hasBody) {
    if (this.getActiveSession(req) == null) {
      return Response.temporaryRedirect("/login");
    }
    return Response.internalError();
  }

  private Response handleNewSubmission(Request req) {
    if (this.getActiveSession(req) == null) {
      return Response.temporaryRedirect("/login");
    }

    return Response.internalError();
  }
}
