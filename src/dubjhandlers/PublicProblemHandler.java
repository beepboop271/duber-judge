package dubjhandlers;

import java.util.ArrayList;
import java.util.HashMap;

import dal.dao.RecordNotFoundException;
import entities.Entity;
import entities.Problem;
import entities.ProfileProblem;
import entities.Session;
import entities.SubmissionResult;
import entities.User;
import services.ProblemService;
import services.PublicService;
import services.SessionService;
import services.UserService;
import templater.Templater;
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
  private UserService us;

  public PublicProblemHandler() {
    this.ps = new PublicService();
    this.prs = new ProblemService();
    this.ss = new SessionService();
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

  private Response getAllProblems(Request req, boolean hasBody) {
    Session currentSession = this.getActiveSession(req);
    String username = "Profile";
    User user;

    if (currentSession != null) {
      try {
        user = this.us.getUser(currentSession.getUserId()).getContent();
        username = user.getUsername();
      } catch (RecordNotFoundException e) {
        System.out.println("user not found");
      }
    }

    ArrayList<Entity<Problem>> practice = ps.getPracticeProblems(0, 500);
    ArrayList<ProfileProblem> problems = new ArrayList<>();
    for (Entity<Problem> entity : practice) {
      Problem prob = entity.getContent();
      problems.add(new ProfileProblem(
        "/problem/"+entity.getId(),
        prob.getCategory(),
        prob.getTitle(),
        prob.getPoints(),
        -1,
        prob.getNumSubmissions(),
        prob.getClearedSubmissions()
      ));
    }

    HashMap<String, Object> templateParams = new HashMap<>();
    templateParams.put("leaderboardLink", "/leaderboard");
    templateParams.put("problemsLink", "/problems");
    templateParams.put("profileLink", "/profile");
    templateParams.put("username", username);
    templateParams.put("problems", problems);
    templateParams.put("previousPageLink", "/problems");
    templateParams.put("page1Link", "/problems");
    templateParams.put("page2Link", "/problems");
    templateParams.put("page3Link", "/problems");
    templateParams.put("nextPageLink", "/problems");

    return Response.okHtml(Templater.fillTemplate("problems", templateParams));
  }

  private Response getProblemLeaderboard(Request req, boolean hasBody) {
    int probId = Integer.parseInt(req.getParam("problemId"));
    ArrayList<Entity<SubmissionResult>> leaderboard = ps.getProblemLeaderboard(probId, 0, 500);

    return Response.internalError();
  }

  private Response getProblem(Request req, boolean hasBody) {
    int probId = Integer.parseInt(req.getParam("problemId"));
    try {
      Problem prob = ps.getProblem(probId).getContent();

      return Response.internalError();
    } catch (RecordNotFoundException e) {
      return Response.notFoundHtml(req.getPath());
    }
  }
}
