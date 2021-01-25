package dubjhandlers;

import java.util.ArrayList;
import java.util.HashMap;

import dal.dao.RecordNotFoundException;
import entities.Entity;
import entities.Problem;
import entities.Session;
import services.ProblemService;
import services.PublicService;
import services.SessionService;
import webserver.Request;
import webserver.Response;
import webserver.RouteTarget;

/*
 * PublicProblemHandler: - /problems -
 * /problems?sort=categoryName - /problems?sort=creatorName
 * - /problems?sort=points - /problems?sort=numSubmissions -
 * /problem/:problemId/leaderboard - /problem/:problemId
 */

public class PublicProblemHandler implements RouteTarget {
  private PublicService ps;
  private ProblemService prs;
  private SessionService ss;

  public PublicProblemHandler() {
    this.ps = new PublicService();
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
      case "problems":
        return this.getAllProblems(req, hasBody);
      case "leaderboard":
        return this.getProblemLeaderboard(req, hasBody);
      default:
        return this.getProblem(req, hasBody);
    }
  }

  /**
   * Handles a POST request to a practice problem, only for
   * clarification submission.
   *
   * @param req The request to handle.
   * @return a response to the POST request provided.
   */
  private Response handlePostRequest(Request req) {
    String probName = req.getParam("problemId");

    return Response.internalError();
  }

  private Response getAllProblems(Request req, boolean hasBody) {
    ArrayList<Entity<Problem>> problems = ps.getPracticeProblems(0, 500);

    HashMap<String, Object> templateParams = new HashMap<>();
    return Response.internalError();
  }

  private Response getProblemLeaderboard(Request req, boolean hasBody) {
    String probName = req.getParam("problemId");

    return Response.internalError();
  }

  private Response getProblem(Request req, boolean hasBody) {
    String probName = req.getParam("problemId");

    return Response.internalError();
  }
}
