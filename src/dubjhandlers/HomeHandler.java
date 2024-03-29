package dubjhandlers;

import java.util.ArrayList;
import java.util.HashMap;

import dal.dao.RecordNotFoundException;
import entities.Contest;
import entities.Entity;
import entities.ProfileUser;
import entities.Session;
import entities.User;
import services.PublicService;
import services.SessionService;
import services.UserService;
import templater.Templater;
import webserver.Request;
import webserver.Response;
import webserver.RouteTarget;

/**
 * The class that handles requests to anything regarding
 * home pages.
 * <p>
 * These pages are generally unrelated to other handlers and
 * have paths that look like {@code url/page}.
 * <p>
 * Created <b> 2020-01-25 </b>.
 *
 * @since 0.0.7
 * @version 1.0.0
 * @author Joseph Wang, Shari Sun
 */
public class HomeHandler implements RouteTarget {
  /**
   * The public service this handler uses for db interaction.
   */
  private PublicService ps;
  private UserService us;
  private SessionService ss;

  /**
   * Constructs a new HomeHandler.
   */
  public HomeHandler() {
    this.ps = new PublicService();
    this.us = new UserService();
    this.ss = new SessionService();
  }

  /**
   * {@inheritDoc}
   * <p>
   * Accepts requests regarding home pages not related to
   * other pages.
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
        return Response.methodNotAllowed();
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
      case "":
        return Response.permanentRedirect("/problems");
      case "contests":
        return this.getContestsPage(req, hasBody);
      case "leaderboard":
        return this.getLeaderboardPage(req, hasBody);
      default:
        return Response.notFoundHtml(req.getPath(), hasBody);
    }
  }

  /**
   * Retrieves the contest page, with the list of contests.
   * <p>
   * Contest problems can be accessed through links shown on
   * this page. As contests do not exist yet, this method
   * redirects to a static html file.
   *
   * @param req     The request to handle.
   * @param hasBody Whether the response should have a body or
   *                not.
   * @return a response with the contests page.
   */
  private Response getContestsPage(Request req, boolean hasBody) {
    ArrayList<Entity<Contest>> contest = this.ps.getOngoingContests(0, 50);
    contest.addAll(this.ps.getUpcomingContests(0, 50));

    // call templater
    String body =
      "<html><head><title>a</title></head><body>contests</body></html>";
    return Response.okHtml(body, hasBody);
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
   * Retrieves the leaderboard page, with the leaderboard of
   * users for total practice problems.
   * <p>
   * Currently, this method redirects to an static page.
   *
   * @param req     The request to handle.
   * @param hasBody Whether the response should have a body or
   *                not.
   * @return a response with the contests page.
   */
  private Response getLeaderboardPage(Request req, boolean hasBody) {
    String username;
    Session currentSession = this.getActiveSession(req);
    // verify and load admin information
    if (currentSession == null) {
      username = "Sign In";
    } else {
      try {
        username = this.us.getUser(currentSession.getUserId()).getContent().getUsername();
      } catch (RecordNotFoundException e) {
        username = "Sign In";
      }
    }

    ArrayList<Entity<User>> allUsers = this.ps.getLeaderboard(0, 100);
    ArrayList<ProfileUser> users = new ArrayList<>();
    for (Entity<User> u : allUsers) {
      User userInfo = u.getContent();
      users.add(new ProfileUser(
        "/profile/"+userInfo.getUsername(),
        userInfo.getUsername(),
        this.us.getPoints(u.getId()),
        this.us.getProblems(u.getId(), 0, 500).size(),
        userInfo.getUserType()
      ));
    }

    HashMap<String, Object> templateParams = new HashMap<>();
    templateParams.put("username", username);
    templateParams.put("users", users);

    return Response.okNoCacheHtml(Templater.fillTemplate("leaderboard", templateParams));
  }
}
