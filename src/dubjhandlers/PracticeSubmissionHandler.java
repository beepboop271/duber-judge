package dubjhandlers;

import java.util.HashMap;

import dal.dao.RecordNotFoundException;
import entities.Entity;
import entities.Language;
import entities.Session;
import entities.User;
import services.InsufficientPermissionException;
import services.ProblemService;
import services.SessionService;
import services.UserService;
import templater.Templater;
import webserver.HttpSyntaxException;
import webserver.Request;
import webserver.Response;
import webserver.RouteTarget;


/**
 * The class that handles requests to anything related to
 * submission pages.
 * <p>
 * Paths:
 * <p>
 * {@code /problem/:problemId/submit}
 * {@code /problem/:problemId/submissions}
 * {@code /problem/:problemId/submissions/:submissionId}
 * {@code /problem/:problemId/submissions?userid=id}
 * <p>
 * Created <b> 2020-01-25 </b>.
 *
 * @since 0.0.7
 * @version1.0.0
 * @author Joseph Wang, Shari Sun
 */
public class PracticeSubmissionHandler implements RouteTarget {
  /**
   * The problem service this handler uses for db interaction.
   */
  private ProblemService prs;
  /**
   * The session service this handler uses for db interaction.
   */
  private SessionService ss;
  /** The user service this handler uses for db interaction. */
  private UserService us;

  /**
   * Constructs a new PracticeSubmissionHandler.
   */
  public PracticeSubmissionHandler() {
    this.prs = new ProblemService();
    this.ss = new SessionService();
    this.us = new UserService();
  }

  /**
   * {@inheritDoc}
   * <p>
   * Serves pages related to contest submissions
   *
   * @param req The request to handle.
   * @return a proper response to the request.
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
    switch (req.getParam("action")) {
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
    switch (req.getParam("action")) {
      case "submit":
        return this.handleNewSubmission(req);
      default:
        return Response.forbidden();
    }
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
   * Retrieves the login page for submitting to a problem.
   *
   * @param req     The request to handle.
   * @param hasBody Whether the response should have a body or
   *                not.
   * @return a response with the submit page.
   */
  private Response getSubmitPage(Request req, boolean hasBody) {
    Session currentSession = this.getActiveSession(req);
    String username = "Profile";
    Entity<User> user;

    if (currentSession == null || !currentSession.isLoggedIn()) {
      return Response.temporaryRedirect("/login");
    }

    try {
      user = this.us.getUser(currentSession.getUserId());
      username = user.getContent().getUsername();
    } catch (RecordNotFoundException e) {
      System.out.println("user not found");
    }

    String title = "";
    try {
      title = this.prs.getProblem(Long.parseLong(req.getParam("problemId")))
        .getContent().getTitle();
    } catch (RecordNotFoundException e) {
      System.out.println("problem doesn't exist");
    }

    // load template params
    HashMap<String, Object> templateParams = new HashMap<>();
    templateParams.put("leaderboardLink", "/leaderboard");
    templateParams.put("problemsLink", "/problems");
    templateParams.put("profileLink", "/profile");
    templateParams.put("username", username);

    templateParams.put("problemTitle", title);
    return Response.okHtml(Templater.fillTemplate("submitSolution", templateParams));
  }

  /**
   * Retrieves the submissions associated with a problem in a
   * templated html file.
   *
   * @param req     The request to handle.
   * @param hasBody Whether the response should have a body or
   *                not.
   * @return a response with the problem's submissions.
   */
  private Response getProblemSubmissions(Request req, boolean hasBody) {
    if (this.getActiveSession(req) == null) {
      return Response.temporaryRedirect("/login");
    }
    return Response.internalError();
  }

  /**
   * Retrieves a specific submission in a templated html file
   * with details about said submission.
   *
   * @param req     The request to handle.
   * @param hasBody Whether the response should have a body or
   *                not.
   * @return a response with a specific submission page.
   */
  private Response getSubmission(Request req, boolean hasBody) {
    if (this.getActiveSession(req) == null) {
      return Response.temporaryRedirect("/login");
    }
    return Response.internalError();
  }

  /**
   * Handles the POST request with a new practice problem
   * submission and the logic associated with submitting a
   * practice problem.
   * <p>
   * Redirects the client to the new submission created
   * afterwards.
   *
   * @param req The request to handle.
   * @return a redirect response with the new created
   *         submission.
   */
  private Response handleNewSubmission(Request req) {
    Session currentSession = this.getActiveSession(req);
    if (currentSession == null || !currentSession.isLoggedIn()) {
      return Response.temporaryRedirect("/login");
    }

    HashMap<String, String> form = new HashMap<>();
    try {
      req.parseFormBody(form);
      this.prs.submitSolution(
        currentSession.getUserId(),
        Long.parseLong(req.getParam("problemId")),
        form.get("code"),
        Language.valueOf(form.get("language"))
      );
    } catch (HttpSyntaxException e) {
      return Response.badRequest();
    } catch (InsufficientPermissionException e) {
      return Response.temporaryRedirect("/login");
    } catch (RecordNotFoundException e) {
      return Response.temporaryRedirect("/problems");
    }

    return Response.seeOther("/profile");
  }
}
