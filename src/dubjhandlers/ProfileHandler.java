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

public class ProfileHandler implements RouteTarget {
  UserService us = new UserService();
  SessionService ss = new SessionService();
  PublicService ps = new PublicService();

  public ProfileHandler() {
    this.us = new UserService();
    this.ss = new SessionService();
    this.ps = new PublicService();
  }

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

  private Response handleRetrievalRequest(Request req, boolean hasBody) {
    switch (req.getEndResource()) {
      case "profile":
        return this.getProfileRedirect(req, hasBody);
      default:
        return this.loadProfile(req, hasBody);
    }
  }

  // TODO: hasSession would be nice on db
  private Session getActiveSession(Request req) {
    if (!req.hasCookie("token")) {
      return null;
    }

    try {
      return this.ss.getSession(req.getCookie("token"));
    } catch (RecordNotFoundException e) {
      return null;
    }
  }

  private Response getProfileRedirect(Request req, boolean hasBody) {
    // Redirect to login if no session
    Session curSession = this.getActiveSession(req);
    if (curSession == null) {
      return Response.temporaryRedirect("/login");
    }

    try {
      // Only get the username and redirect to correct page
      long uid = curSession.getUserId();
      Entity<User> curUser = us.getUser(uid);

      return Response.temporaryRedirect(curUser.getContent().getUsername());
    } catch (RecordNotFoundException e) {
      // TODO: some sort of user failsafe later?
      return Response.internalError();
    }
  }

  private Response loadProfile(Request req, boolean hasBody) {
    // Redirect to login if no session
    Session curSession = this.getActiveSession(req);
    if (curSession == null) {
      return Response.temporaryRedirect("/login");
    }

    long uid = curSession.getUserId();
    Entity<User> curUser;
    // TODO: necessary?
    try {
      curUser = us.getUser(uid);
    } catch (RecordNotFoundException e) {
      return Response.notFoundHtml("profile");
    }

    // Load information for template
    try {
      String username = curUser.getContent().getUsername();
      ArrayList<Entity<SubmissionResult>> results =
        us.getSubmissions(uid, 0, 500);
      ArrayList<ProfileProblem> problems = new ArrayList<>();
      // TODO: we need total amount of submissions from db
      int submissionsCount = results.size();
      // TODO: problems solved from db needed
      int problemsSolved = us.getProblems(uid, 0, 500).size();
      int currentPoints = us.getPoints(uid);

      // Get a list of all submissions for template
      // TODO: care about query strings
      for (Entity<SubmissionResult> entity : results) {
        SubmissionResult result = entity.getContent();
        Problem prob =
          ps.getProblem(result.getSubmission().getProblemId()).getContent();
        problems.add(
          new ProfileProblem(
            "/problem/"+prob.getTitle(), // TODO: this doesnt account for
                                         // contest, and this assumes id is
                                         // title
            prob.getCategory(),
            prob.getTitle(),
            prob.getPoints(),
            result.getScore(),
            prob.getNumSubmissions(),
            prob.getClearedSubmissions()
          )
        );
      }

      // Load template names
      HashMap<String, Object> templateParams = new HashMap<>();
      templateParams.put("leaderboardLink", "/leaderboard");
      templateParams.put("problemsLink", "/problems");
      templateParams.put("profileLink", "/profile/"+username);
      templateParams.put("username", username);
      templateParams.put("submissionsCount", submissionsCount);
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
        body = Templater.fillTemplate("adminProfile", templateParams);
      } else {
        body = Templater.fillTemplate("userProfile", templateParams);
      }

      return Response.okHtml(body, hasBody);
    } catch (RecordNotFoundException e) {
      // TODO: some sort of user failsafe later?
      return Response.internalError();
    }
  }
}
