package dubjhandlers;

import dal.dao.RecordNotFoundException;
import entities.Session;
import entities.User;
import services.AdminService;
import services.ProblemService;
import services.SessionService;
import services.UserService;
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
 * @version 0.0.7
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
   * The problem service that this handler uses for db
   * interaction.
   */
  private ProblemService prs;
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
    this.prs = new ProblemService();
    this.us = new UserService();
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
      case "problems":
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
    return Response.internalError();
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
    return Response.internalError();
  }
}
