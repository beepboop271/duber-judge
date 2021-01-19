package webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import webserver.webcache.WebLruCache;

public class WebServer {
  /** The port that this WebServer is hosted on. **/
  private int port;
  /** Whether the server is currently running or not. */
  private boolean running = true;

  /**
   * The {@code ServerSocket} associated with this web server.
   */
  private ServerSocket sock;
  /** The routes that this WebServer has. */
  private HashMap<String, RouteTarget> routes;
  /** The web cache to store cached pages. */
  private WebLruCache cache;

  /**
   * The thread pool, to execute connections without needing
   * to allocate a thread to each connection.
   */
  private ExecutorService workers;

  /**
   * Constructs a new WebServer that should run on a specified
   * port.
   * <p>
   * To actually start running the web server after
   * constructing it, call {@link #run()}.
   * <p>
   * The web cache will be made with the default web cache
   * capacity, defined by the constant
   * {@link WebLruCache#DEFAULT_MAX_CAPACITY}.
   *
   * @param port The port to host this web server on.
   */
  public WebServer(int port) {
    this(port, WebLruCache.DEFAULT_MAX_CAPACITY);
  }

  /**
   * Constructs a new WebServer with a specified cache
   * capacity that should run on a specified port.
   * <p>
   * To actually start running the web server after
   * constructing it, call {@link #run()}.
   *
   * @param port             The port to host this web server
   *                         on.
   * @param maxCacheCapacity The max capacity of this web
   *                         server's web cache
   */
  public WebServer(int port, int maxCacheCapacity) {
    this.workers = Executors.newCachedThreadPool();
    this.routes = new HashMap<>();
    this.port = port;

    this.cache = new WebLruCache(maxCacheCapacity);
  }

  /**
   * Begins running this web server, allowing it to accept and
   * respond to connections.
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
   * Any existing task remaining in the task queue will be
   * discarded. No new tasks will be accepted, and closing of
   * the socket will be attempted.
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
   * The characters {@code *}, {@code ?}, and {@code +} are
   * all subsets of their regex counterparts. {@code *} and
   * {@code +} are specifically used as wildcards matching
   * anything 0-unlimited and 1-unlimited, respectively. the
   * hyphen {@code -} and dot {@code .} character are
   * interpreted literally.
   * <p>
   * All route will be matched as is, from start to end (akin
   * to placing {@code ^$} symbols). To match every file under
   * a path, use {@code /*} to indicate that everything under
   * that path should be matched using this route handler.
   * <p>
   * Query strings should not be included in the path as they
   * are individually parsed separately and can be found in
   * the request object.
   * <p>
   * Caution must be placed with routing two strings that
   * match the same path, as non-deterministic routing
   * resolution will occur. There will be no guarantees as to
   * which route target will handle the request.
   * <p>
   * A brief example:
   * 
   * <pre>
   * WebServer server = new WebServer(5000);
   * // This will only match the /static path, nothing more,
   * // nothing less.
   * server.route("/static", new StaticHandler());
   * // This will match /problems, /problems/problem1,
   * // /problems/problem2, etc.
   * server.route("/problems/?*", new ProblemHandler());
   * </pre>
   *
   * @param route  The route associated with the
   *               {@code RouteTarget}.
   * @param target The RouteTarget to handle the request.
   * @see RouteTarget
   */
  public void route(String route, RouteTarget target) {
    String cleanedRoute = "^";
    for (int i = 0; i < route.length(); i++) {
      char charAt = route.charAt(i);

      switch (charAt) {
        // These characters would mess up the regex and should not
        // appear
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
   * The worst case speed to find a matching route target is
   * linear time compared to the amount of routes initialized.
   *
   * @param path The path to find the route target for.
   * @return the proper route target to handle the request, or
   *         {@code null} if none exists.
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
   * A connection handler designed to handle a connection from
   * a specified client.
   * <p>
   * This class is designed to work alongside a webserver, and
   * will manage all io with the connected client (expected to
   * be a browser of some sort). More specifically, it will
   * read in the client's HTTP request and return an HTTP
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
     * Constructs a new ConnectionHandler to handle a specific
     * client.
     *
     * @param client The client to handle.
     */
    public ConnectionHandler(Socket client) {
      this.client = client;

      try {
        this.input =
          new BufferedReader(
            new InputStreamReader(this.client.getInputStream())
          );
        this.output = new PrintWriter(this.client.getOutputStream());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Starts this ConnectionHandler and begins handling a
     * specific client.
     */
    public void run() {
      String statusLine = "";
      String headerLine = "";

      try {
        while (statusLine.equals("")) {
          if (input.ready()) {
            statusLine = input.readLine();

            // We add them to one large header line so we can split it
            // later and have the
            // string array be the correct size

            // Since readLine() strips the \n, we can safely use \n as a
            // delimiter for split
            while (input.ready()) {
              headerLine += input.readLine()+"\n";
            }
          }
        }
      } catch (IOException e) {
        e.printStackTrace();
      }

      Response response = processRecievedStrings(statusLine, headerLine);

      // Output to user and shut down
      output.println(response);
      output.flush();

      closeConnection();
    }

    /**
     * Attempts to close the connection with the client.
     */
    private void closeConnection() {
      try {
        input.close();
        output.close();
        client.close();
      } catch (IOException e) {
        System.out.println("Failed to close connection.");
        e.printStackTrace();
      }
    }

    /**
     * Processes any recieved strings that should be used to
     * form a request, and generates the response based on that
     * request, for output back to the client.
     * <p>
     * If invalid strings are provided, this method will return
     * a response with a failed status, depending on the invalid
     * string.
     *
     * @param statusLine The string with the status line of the
     *                   HTML request
     * @param othersLine The string with the headers + body,
     *                   separated by {@code \n}.
     * @return a Response generated from the recieved strings.
     */
    private Response processRecievedStrings(
      String statusLine,
      String othersLine
    ) {
      String[] statusTokens;
      String method;
      String fullPath;
      String protocol;

      // This should be a 3 string array, and if not we stop
      // processing it
      statusTokens = statusLine.split(" ");
      if (statusTokens.length != 3) {
        return this.generateFailedResponse(400, "400 Bad Request.");
      }

      method = statusTokens[0];
      fullPath = statusTokens[1];
      protocol = statusTokens[2];

      if (!protocol.equals("HTTP/1.1")) {
        return this.generateFailedResponse(505, "HTTP Version Not Supported.");
      }

      String[] headers;
      String body = "";
      // The body is simply the last string in the request that we
      // can parse
      // Normally an empty string
      body = othersLine.substring(othersLine.lastIndexOf("\n"));
      // Everything else is a lot of headers
      othersLine = othersLine.substring(0, othersLine.lastIndexOf("\n"));
      headers = othersLine.split("\n");

      // Generate response from request object from the parsed
      // HTTP request
      Request newRequest = new Request(method, fullPath, headers, body);
      Response response = this.generateResponseFromRequest(newRequest);

      // Re-insert into cache if we need to
      if (!cache.checkCache(fullPath)) {
        cache.putCache(response.getBody(), fullPath, 60);
      }

      return response;
    }

    /**
     * Generates a new response given an HTTP request.
     * <p>
     * This response can stem from the cache or a handler. If
     * neither can handle the request, a generic fail page will
     * be returned alonside a 404 response.
     *
     * @param request The HTTP request to handle.
     * @return an HTTP response to return to the user.
     */
    private Response generateResponseFromRequest(Request request) {
      if (cache.checkCache(request.getFullPath())) {
        // Retrieve the cached object
        return new Response(200, cache.getCachedObject(request.getFullPath()));
      }

      RouteTarget handler = getRoute(request.getPath());

      if (handler != null) {
        // Return the accepted response
        return handler.accept(request);
      } else {
        // Generate the failed response
        return this.generateFailedResponse(404, "404 Not Found.");
      }
    }

    /**
     * Generates a generic failed response with an HTML file
     * detailing the failure.
     *
     * @param status      The failed status to return to the
     *                    client.
     * @param description A small description for the user.
     * @return a failed Response.
     */
    private Response generateFailedResponse(int status, String description) {
      String failedTemplate =
        "<html><head><title>%s</title></head><body>%s</body></html>";
      String failedBody =
        String.format(failedTemplate, description, description);

      Response response = new Response(status, failedBody);
      response.addHeader("Content-Type", "text/html");
      response
        .addHeader("Content-Length", Integer.toString(failedBody.length()));

      return response;
    }
  }
}
