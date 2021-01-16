package webserver;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.HashMap;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;

import dubjhandlers.StaticHandler;
import dubjhandlers.ProblemHandler;
import dubjhandlers.LeaderboardHandler;
import dubjhandlers.HomeHandler;

public class WebServer {
  /** The port that this WebServer is hosted on. **/
  private int port;
  /** Whether the server is currently running or not. */
  private boolean running = true;

  /** The {@code ServerSocket} associated with this web server. */
  private ServerSocket sock;
  /** The routes that this WebServer has. */
  private HashMap<String, RouteTarget> routes;
  /**
   * The thread pool, to execute connections without needing to allocate a thread
   * to each connection.
   */
  private ExecutorService workers;

  public static void main(String[] args) {
    WebServer server = new WebServer(5000);

    server.route("/", new HomeHandler());
    server.route("/static", new StaticHandler());
    server.route("/contest/*/problems?/?*", new ProblemHandler());
    server.route("/admin/*/problems/*", new ProblemHandler());
    server.route("/contest/*/leaderboard", new LeaderboardHandler());
    server.route("/admin/*/leaderboard", new LeaderboardHandler());

    server.run();
  }

  /**
   * Constructs a new WebServer.
   * <p>
   * To actually start running the web server after constructing it, call
   * {@link #run()}.
   * 
   * @param port the port to host this web server on.
   */
  public WebServer(int port) {
    this.workers = Executors.newCachedThreadPool();
    this.routes = new HashMap<>();
    this.port = port;
  }

  /**
   * Begins running this web server, allowing it to accept and respond to
   * connections.
   */
  public void run() {
    try {
      this.sock = new ServerSocket(port);
      Socket client;

      while (running) {
        client = this.sock.accept();
        this.workers.execute(new ConnectionHandler(client));
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Shuts down this web server.
   * <p>
   * Any existing task remaining in the task queue will be discarded. No new tasks
   * will be accepted, and closing of the socket will be attempted.
   */
  public void shutdown() {
    this.running = false;
    // dispose of all tasks, and shuts down all workers
    this.workers.shutdownNow();

    try {
      this.sock.close();
    } catch (IOException e) {
      System.out.println("Could not shut down server socket!");
    }
  }

  /**
   * Adds a new routing to a {@code RouteTarget}.
   * <p>
   * The characters {@code *}, {@code ?}, and {@code +} are all subsets of their
   * regex counterparts. {@code *} and {@code +} are specifically used as
   * wildcards matching anything 0-unlimited and 1-unlimited, respectively. the
   * hyphen {@code -} and dot {@code .} character are interpreted literally.
   * <p>
   * All route will be matched as is, from start to end (akin to placing
   * {@code ^$} symbols). To match every file under a path, use {@code /*} to
   * indicate that everything under that path should be matched using this route
   * handler.
   * <p>
   * Query strings are not included in the path and can be found in the request
   * object.
   * <p>
   * Caution must be placed with routing two strings that match the same path, as
   * non-deterministic routing resolution will occur. There will be no guarantees
   * as to which route target will handle the request.
   * 
   * @param route  the route associated with the {@code RouteTarget}.
   * @param target the RouteTarget to handle the request.
   * @see RouteTarget
   */
  public void route(String route, RouteTarget target) {
    String cleanedRoute = "^";
    for (int i = 0; i < route.length(); i++) {
      char charAt = route.charAt(i);

      switch (charAt) {
        // These characters would mess up the regex and should not appear
        case '^':
        case '[':
        case ']':
          break;
        // These characters simply need to be treated as literals
        case '$':
          cleanedRoute += "\\$";
          break;
        case '.':
          cleanedRoute += "\\.";
          break;
        case '-':
          cleanedRoute += "\\-";
          break;
        // the * and + chars need wildcard dots
        case '*':
          cleanedRoute += ".*";
          break;
        case '+':
          cleanedRoute += ".+";
          break;
        default:
          cleanedRoute += charAt;
          break;
      }
    }

    cleanedRoute += "$";

    this.routes.put(cleanedRoute, target);
  }

  /**
   * Gets the appropriate route target for the specified path.
   * <p>
   * The worst case speed to find a matching route target is linear time compared
   * to the amount of routes initialized.
   * 
   * @param path the path to find the route target for.
   * @return the proper route target to handle the request, or {@code null} if
   *         none exists.
   */
  private RouteTarget getRoute(String route) {
    for (String possibleRoute : this.routes.keySet()) {
      if (route.matches(possibleRoute)) {
        return this.routes.get(possibleRoute);
      }
    }

    return null;
  }

  /**
   * A connection handler designed to handle a connection from a specified client.
   * <p>
   * This class is designed to work alongside a webserver, and will manage all io
   * with the connected client (expected to be a browser of some sort). More
   * specifically, it will read in the client's HTTP request and return an HTTP
   * response to the client.
   * <p>
   * Created <b>2021-01-08</b>.
   * 
   * @since 0.0.1
   * @version 0.0.1
   * @author Joseph Wang
   */
  private class ConnectionHandler implements Runnable {
    /** The client to handle. */
    private Socket client;
    /** The input stream from the client. */
    private BufferedReader input;
    /** The output stream for the client. */
    private PrintWriter output;

    /**
     * Constructs a new ConnectionHandler to handle a specific client.
     * 
     * @param client the client to handle.
     */
    public ConnectionHandler(Socket client) {
      this.client = client;

      try {
        this.input = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
        this.output = new PrintWriter(this.client.getOutputStream());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Starts this ConnectionHandler and begins handling a specific client.
     */
    public void run() {
      String statusLine = "";
      String[] statusTokens;
      String method;
      String fullPath;
      String protocol;

      String headerLine = "";
      String[] headers;

      String body = "";

      try {
        while (statusLine.equals("")) {
          if (input.ready()) {
            statusLine = input.readLine();

            // We add them to one large header line so we can split it later and have the
            // string array be the correct size

            // Since readLine() strips the \n, we can safely use \n as a delimiter for split
            while (input.ready()) {
              headerLine += input.readLine() + "\n";
            }
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }

      // The body is simply the last string in the request that we can parse
      body = headerLine.substring(headerLine.lastIndexOf("\n"));
      // Everything else is a lot of headers
      headerLine = headerLine.substring(0, headerLine.lastIndexOf("\n"));
      headers = headerLine.split("\n");

      // This should be a 3 string array
      statusTokens = statusLine.split(" ");
      method = statusTokens[0];
      fullPath = statusTokens[1];
      protocol = statusTokens[2];

      Response response;
      RouteTarget handler = getRoute(fullPath);

      if (handler != null) {
        response = handler.accept(new Request(method, fullPath, headers, body));
      } else {
        String failedBody = "<html><head><TITLE>404 Not Found.</TITLE></head><body>404 Not Found.</body></html>";

        response = new Response(200, failedBody);
        response.addHeader("Content-Type", "text/html");
        response.addHeader("Content-Length", "82");
      }

      output.println(response);
      output.flush();

      try {
        input.close();
        output.close();
        client.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
