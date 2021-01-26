package dubjhandlers;

import java.util.ArrayList;
import java.util.HashMap;

import dal.dao.RecordNotFoundException;
import entities.Entity;
import entities.ProfileUser;
import entities.Session;
import entities.User;
import services.AdminService;
import services.SessionService;
import services.UserService;
import templater.Templater;
import webserver.Request;
import webserver.Response;
import webserver.RouteTarget;

/**
 * The class that handles requests to anything regarding the
 * admin side of problems, like editing.
 * <p>
 * Created <b> 2020-01-25 </b>.
 *
 * @since 1.0.0
 * @version 1.0.0
 * @author Joseph Wang, Shari Sun
 */
public class AdminHandler implements RouteTarget {

  /**
   * The session service that this handler uses for db
   * interaction.
   */
  private SessionService ss;
  /**
   * The user service that this handler uses for db
   * interaction.
   */
  private UserService us;
  /**
   * The admin service that this handler uses for db
   * interaction.
   */
  private AdminService as;

  public AdminHandler() {
    this.us = new UserService();
    this.as = new AdminService();
    this.ss = new SessionService();
  }
  /**
   * {@inheritDoc}
   * <p>
   * Accepts requests regarding general admin content.
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
   * <p>
   * For now, returns internal error.
   *
   * @param req     The request to handle.
   * @param hasBody Whether the body should be included in the
   *                response or not.
   * @return a response to the retrieval request.
   */
  private Response handleRetrievalRequest(Request req, boolean hasBody) {
    Session currentSession = this.getActiveSession(req);
    // verify and load admin information
    if (currentSession == null || !currentSession.isLoggedIn()) {
      return Response.temporaryRedirect("/login");
    }
    User user = null;
    try {
      user = this.getAdminUser(currentSession.getUserId());
      if (user == null) {
        return Response.forbidden();
      }
    } catch (RecordNotFoundException e) {
      return Response.internalError();
    }

    ArrayList<Entity<User>> allUsers = this.as.getUsers(0, 100);
    ArrayList<ProfileUser> users = new ArrayList<>();
    for (Entity<User> u : allUsers) {
      User userInfo = u.getContent();
      users.add(new ProfileUser(
        "/profile/"+userInfo.getUsername(),
        userInfo.getUsername(),
        this.us.getPoints(u.getId()),
        this.us.getProblems(u.getId(), 0, 500).size()
      ));
    }

    HashMap<String, Object> templateParams = new HashMap<>();
    templateParams.put("user", user);
    templateParams.put("users", users);

    return Response.okHtml(Templater.fillTemplate("adminUsers", templateParams));
  }


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
   * Handles a POST request to a practice problem, currently
   * only editing general admin information.
   * <p>
   * If a post is submitted to another path, a Forbidden
   * request will be returned.
   * <p>
   * For now, returns internal error.
   *
   * @param req The request to handle.
   * @return a response to the POST request provided.
   */
  private Response handlePostRequest(Request req) {
    return Response.internalError();
  }
}
