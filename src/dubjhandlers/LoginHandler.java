package dubjhandlers;

import java.io.IOException;
import java.util.HashMap;

import dal.dao.RecordNotFoundException;
import entities.Session;
import entities.entity_fields.SessionField;
import services.SessionService;
import services.UserService;
import webserver.HttpSyntaxException;
import webserver.Request;
import webserver.Response;
import webserver.RouteTarget;
import webserver.WebServer;

/**
 * The class that handles requests to anything regarding
 * login and signing up.
 * <p>
 * This handler will redirect already signed up users to the
 * proper profile, and requests requiring authentication
 * should redirect to this handler.
 * <p>
 * Created <b> 2020-01-25 </b>.
 *
 * @since 0.0.7
 * @version 1.0.0
 * @author Joseph Wang, Shari Sun
 */
public class LoginHandler implements RouteTarget {
  /**
   * The user service this handler uses for db interaction.
   */
  private UserService us;
  /**
   * The session service this handler uses for db interaction.
   */
  private SessionService ss;

  /**
   * Constructs a new LoginHandler.
   */
  public LoginHandler() {
    this.us = new UserService();
    this.ss = new SessionService();
  }

  /**
   * {@inheritDoc}
   * <p>
   * Serves pages related to login and sign-ups.
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
    switch (req.getParam("path")) {
      case "login":
        return this.getLoginPage(req, hasBody);
      case "signup":
        return this.getSignupPage(req, hasBody);
      case "logout":
        return this.getLogoutPage(req, hasBody);
      default:
        return Response.notFoundHtml(req.getPath(), hasBody);
    }
  }

  /**
   * Handles a POST request to a practice problem, currently
   * only for logging in or signing up.
   * <p>
   * If a post is submitted to another path, a Forbidden
   * request will be returned.
   *
   * @param req The request to handle.
   * @return a response to the POST request provided.
   */
  private Response handlePostRequest(Request req) {
    switch (req.getParam("path")) {
      case "login":
        return this.handleLogin(req);
      case "signup":
        return this.handleSignup(req);
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
      // cookie implementation removes the last = , so we add it
      // in
      // TODO: when fixed cookie implementation, adjust this
      return this.ss.getSession(req.getCookie("token")+"=");
    } catch (RecordNotFoundException e) {
      return null;
    }
  }

  /**
   * Retrieves the login page for logging in.
   *
   * @param req     The request to handle.
   * @param hasBody Whether the response should have a body or
   *                not.
   * @return a response with the login page.
   */
  private Response getLoginPage(Request req, boolean hasBody) {
    Session curSession = this.getActiveSession(req);
    if (curSession != null && curSession.isLoggedIn()) {
      return Response.temporaryRedirect("/problems");
    }

    return this.loadPage("./static/login.html", hasBody);
  }

  private Response getLogoutPage(Request req, boolean hasBody) {
    Session curSession = this.getActiveSession(req);
    if (curSession != null && curSession.isLoggedIn()) {
      this.ss.updateSession(curSession.getToken(), SessionField.USER_ID, -1L);
    }

    return Response.temporaryRedirect("/problems");
  }

  /**
   * Retrieves the signup page for signing up.
   *
   * @param req     The request to handle.
   * @param hasBody Whether the response should have a body or
   *                not.
   * @return a response with the signup page.
   */
  private Response getSignupPage(Request req, boolean hasBody) {
    Session curSession = this.getActiveSession(req);
    if (curSession != null && curSession.isLoggedIn()) {
      return Response.temporaryRedirect("/problems");
    }

    return this.loadPage("./static/signup.html", hasBody);
  }

  private Response handleLogin(Request req) {
    HashMap<String, String> bodyParams = new HashMap<>();

    try {
      // get form results
      req.parseFormBody(bodyParams);
    } catch (HttpSyntaxException e) {
      return Response.badRequest();
    }

    if (
      !bodyParams.containsKey("username") || !bodyParams.containsKey("password")
    ) {
      return Response.badRequest();
    }

    // Attempt to login
    try {
      String username = bodyParams.get("username");
      String password = bodyParams.get("password");

      long uid = us.login(username, password);
      Session curSession = this.getActiveSession(req);

      Response r = Response.seeOther("/profile/"+username);
      if (curSession != null) {
        this.ss.updateSession(curSession.getToken(), SessionField.USER_ID, uid);
      } else {
        String token = ss.createSession(uid);
        r.addCookie("token", token, 60*60);
      }




      return r;
    } catch (IllegalArgumentException e) {
      // Thrown if login failed
      return this.loadPage("./static/invalid-login.html", true);
    }

  }

  /**
   * Handles a POST request to the signup page, and attempts
   * to create a new user with the signup details.
   * <p>
   * On a success, the user will be redirected to their
   * profile.
   *
   * @param req The request to handle.
   * @return a redirected response to the new profile, or a
   *         failed response.
   */
  private Response handleSignup(Request req) {
    HashMap<String, String> bodyParams = new HashMap<>();

    try {
      req.parseFormBody(bodyParams);
    } catch (HttpSyntaxException e) {
      return Response.badRequest();
    }

    if (
      !bodyParams.containsKey("username") || !bodyParams.containsKey("password")
    ) {
      return Response.badRequest();
    }

    // Attempt to sign up
    try {
      String username = bodyParams.get("username");
      String password = bodyParams.get("password");

      long uid = us.createUser(username, password);
      Session curSession = this.getActiveSession(req);

      Response r = Response.seeOther("/profile/"+username);
      if (curSession != null) {
        this.ss.updateSession(curSession.getToken(), SessionField.USER_ID, uid);
      } else {
        String token = ss.createSession(uid);
        r.addCookie("token", token, 60*60);
      }

      return r;
    } catch (IllegalArgumentException e) {
      // Thrown if sign up failed
      return this.loadPage("./static/invalid-signup.html", true);
    }
  }

  /**
   * Handles a POST request to the login page, and attempts to
   * verify the user's login details.
   * <p>
   * On a success, the user will be redirected to their
   * profile.
   *
   * @param req The request to handle.
   * @return a redirected response to the user's profile, or a
   *         failed response.
   */
  private Response loadPage(String path, boolean hasBody) {
    // login page is static, no need to call templater
    byte[] fileData;
    try {
      fileData = WebServer.loadFile(path);
    } catch (IOException e) {
      // internal error occurred, let client know before panicking
      return Response.internalError();
    }

    return Response.okByteHtml(fileData, hasBody);
  }
}
