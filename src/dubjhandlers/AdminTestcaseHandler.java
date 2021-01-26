package dubjhandlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import dal.dao.RecordNotFoundException;
import entities.Batch;
import entities.Entity;
import entities.Problem;
import entities.ProfileBatch;
import entities.ProfileTestcase;
import entities.Session;
import entities.Testcase;
import entities.User;
import services.AdminService;
import services.InsufficientPermissionException;
import services.ProblemService;
import services.PublicService;
import services.SessionService;
import services.UserService;
import templater.Templater;
import webserver.HttpSyntaxException;
import webserver.Request;
import webserver.Response;
import webserver.RouteTarget;

/**
 * The class that handles requests to anything regarding
 * problem testcases.
 * <p>
 * Created <b> 2020-01-25 </b>.
 *
 * @since 0.0.7
 * @version 0.0.7
 * @author Joseph Wang
 */
public class AdminTestcaseHandler implements RouteTarget {
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
   * The problem service that this handler uses for db
   * interaction.
   */
  private ProblemService prs;
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
   * Constructs a new AdminTestcaseHandler.
   */
  public AdminTestcaseHandler() {
    this.ss = new SessionService();
    this.as = new AdminService();
    this.prs = new ProblemService();
    this.us = new UserService();
    this.ps = new PublicService();
  }

  /**
   * {@inheritDoc}
   * <p>
   * Serves pages related to problem testcases.
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
      case "testcases":
        return this.getAllTestcases(req, hasBody);
      case "add":
        return this.getAddTestcasePage(req, hasBody);
      default:
        return this.getTestcase(req, hasBody);
    }
  }

  /**
   * Handles a POST request to a the testcases of a problem,
   * currently only for adding a testcase or batch, etc.
   * <p>
   * If a post is submitted to another path, a Forbidden
   * request will be returned.
   *
   * @param req The request to handle.
   * @return a response to the POST request provided.
   */
  private Response handlePostRequest(Request req) {
    switch (req.getEndResource()) {
      case "testcases":
        return this.addBatch(req);
      case "add":
        return this.addTestcase(req);
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
   * Retrieves all the testcases in a templated html file.
   *
   * @param req     The request to handle.
   * @param hasBody Whether the response should have a body or
   *                not.
   * @return a response with the admin testcase page.
   */
  private Response getAllTestcases(Request req, boolean hasBody) {
    Session currentSession = this.getActiveSession(req);
    // verify and load admin information
    if (currentSession == null) {
      return Response.temporaryRedirect("/login");
    }
    String username = "Profile";
    long uid = currentSession.getUserId();
    try {
      User user = this.getAdminUser(uid);
      if (user == null) {
        return Response.forbidden();
      }

      username = user.getUsername();
    } catch (RecordNotFoundException e) {
      return Response.internalError();
    }

    // Actually retrieve test cases
    String probIdStr = req.getParam("problemId");
    try {
      if (!probIdStr.matches("^\\d+$")) {
        return Response.notFoundHtml(req.getPath());
      }

      int probId = Integer.parseInt(probIdStr);
      Entity<Problem> prob = as.getNestedProblem(probId);
      List<Entity<Batch>> batches = prob.getContent().getBatches();
      ArrayList<ProfileBatch> allBatches = new ArrayList<>();

      if (batches != null) {
        for (Entity<Batch> entity : batches) {
          Batch batch = entity.getContent();
          List<Entity<Testcase>> testcases = entity.getContent().getTestcases();
          ArrayList<ProfileTestcase> profTestcases = new ArrayList<>();

          // make sure these fields are not null
          if (testcases != null) {
            for (Entity<Testcase> testcase : testcases) {
              profTestcases.add(
                new ProfileTestcase(
                  "/admin/"+probId+"/testcases/"+testcase.getId(),
                  testcase.getId(),
                  testcase.getContent()
                )
              );
            }

          }
          allBatches.add(
            new ProfileBatch(
              profTestcases,
              entity.getId(),
              batch.getSequence(),
              "/admin/problem/"+probId+"/testcases/"+entity.getId()+"add"
            )
          );
        }
      }

      // load template params
      HashMap<String, Object> templateParams = new HashMap<>();
      templateParams.put("homeLink", "/problems");
      templateParams.put("leaderboardLink", "/leaderboard");
      templateParams.put("problemsLink", "/problems");
      templateParams.put("profileLink", "/profile");
      templateParams.put("username", username);
      templateParams.put("problemTitle", prob.getContent().getTitle());
      templateParams.put("batches", allBatches);
      templateParams.put("batchPostUrl", "/admin/problem/"+probId+"/testcases");

      return Response
        .okNoCacheHtml(Templater.fillTemplate("addTestcases", templateParams));
    } catch (RecordNotFoundException e) {
      return Response.notFoundHtml(req.getPath());
    }
  }

  /**
   * Returns the page used to add testcases.
   *
   * @param req     The request to handle.
   * @param hasBody Whether the response should have a body or
   *                not.
   * @return a response with the add testcase page.
   */
  private Response getAddTestcasePage(Request req, boolean hasBody) {
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

    // parse and use the problem id and batch ids
    String probIdStr = req.getParam("problemId");
    String batchIdStr = req.getParam("batchId");
    if (!probIdStr.matches("^\\d+$") || !batchIdStr.matches("^\\d+$")) {
      return Response.notFoundHtml(req.getPath());
    }

    int probId = Integer.parseInt(probIdStr);
    HashMap<String, Object> templateParams = new HashMap<>();
    templateParams.put("leaderboardLink", "/leaderboard");
    templateParams.put("problemsLink", "/problems");
    templateParams.put("profileLink", "/profile");
    templateParams.put("username", username);
    templateParams
      .put("postUrl", "/admin/problem/"+probId+"/testcases/"+batchIdStr+"add");

    return Response.okNoCacheHtml(
      Templater.fillTemplate("addTestcaseDetails", templateParams)
    );
  }

  /**
   * Returns the page with a specific testcase's details.
   *
   * @param req     The request to handle.
   * @param hasBody Whether the response should have a body or
   *                not.
   * @return a response with a page with the testcase.
   */
  private Response getTestcase(Request req, boolean hasBody) {
    return Response.internalError();
  }

  /**
   * Handles a POST request to add a new testcase.
   * <p>
   * This method will redirect the client to the new admin
   * page of the problem.
   *
   * @param req The request to handle.
   * @return a redirect response to the testcase overview
   *         page.
   */
  private Response addTestcase(Request req) {
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
    if (!bodyParams.containsKey("input") || !bodyParams.containsKey("output")) {
      return Response.badRequest();
    }

    // Get an actual batch id and problem id
    String batchIdStr = req.getParam("batchId");
    String probIdStr = req.getParam("problemId");
    if (!probIdStr.matches("^\\d+$") || !batchIdStr.matches("^\\d+$")) {
      return Response.notFoundHtml(req.getPath());
    }
    long batchId = Long.parseLong(batchIdStr);

    // Create the actual testcase
    try {
      String input = bodyParams.get("username");
      String output = bodyParams.get("password");

      Batch batch = as.getBatch(uid, batchId).getContent();
      int sequenceNumber;
      if (batch.getTestcases() == null) {
        sequenceNumber = 1;
      } else {
        sequenceNumber = batch.getTestcases().size()+1;
      }
      as.createTestcase(uid, batchId, sequenceNumber, input, output);
    } catch (RecordNotFoundException e) {
      // Not found, return not found html
      return Response.notFoundHtml(req.getPath());
    } catch (InsufficientPermissionException e) {
      // Forbid them from posting if they cannot
      return Response.forbidden();
    }

    return Response.seeOther("/admin/problem/"+probIdStr+"/testcases");
  }

  /**
   * Handles a POST request to add a new batch.
   * <p>
   * This method will redirect the client to the new admin
   * page of the problem.
   *
   * @param req The request to handle.
   * @return a redirect response to the testcases overview
   *         page.
   */
  private Response addBatch(Request req) {
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
    if (!bodyParams.containsKey("points")) {
      return Response.badRequest();
    }

    // Get an actual batch id and problem id
    String probIdStr = req.getParam("problemId");
    if (!probIdStr.matches("^\\d+$")) {
      return Response.notFoundHtml(req.getPath());
    }
    long probId = Long.parseLong(probIdStr);

    // Create the actual testcase
    try {
      int points = Integer.parseInt(bodyParams.get("points"));
      Problem prob = this.prs.getProblem(probId).getContent();
      int sequenceNumber;
      if (prob.getBatches() == null) {
        sequenceNumber = 1;
      } else {
        sequenceNumber = prob.getBatches().size()+1;
      }

      this.as.createBatch(uid, probId, sequenceNumber, points);

    } catch (RecordNotFoundException e) {
      // Not found, return not found html
      return Response.notFoundHtml(req.getPath());
    } catch (InsufficientPermissionException e) {
      // Forbid them from posting if they cannot
      return Response.forbidden();
    }

    return Response.seeOther("/admin/problem/"+probIdStr+"/testcases");
  }
}
