import java.io.IOException;
import java.nio.file.Paths;

import dubjhandlers.AdminHandler;
import dubjhandlers.ContestHandler;
import dubjhandlers.ContestProblemHandler;
import dubjhandlers.ContestSubmissionHandler;
import dubjhandlers.HomeHandler;
import dubjhandlers.LoginHandler;
import dubjhandlers.PracticeSubmissionHandler;
import dubjhandlers.ProfileEditHandler;
import dubjhandlers.ProfileHandler;
import dubjhandlers.PublicProblemHandler;
import dubjhandlers.StaticHandler;
import templater.Templater;
import templater.compiler.tokeniser.UnknownTokenException;
import webserver.WebServer;

public class Main {
  public static void main(String[] args) {
    WebServer server = new WebServer(5000);

    try {
      Templater.prepareTemplate("userProfile", Paths.get("static/userProfile"));
      Templater.prepareTemplate("adminUsers", Paths.get("static/adminUsers"));
      Templater.prepareTemplate("adminProfile", Paths.get("static/adminProfile"));
    } catch (IOException e) {
      System.out.println("An exception occurred while preparing templates.");
    } catch (UnknownTokenException e) {
      System.out.println("A token in a template was unparsable.");
    }

    HomeHandler home = new HomeHandler();
    LoginHandler login = new LoginHandler();
    ProfileHandler profile = new ProfileHandler();
    ProfileEditHandler profileEdit = new ProfileEditHandler();
    ContestHandler contest = new ContestHandler();
    PublicProblemHandler publicProblem = new PublicProblemHandler();
    ContestProblemHandler contestProblem = new ContestProblemHandler();
    PracticeSubmissionHandler practiceSubmission =
      new PracticeSubmissionHandler();
    ContestSubmissionHandler contestSubmission = new ContestSubmissionHandler();
    AdminHandler admin = new AdminHandler();
    StaticHandler staticHandler = new StaticHandler();

    server.route("/", home);
    server.route("/contests", home);
    server.route("/leaderboard", home);

    server.route("/login", login);
    server.route("/signup", login);

    server.route("/profile", profile);
    server.route("/profile/:username", profile);
    server.route("/profile/:username/(submissions|problems|contests)", profile);
    server.route("/profile/:username/edit", profileEdit);

    server.route("/contest/:contestId", contest);
    server.route("/contest/:contestId/problems", contest);
    server.route("/contest/:contestId/leaderboard", contest);

    server.route("/problems", publicProblem);
    server.route("/problems/:problemId", publicProblem);
    server.route("/problems/:problemId/leaderboard", publicProblem);

    server.route("/contest/:contestId/problem/:problemId", contestProblem);

    server.route("/problem/:problemId/submit", practiceSubmission);
    server.route("/problem/:problemId/submissions", practiceSubmission);
    server.route(
      "/problem/:problemId/submissions/:submissionId",
      practiceSubmission
    );

    server.route(
      "/contest/:contestId/problem/:problemId/submit",
      contestSubmission
    );
    server.route(
      "/contest/:contestId/problem/:problemId/submissions",
      contestSubmission
    );
    server.route(
      "/contest/:contestId/problem/:problemId/submissions/:submissionId",
      contestSubmission
    );

    server.route("/admin", admin);
    server.route("/admin/clarifications", admin);
    server.route("/admin/problems", admin);
    server.route("/admin/restrictions", admin);
    server.route("/admin/contests", admin);
    server.route("/admin/clarifications/:clarificationId", admin);
    server.route("/admin/problem/:problemId", admin);
    server.route("/admin/contest/:contestId", admin);
    server.route("/admin/contest/:contestId/problems", admin);
    server.route("/admin/contest/:contestId/problem/:problemId", admin);

    server.route("/static", staticHandler);
    server.route("/static/*", staticHandler);
    server.route("/vendored/*", staticHandler);
    server.route("/styles.css", staticHandler);
    server.route("/scripts.js", staticHandler);

    server.run();
  }
}
