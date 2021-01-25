package dubjhandlers;

import webserver.RouteTarget;
import webserver.Response;
import dal.dao.RecordNotFoundException;
import entities.Session;
import entities.User;
import services.AdminService;
import services.ProblemService;
import services.SessionService;
import services.UserService;
import webserver.Request;

public class AdminProblemHandler implements RouteTarget {
  private SessionService ss;
  private AdminService as;
  private ProblemService prs;
  private UserService us;

  public AdminProblemHandler() {
    this.ss = new SessionService();
    this.as = new AdminService();
    this.prs = new ProblemService();
    this.us = new UserService();
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
      case "problems":
        return this.getAllProblems(req, hasBody);
      default:
        return this.findProblem(req, hasBody);
    }
  }

  private Response handlePostRequest(Request req) {
    switch (req.getEndResource()) {
      case "problems":
        return this.addProblem(req);
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
      return this.ss.getSession(req.getCookie("token") + "=");
    } catch (RecordNotFoundException e) {
      return null;
    }
  }

  private User getAdminUser(long uid) throws RecordNotFoundException {
    if (this.us.isAdmin(uid)) {
      return this.us.getUser(uid).getContent();
    }

    return null;
  }

  private Response getAllProblems(Request req, boolean hasBody) {
    return Response.internalError();
  }

  private Response findProblem(Request req, boolean hasBody) {
    return Response.internalError();
  }

  private Response addProblem(Request req) {
    return Response.internalError();
  }
}
