package dubjhandlers;

import java.util.ArrayList;
import java.util.HashMap;

import dal.dao.RecordNotFoundException;
import entities.Entity;
import entities.Problem;
import entities.ProfileProblem;
import entities.Session;
import entities.User;
import services.ProblemService;
import services.PublicService;
import services.SessionService;
import services.UserService;
import templater.Templater;
import webserver.Request;
import webserver.Response;
import webserver.RouteTarget;

/**
 * The class that handles requests to anything regarding the
 * public practice problems.
 * <p>
 * This includes routes like
 * {@code /problems?sort=categoryName}, etc.
 * <p>
 * Created <b> 2020-01-25 </b>.
 *
 * @since 0.0.7
 * @version1.0.0
 * @author Joseph Wang
 */
public class PublicProblemHandler implements RouteTarget {
  /**
   * The public service this handler uses for db interaction.
   */
  private PublicService ps;
  /**
   * The session service this handler uses for db interaction.
   */
  private SessionService ss;
  /**
   * The user service this handler uses for db interaction.
   */
  private UserService us;

  /**
   * Constructs a new PublicProblemHandler.
   */
  public PublicProblemHandler() {
    this.ps = new PublicService();
    this.ss = new SessionService();
    this.us = new UserService();
  }

  /**
   * {@inheritDoc}
   * <p>
   * Accepts requests regarding public problems.
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
   *
   * @param req     The request to handle.
   * @param hasBody Whether the body should be included in the
   *                response or not.
   * @return a response to the retrieval request.
   */
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
   * Handles a POST request to a practice problem, currently
   * only for clarification submission.
   * <p>
   * If a post is submitted to another path, a Forbidden
   * request will be returned.
   *
   * @param req The request to handle.
   * @return a response to the POST request provided.
   */
  private Response handlePostRequest(Request req) {
    return Response.internalError();
  }

  // TODO: hasSession would be nice on db
  /**
   * Gets the current active session from the db, if present.
   * <p>
   * If not present, returns {@code null}.
   *
   * @param req The request to handle.
   * @return the current active session, or {@code null} if
   *         not present
   */
  private Session getActiveSession(Request req) {
    if (!req.hasCookie("token")) {
      return null;
    }

    try {
      return this.ss.getSession(req.getCookie("token")+"=");
    } catch (RecordNotFoundException e) {
      return null;
    }
  }

  /**
   * Retrieves all the practice problems present in the
   * database, and returns the templated html file.
   *
   * @param req     The request to handle.
   * @param hasBody Whether the response should have a body or
   *                not.
   * @return a response with the problems page.
   */
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
      problems.add(
        new ProfileProblem(
          "/problem/"+entity.getId(),
          prob.getCategory(),
          prob.getTitle(),
          prob.getPoints(),
          -1,
          prob.getNumSubmissions(),
          prob.getClearedSubmissions()
        )
      );
    }

    // load template params
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

    return Response.okNoCacheHtml(Templater.fillTemplate("problems", templateParams));
  }

  /**
   * Retrieves the leaderboard for a specific problem, and
   * returns the templated html file.
   *
   * @param req     The request to handle.
   * @param hasBody Whether the response should have a body or
   *                not.
   * @return a response with the leaderboard page for a
   *         problem.
   */
  private Response getProblemLeaderboard(Request req, boolean hasBody) {
    return Response.internalError();
  }

  //TODO: make an admin verification method
  /**
   * Retrieves the templated html file for a specific problem
   * and its details.
   *
   * @param req     The request to handle.
   * @param hasBody Whether the response should have a body or
   *                not.
   * @return a response with a specific problem page.
   */
  private Response getProblem(Request req, boolean hasBody) {
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

    String probIdStr = req.getParam("problemId");
    try {
      if (!probIdStr.matches("^\\d+$")) {
        return Response.notFoundHtml(req.getPath());
      }

      int probId = Integer.parseInt(probIdStr);
      Entity<Problem> prob = ps.getProblem(probId);

      HashMap<String, Object> templateParams = new HashMap<>();
      templateParams.put("leaderboardLink", "/leaderboard");
      templateParams.put("problemsLink", "/problems");
      templateParams.put("profileLink", "/profile");
      templateParams.put("username", username);
      templateParams.put("problem", prob.getContent());
      templateParams.put("submitLink", "/problem/" + probId + "/submit");
      templateParams.put("allSubmissionsLink", "/problem/" + probId + "/submissions");
      templateParams.put("homeLink", "/problems");

      String body = Templater.fillTemplate("viewProblem", templateParams);
      return Response.okHtml(body, hasBody);
    } catch (RecordNotFoundException e) {
      return Response.notFoundHtml(req.getPath());
    }
  }
}
