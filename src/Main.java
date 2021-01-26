import java.io.IOException;
import java.nio.file.Paths;

import dubjhandlers.AdminHandler;
import dubjhandlers.AdminProblemHandler;
import dubjhandlers.AdminTestcaseHandler;
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
import judge.ChildProcesses;
import entities.Category;
import entities.PublishingState;
import entities.entity_fields.ContestField;
import entities.entity_fields.ProblemField;
import services.AdminService;
import services.ProblemService;
import services.SessionCleaner;
import templater.Templater;
import templater.compiler.tokeniser.UnknownTokenException;
import webserver.WebServer;

public class Main {
  public static void main(String[] args) {
    Main.initialize();
    Runtime.getRuntime().addShutdownHook(new Thread(new ResourceCleaner()));
    Main.startWebServer(5000);
  }

  public static void initialize() {
    ChildProcesses.initialize();
  }

  public static void startWebServer(int port) {
    WebServer server = new WebServer(port);
    SessionCleaner sessCleaner = new SessionCleaner();
    sessCleaner.start();

    try {
      Templater
        .prepareTemplate("viewProblem", Paths.get("static/view-problem"));
      Templater.prepareTemplate("leaderboard", Paths.get("static/leaderboard"));
      Templater.prepareTemplate("userProfile", Paths.get("static/userProfile"));
      Templater.prepareTemplate("adminUsers", Paths.get("static/adminUsers"));
      Templater.prepareTemplate("submitSolution", Paths.get("static/submit-solution"));
      Templater
        .prepareTemplate("adminProfile", Paths.get("static/adminProfile"));
      Templater
        .prepareTemplate("problems", Paths.get("static/viewAllProblems"));
      Templater
        .prepareTemplate("adminProblems", Paths.get("static/adminProblems"));
      Templater
        .prepareTemplate("addTestcases", Paths.get("static/add-testcases"));
      Templater
        .prepareTemplate("addTestcaseDetails", Paths.get("static/add-testcase-details"));
    } catch (IOException e) {
      System.out.println("An exception occurred while preparing templates.");
    } catch (UnknownTokenException e) {
      System.out.println("A token in a template was unparsable.");
      e.printStackTrace();
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
    AdminProblemHandler adminProblem = new AdminProblemHandler();
    AdminTestcaseHandler adminTestcase = new AdminTestcaseHandler();
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

    // TODO route /problem to /problems
    server.route("/problems", publicProblem);
    server.route("/problem/:problemId", publicProblem);
    server.route("/problem/:problemId/leaderboard", publicProblem);

    server.route("/contest/:contestId/problem/:problemId", contestProblem);

    server.route("/problem/:problemId/:action(submit)", practiceSubmission);
    server.route("/problem/:problemId/:action(submissions)", practiceSubmission);
    server.route(
      "/problem/:problemId/submissions/:action(:submissionId)",
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
    server.route("/admin/restrictions", admin);
    server.route("/admin/contests", admin);
    server.route("/admin/clarifications/:clarificationId", admin);
    server.route("/admin/problems", adminProblem);
    server.route("/admin/problem/:problemId", adminProblem);

    server.route("/admin/problem/:problemId/testcases", adminTestcase);
    server
      .route("/admin/problem/:problemId/testcases/:testcaseId", adminTestcase);
    server.route("/admin/problem/:problemId/testcases/:batchId/add", adminTestcase);

    // server.route("/admin/contest/:contestId", admin);
    // server.route("/admin/contest/:contestId/problems",
    // admin);
    // server.route("/admin/contest/:contestId/problem/:problemId",
    // admin);

    server.route("/static", staticHandler);
    server.route("/static/*", staticHandler);
    server.route("/vendored/*", staticHandler);
    server.route("/styles.css", staticHandler);
    server.route("/scripts.js", staticHandler);
    server.route("/problem-viewing-script.js", staticHandler);

    server.run();
  }
}
