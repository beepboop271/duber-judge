package dubjhandlers;

import java.io.IOException;
import java.util.HashMap;

import dal.dao.RecordNotFoundException;
import entities.Session;
import services.SessionService;
import services.UserService;
import webserver.HttpSyntaxException;
import webserver.Request;
import webserver.Response;
import webserver.RouteTarget;
import webserver.WebServer;

public class LoginHandler implements RouteTarget {
  private UserService us;
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

  private Response handleRetrievalRequest(Request req, boolean hasBody) {
    switch (req.getEndResource()) {
      case "login":
        return this.getLoginPage(req, hasBody);
      case "signup":
        return this.getSignupPage(req, hasBody);
      default:
        return Response.notFoundHtml(req.getPath(), hasBody);
    }
  }

  private Response handlePostRequest(Request req) {
    switch (req.getEndResource()) {
      case "login":
        return this.handleLogin(req);
      case "signup":
        return this.handleSignup(req);
      default:
        return Response.forbidden();
    }
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

  private Response getLoginPage(Request req, boolean hasBody) {
    // TODO: get user from db so we can redirect them to profile
    if (this.getActiveSession(req) != null) {
      return Response.temporaryRedirect("/problems");
    }

    return this.loadPage("./static/login.html", hasBody);
  }

  private Response getSignupPage(Request req, boolean hasBody) {
    // TODO: get user from db so we can redirect them to profile
    if (this.getActiveSession(req) != null) {
      return Response.temporaryRedirect("/problems");
    }

    return this.loadPage("./static/signup.html", hasBody);
  }

  private Response handleLogin(Request req) {
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

    // Attempt to login
    try {
      String username = bodyParams.get("username");
      String password = bodyParams.get("password");

      long uid = us.login(username, password);
      String token = ss.createSession(uid);

      Response r = Response.seeOther("/profile/"+username);
      r.addCookie("token", token, 300);

      return r;
    } catch (IllegalArgumentException e) {
      // Thrown if login failed
      return this.loadPage("./static/login.html", true);
    }

  }

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
      String token = ss.createSession(uid);

      Response r = Response.seeOther("/profile/"+username);
      r.addCookie("token", token, 60*30);

      return r;
    } catch (IllegalArgumentException e) {
      // Thrown if sign up failed
      return this.loadPage("./static/signup.html", true);
    }
  }

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
