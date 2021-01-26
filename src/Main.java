import java.io.IOException;
import java.nio.file.Paths;
import java.util.Scanner;

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
import services.InvalidArguments;
import services.SessionCleaner;
import services.UserService;
import templater.Templater;
import templater.compiler.tokeniser.UnknownTokenException;
import webserver.WebServer;

public class Main {
  public static void main(String[] args) {
    Main.initialize();
    promptCreateAdmin();
    Runtime.getRuntime().addShutdownHook(new Thread(new ResourceCleaner()));
    System.out.println("Server started at 5000");
    Main.startWebServer(5000);
  }

  public static void initialize() {
    ChildProcesses.initialize();
  }

  /**
   * Prompts the user to see if they wish to create an admin
   * account.
   */
  public static void promptCreateAdmin() {
    Scanner input = new Scanner(System.in);
    System.out.println("\n\n");
    System.out.println("An admin account is required to add problems.");
    System.out.println("So if you do not already have one already, you should create one.");
    System.out.print("Do you wish to create a new admin account (y/n): ");
    String answer = input.nextLine();
    while (!answer.equals("y") && !answer.equals("n")) {
      System.out.println("Please enter a valid option and try again.");
      System.out.print("Do you wish to create a new admin account (y/n): ");
      answer = input.nextLine();
    }
    if (answer.equals("y")) {
      createAdmin(input);
    }

    input.close();
  }

  /**
   * Prompts for relevant info and creates an admin user.
   *
   * @param input The Scanner used for CLI input.
   */
  public static void createAdmin(Scanner input) {
    UserService us = new UserService();

    boolean prompt = true;
    while (prompt) {
      try {
        System.out.print("Username: ");
        String username = input.nextLine();
        System.out.print("Password: ");
        String password = input.nextLine();

        us.createAdmin(username, password);
        System.out.println("You may now log in with your admin account at localhost:5000/login");
        prompt = false;
      } catch (IllegalArgumentException e) {
        switch (InvalidArguments.valueOf(e.getMessage())) {
          case BAD_USERNAME:
            System.out.println(
              "Please make sure your username is 3-20 characters long"
              +"and contains letters, numbers, underscore, or dash only."
            );
            break;
          case INSECURE_PASSWORD:
            System.out.println(
              "Please make sure your password is 6-25 characters long"
              +"and contains at least one letter and number."
            );
            break;
          case USERNAME_TAKEN:
            System.out.println("Username taken, please try a different username.");
            break;
          default:
            break;
        }
      }
    }
  }

  /**
   * Starts the web server and add necessary routes.
   *
   * @param port The port at which to start the server at.
   */
  public static void startWebServer(int port) {
    WebServer server = new WebServer(port);
    SessionCleaner sessCleaner = new SessionCleaner();
    sessCleaner.start();

    try {
      Templater.prepareTemplate("viewProblem", Paths.get("static/view-problem"));
      Templater.prepareTemplate("leaderboard", Paths.get("static/leaderboard"));
      Templater.prepareTemplate("userProfile", Paths.get("static/userProfile"));
      Templater.prepareTemplate("submission", Paths.get("static/submission"));
      Templater.prepareTemplate("userProfileProblem", Paths.get("static/userProfileProblem"));
      Templater.prepareTemplate("adminUsers", Paths.get("static/adminUsers"));
      Templater.prepareTemplate("submitSolution", Paths.get("static/submit-solution"));
      Templater
        .prepareTemplate("viewProbSubmissions", Paths.get("static/view-problem-submissions"));
      Templater
        .prepareTemplate("adminProfile", Paths.get("static/adminProfile"));
      Templater
        .prepareTemplate("problems", Paths.get("static/viewAllProblems"));
      Templater
        .prepareTemplate("adminProblems", Paths.get("static/adminProblems"));
      Templater
        .prepareTemplate("addProblemDetails", Paths.get("static/add-problem-details"));
      Templater
        .prepareTemplate("addTestcases", Paths.get("static/add-testcases"));
      Templater.prepareTemplate("adminProfileProblem", Paths.get("static/adminProfileProblem"));
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

    server.route("/:path(login|signup|logout)", login);

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
    server.route("/problem/:problemId/:leaderboard(leaderboard)", publicProblem);

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
    server.route("/admin/users", admin);
    server.route("/admin/clarifications/:clarificationId", admin);
    server.route("/admin/problems", adminProblem);
    server.route("/admin/problems/:action(add)", adminProblem);
    server.route("/admin/problem/:action(:problemId)", adminProblem);

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
    server.route("/editor.js", staticHandler);

    server.run();
  }
}
