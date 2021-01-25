package dubjhandlers;

import java.util.ArrayList;

import dal.dao.UserPoints;
import entities.Contest;
import entities.Entity;
import services.PublicService;

import webserver.Request;
import webserver.Response;
import webserver.RouteTarget;
public class HomeHandler implements RouteTarget {
  private PublicService ps;

  public HomeHandler() {
    this.ps = new PublicService();
  }

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

  private Response getContestsPage(Request req, boolean hasBody) {
    ArrayList<Entity<Contest>> contest = this.ps.getOngoingContests(0, 50);
    contest.addAll(this.ps.getUpcomingContests(0, 50));

    // call templater
    String body =
      "<html><head><title>a</title></head><body>contests</body></html>";
    return Response.okHtml(body, hasBody);
  }

  private Response getLeaderboardPage(Request req, boolean hasBody) {
    ArrayList<UserPoints> leaderboard = this.ps.getLeaderboard(0, 50);

    // call templater
    String body =
      "<html><head><title>a</title></head><body>leaderboard</body></html>";
    return Response.okHtml(body, hasBody);
  }
}
