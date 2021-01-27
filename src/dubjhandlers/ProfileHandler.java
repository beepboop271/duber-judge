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
import services.PublicService;
import services.SessionService;
import services.UserService;
import templater.Templater;
import webserver.Request;
import webserver.Response;
import webserver.RouteTarget;

/**
 * The class that handles requests to anything regarding a
 * user profile.
 * <p>
 * Created <b> 2020-01-25 </b>.
 *
 * @since 0.0.7
 * @version 1.0.0
 * @author Joseph Wang, Shari Sun
 */
public class ProfileHandler implements RouteTarget {
  UserService us = new UserService();
  SessionService ss = new SessionService();
  PublicService ps = new PublicService();

  /**
   * Constructs a new ProfileHandler.
   */
  public ProfileHandler() {
    this.us = new UserService();
    this.ss = new SessionService();
    this.ps = new PublicService();
  }

  /**
   * {@inheritDoc}
   * <p>
   * Accepts requests regarding profiles.
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
      default:
        return Response.methodNotAllowed("");
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
    if (req.getParam("username") == null) {
      return this.getProfileRedirect(req, hasBody);
    }
    return this.loadProfile(req, hasBody);
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
   * Retrieves and responds with a redirect link to the proper
   * user profile.
   * <p>
   * This redirect is used for redirecting {@code /profile} to
   * the correct profile.
   *
   * @param req     The request to handle.
   * @param hasBody Whether the response should have a body or
   *                not.
   * @return a response with the redirected link or a failed
   *         response.
   */
  private Response getProfileRedirect(Request req, boolean hasBody) {
    // Redirect to login if no session
    Session curSession = this.getActiveSession(req);
    if (curSession == null || !curSession.isLoggedIn()) {
      return Response.temporaryRedirect("/login");
    }

    try {
      // Only get the username and redirect to correct page
      long uid = curSession.getUserId();
      Entity<User> curUser = us.getUser(uid);

      return Response
        .temporaryRedirect("/profile/"+curUser.getContent().getUsername());
    } catch (RecordNotFoundException e) {
      // TODO: some sort of user failsafe later?
      return Response.internalError();
    }
  }

  /**
   * Retrieves and responds with a user profile.
   * <p>
   * The profile contains information about the user, like
   * submissions or points.
   *
   * @param req     The request to handle.
   * @param hasBody Whether the response should have a body or
   *                not.
   * @return a response with the user profile or a failed
   *         response.
   */
  private Response loadProfile(Request req, boolean hasBody) {
    // Redirect to login if no session
    Session curSession = this.getActiveSession(req);
    if (curSession == null || !curSession.isLoggedIn()) {
      return Response.temporaryRedirect("/login");
    }
    String curUser = "";
    String username = req.getParam("username");
    Entity<User> user = null;

    try {
      curUser =
        this.us.getUser(curSession.getUserId()).getContent().getUsername();
      user = this.us.getUser(username);
    } catch (RecordNotFoundException e) {
      return Response.notFoundHtml("profile");
    }

    // Load information for template
    long uid = user.getId();
    String data = req.getQuery("data");
    if (data == null) {
      data = "submissions";
    }
    try {
      ArrayList<Entity<SubmissionResult>> results = null;
      if (data.equals("submissions")) {
        results = us.getSubmissions(uid, 0, 500);
      } else {
        results = us.getProblems(uid, 0, 500);
      }

      ArrayList<ProfileProblem> problems = new ArrayList<>();

      // Get a list of all submissions for template
      // TODO: care about query strings
      for (Entity<SubmissionResult> entity : results) {
        SubmissionResult result = entity.getContent();
        Problem prob =
          ps.getProblem(result.getSubmission().getProblemId()).getContent();
        // TODO: doesn't account for contest problems, which i guess
        // is okay
        String link;
        if (data.equals("submissions")) {
          link = "/problem/"
            +result.getSubmission().getProblemId()
            +"/submissions/"
            +entity.getId();
        } else {
          link = "/problem/"+result.getSubmission().getProblemId();
        }
        problems.add(
          new ProfileProblem(
            link,
            prob.getCategory(),
            prob.getTitle(),
            prob.getPoints(),
            result.getScore(),
            prob.getNumSubmissions(),
            prob.getClearedSubmissions(),
            result.getSubmission().getLanguage(),
            result.getStatus(),
            result.getRunDurationMillis()/1000.0,
            result.getMemoryUsageBytes()/1024.0,
            this.us.getProblemSubmissions(uid, result.getSubmission().getProblemId(), 0, 500).size()
          )
        );
      }
      // TODO: problems solved from db needed
      int problemsSolved = us.getProblems(uid, 0, 500).size();
      int currentPoints = us.getPoints(uid);

      // Load template names
      HashMap<String, Object> templateParams = new HashMap<>();
      templateParams.put("leaderboardLink", "/leaderboard");
      templateParams.put("problemsLink", "/problems");
      templateParams.put("profileLink", "/profile/"+curUser);
      templateParams.put("username", curUser);
      templateParams
        .put("submissionsCount", this.us.getSubmissions(uid, 0, 500).size());
      templateParams.put("problemsSolved", problemsSolved);
      templateParams.put("currentPoints", currentPoints);
      templateParams
        .put("userSubmissionsLink", "/profile/"+username+"?data=submissions");
      templateParams
        .put("userProblemsLink", "/profile/"+username+"?data=problems");
      templateParams
        .put("userContestsLink", "/profile/"+username+"?data=contests");
      templateParams.put("problems", problems);

      String body;
      if (us.isAdmin(uid)) {
        if (data.equals("submissions")) {
          body = Templater.fillTemplate("adminProfile", templateParams);
        } else {
          body = Templater.fillTemplate("adminProfileProblem", templateParams);
        }
      } else {
        if (data.equals("submissions")) {
          body = Templater.fillTemplate("userProfile", templateParams);
        } else {
          body = Templater.fillTemplate("userProfileProblem", templateParams);
        }
      }

      return Response.okNoCacheHtml(body, hasBody);
    } catch (RecordNotFoundException e) {
      // TODO: some sort of user failsafe later?
      return Response.internalError();
    }
  }
}
