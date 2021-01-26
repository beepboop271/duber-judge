package dubjhandlers;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;

import dal.dao.RecordNotFoundException;
import entities.Category;
import entities.Entity;
import entities.Problem;
import entities.ProfileProblem;
import entities.PublishingState;
import entities.Session;
import entities.User;
import services.AdminService;
import services.InsufficientPermissionException;
import services.PublicService;
import services.SessionService;
import services.UserService;
import templater.Templater;
import webserver.HttpSyntaxException;
import webserver.Request;
import webserver.Response;
import webserver.RouteTarget;

/**
 * The class that handles requests to anything regarding the
 * admin side of problems, like editing.
 * <p>
 * Created <b> 2020-01-25 </b>.
 *
 * @since 0.0.7
 * @version1.0.0
 * @author Joseph Wang
 */
public class AdminProblemHandler implements RouteTarget {
  /**
   * The session service that this handler uses for db
   * interaction.
   */
  private SessionService ss;
  /**
   * The admin service that this handler uses for db
   * interaction.
   */
  private AdminService as;
  /**
   * The public service that this handler uses for db
   * interaction.
   */
  private PublicService ps;
  /**
   * The user service that this handler uses for db
   * interaction.
   */
  private UserService us;

  /**
   * Constructs a new AdminProblemHandler.
   */
  public AdminProblemHandler() {
    this.ss = new SessionService();
    this.as = new AdminService();
    this.us = new UserService();
    this.ps = new PublicService();
  }

  /**
   * {@inheritDoc}
   * <p>
   * Serves pages related to the admin side of problems.
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
    switch (req.getEndResource()) {
      case "problems":
        return this.getAllProblems(req, hasBody);
      case "add":
        return this.getAddProblemPage(req, hasBody);
      default:
        return this.findProblem(req, hasBody);
    }
  }

  /**
   * Handles a POST request to a practice problem, currently
   * only for editing problem information, etc.
   * <p>
   * If a post is submitted to another path, a Forbidden
   * request will be returned.
   *
   * @param req The request to handle.
   * @return a response to the POST request provided.
   */
  private Response handlePostRequest(Request req) {
    switch (req.getEndResource()) {
      case "add":
        return this.addProblem(req);
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
   * Gets the admin user associated with the provided uid, or
   * {@code null} if the user is not an admin.
   *
   * @param uid The provided uid to check.
   * @return an admin user, or {@code null} if the provided
   *         uid is not an admin.
   * @throws RecordNotFoundException if the user could not be
   *                                 found in the first place.
   */
  private User getAdminUser(long uid) throws RecordNotFoundException {
    if (this.us.isAdmin(uid)) {
      return this.us.getUser(uid).getContent();
    }

    return null;
  }

  /**
   * Retrieves all the practice problems to be edited in a
   * templated html file.
   *
   * @param req     The request to handle.
   * @param hasBody Whether the response should have a body or
   *                not.
   * @return a response with the admin problems page.
   */
  private Response getAllProblems(Request req, boolean hasBody) {
    Session currentSession = this.getActiveSession(req);
    // verify and load admin information
    if (currentSession == null) {
      return Response.temporaryRedirect("/login");
    }
    long uid = currentSession.getUserId();
    String username = "Profile";
    try {
      User user = this.getAdminUser(currentSession.getUserId());
      if (user == null) {
        return Response.forbidden();
      }

      username = user.getUsername();
    } catch (RecordNotFoundException e) {
      return Response.internalError();
    }

    ArrayList<Entity<Problem>> practice =
      ps.getPracticeProblemsByCreator(uid, 0, 500);
    ArrayList<ProfileProblem> problems = new ArrayList<>();
    for (Entity<Problem> entity : practice) {
      Problem prob = entity.getContent();
      problems.add(
        new ProfileProblem(
          "/admin/problem/"+entity.getId()+"/testcases",
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
    templateParams.put("usersPageLink", "/admin/users");
    templateParams.put("problemsPageLink", "/admin/problems");
    templateParams.put("addProblemLink", "/admin/problems/add");

    return Response
      .okNoCacheHtml(Templater.fillTemplate("adminProblems", templateParams));
  }

  /**
   * Retrieves the add problem page.
   * <p>
   *
   * @param req     The request to handle.
   * @param hasBody Whether the response should have a body or
   *                not.
   * @return a response with the admin version of one problem.
   */
  private Response getAddProblemPage(Request req, boolean hasBody) {
    Session currentSession = this.getActiveSession(req);
    // verify and load admin information
    if (currentSession == null) {
      return Response.temporaryRedirect("/login");
    }
    long uid = currentSession.getUserId();
    String username = "Profile";
    try {
      User user = this.getAdminUser(uid);
      if (user == null) {
        return Response.forbidden();
      }
      username = user.getUsername();
    } catch (RecordNotFoundException e) {
      return Response.internalError();
    }

    // load adding page
    HashMap<String, Object> templateParams = new HashMap<>();
    templateParams.put("leaderboardLink", "/leaderboard");
    templateParams.put("problemsLink", "/problems");
    templateParams.put("profileLink", "/profile");
    templateParams.put("username", username);
    templateParams.put("postUrl", "/admin/problems/add");

    return Response
      .okHtml(Templater.fillTemplate("addProblemDetails", templateParams));
  }

  /**
   * Retrieves one specific problem to be edited.
   * <p>
   * A 404 will be returned if the problem cannot be found.
   *
   * @param req     The request to handle.
   * @param hasBody Whether the response should have a body or
   *                not.
   * @return a response with the admin version of one problem.
   */
  private Response findProblem(Request req, boolean hasBody) {
    return Response.internalError();
  }

  /**
   * Handles a POST request to add a new problem.
   * <p>
   * This method will redirect the client to the new admin
   * page of the problem.
   *
   * @param req     The request to handle.
   * @param hasBody Whether the response should have a body or
   *                not.
   * @return a redirect response to the new admin page of the
   *         problem.
   */
  private Response addProblem(Request req) {
    Session currentSession = this.getActiveSession(req);
    // verify and load admin information
    if (currentSession == null) {
      return Response.temporaryRedirect("/login");
    }
    long uid = currentSession.getUserId();

    try {
      // Ensure the user is an admin before adding
      if (!us.isAdmin(uid)) {
        return Response.forbidden();
      }
    } catch (RecordNotFoundException e) {
      return Response.internalError();
    }

    HashMap<String, String> bodyParams = new HashMap<>();

    try {
      // get form results
      req.parseFormBody(bodyParams);
    } catch (HttpSyntaxException e) {
      return Response.badRequest();
    }

    // If body doesn't match expected, reject
    if (
      !bodyParams.containsKey("title")
        || !bodyParams.containsKey("description")
        || !bodyParams.containsKey("category")
        || !bodyParams.containsKey("memoryLimit")
        || !bodyParams.containsKey("outputLimit")
        || !bodyParams.containsKey("timeLimit")
    ) {
      return Response.badRequest();
    }

    String memoryLimit = bodyParams.get("memoryLimit");
    String outputLimit = bodyParams.get("outputLimit");
    String timeLimit = bodyParams.get("timeLimit");

    // Make sure certain fields are indeed numbers
    if (
      !timeLimit.matches("^\\d+$")
        || !outputLimit.matches("^\\d+$")
        || !memoryLimit.matches("^\\d+$")
    ) {
      return Response.badRequest();
    }

    String editorial = "";
    if (bodyParams.containsKey("editorial")) {
      editorial = bodyParams.get("editorial");
    }

    // Create the actual testcase
    try {
      this.as.createPracticeProblem(
        uid,
        Category.valueOf(bodyParams.get("category")),
        new Timestamp(System.currentTimeMillis()),
        new Timestamp(System.currentTimeMillis()),
        bodyParams.get("title"),
        bodyParams.get("description"),
        Integer.parseInt(timeLimit),
        Integer.parseInt(memoryLimit),
        Integer.parseInt(outputLimit),
        0,
        1,
        editorial,
        PublishingState.PUBLISHED
      );
    } catch (InsufficientPermissionException e) {
      // Forbid them from posting if they cannot
      return Response.forbidden();
    }

    return Response.seeOther("/admin/problems");
  }
}
