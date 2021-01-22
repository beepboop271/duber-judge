package webserver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    this.workers =
      new ThreadPoolExecutor(
        20,
        200,
        60L,
        TimeUnit.SECONDS,
        new SynchronousQueue<Runnable>()
      );

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
    public static final int DEFAULT_TIMEOUT_MS = 15_000;
    public static final int MAX_REQ = 1000;

    private int timeoutMs = ConnectionHandler.DEFAULT_TIMEOUT_MS;
    private int maxReq = ConnectionHandler.MAX_REQ;
    private long connectionOpenTime;
    private int curReq;

    /** The client to handle. */
    private Socket client;
    /** The input stream from the client. */
    private BufferedReader input;
    /** The output stream for the client. */
    private PrintWriter output;
    /** Determines if the connection loop should run. */
    private boolean shouldRun = true;

    /**
     * Constructs a new ConnectionHandler to handle a specific
     * client.
     *
     * @param client The client to handle.
     */
    public ConnectionHandler(Socket client) {
      this.client = client;

      try {
        // Initialize proper input and output
        InputStreamReader clientInput =
          new InputStreamReader(this.client.getInputStream());
        this.input = new BufferedReader(clientInput);

        OutputStreamWriter utfWriter =
          new OutputStreamWriter(
            this.client.getOutputStream(),
            StandardCharsets.UTF_8
          );
        BufferedWriter bufferedUtfWriter = new BufferedWriter(utfWriter);
        this.output = new PrintWriter(bufferedUtfWriter);

        this.connectionOpenTime = System.currentTimeMillis();
        this.curReq = 0;

      } catch (SocketException e) {
        System.out.println("A stream failed to connect.");
        e.printStackTrace();
      } catch (IOException e) {
        System.out.println("An I/O error occured while handling the streams.");
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
      try {
        // Keep this connection open for as long as we need it in
        // case keep-alive header exists
        do {
          RequestBuilder rb = new RequestBuilder();

          boolean waitingForConnection = true;
          while (waitingForConnection) {
            if (this.input.ready()) {
              this.assembleRequest(rb);

              waitingForConnection = false;
            }
          }

          Response res;
          this.curReq++;
          try {
            Request req = rb.construct();

            if (!req.getProtocol().equals("HTTP/1.1")) {
              res = Response.unsupportedVersion();
            } else {
              this.initializeConnectionInformation(req);
              res = this.generateResponseFromRequest(req);
              this.attemptCacheStorage(req.getFullPath(), res);
            }
          } catch (HttpSyntaxException e) {
            res = Response.badRequest();
          }

          // Finally, output to user
          // Keep open if keep alive header exists
          this.output.print(res);
          this.output.flush();

          // TODO: do we even need to remove hop to hop headers
          this.shouldRun = shouldCloseConnection();
        } while (this.shouldRun);

        this.closeConnection();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    /**
     * Determines whether or not to close the current open
     * connection with the client, based on {@code Connection}
     * and {@code Keep-Alive} headers.
     *
     * @return true if the connection with the client should be
     *         terminated.
     */
    private boolean shouldCloseConnection() {
      if (this.curReq >= this.maxReq) {
        return true;
      }

      if (
        System.currentTimeMillis()-this.connectionOpenTime >= this.timeoutMs
      ) {
        return true;
      }

      return false;
    }

    /**
     * Initializes information about the connection with the
     * client.
     * <p>
     * This method will use {@code Connection} and
     * {@code Keep-Alive} headers to determine how to manage the
     * connection. If these headers are not present, the
     * connection will persist for the default amount of time.
     * <p>
     * Refer to
     * <a href="https://tools.ietf.org/html/rfc2616">RFC
     * 2616</a> for more details.
     *
     * @param res The response to use to parse information about
     *            the connection.
     * @throws HttpSyntaxException if the {@code Connection} and
     *                             {@code Keep-Alive} headers
     *                             are invalid.
     */
    private void initializeConnectionInformation(Request res)
      throws HttpSyntaxException {
      // Connection header does not exist
      if (!res.hasHeader("Connection")) {
        return;
      }

      // Connection header exists
      String connectionValue = res.getHeader("Connection");

      if (connectionValue.contains("close")) {
        return;

      } else if (connectionValue.contains("keep-alive")) {
        if (res.hasHeader("Keep-Alive")) {
          String pattern = "timeout=(\\d+), max=(\\d+)";
          Matcher m =
            Pattern.compile(pattern).matcher(res.getHeader("Keep-Alive"));

          if (m.find()) {
            this.timeoutMs = Integer.parseInt(m.group(0));
            this.maxReq = Integer.parseInt(m.group(1));
          } else {
            throw new HttpSyntaxException("Keep-Alive header malformed.");
          }
        }
      }
    }

    /**
     * Attempts to assemble a request from the input stream.
     * <p>
     * This method will return early if the
     * {@code RequestBuilder} timeout has been reached while
     * assembling the event
     * ({@link RequestBuilder#shouldTimeout()} returns
     * {@code true}).
     *
     * @param rb The {@code RequestBuilder} used to assemble the
     *           request.
     * @throws IOException if an I/O error occurs during reading
     *                     the connection.
     */
    private void assembleRequest(RequestBuilder rb) throws IOException {
      rb.resetTimeoutStart();

      while (!rb.hasCompletedRequest()) {
        while (this.input.ready()) {
          String reqString = input.readLine();
          rb.append(reqString+"\r\n");
        }

        if (rb.shouldTimeout()) {
          return;
        }
      }
    }

    /**
     * Attempts to close the connection with the client.
     */
    private void closeConnection() {
      try {
        this.input.close();
        this.output.close();
        this.client.close();
      } catch (IOException e) {
        System.out.println("Failed to close connection.");
        e.printStackTrace();
      }
    }

    // TODO: find the header that affects cache time
    /**
     * Attempts to store the provided request body in the web
     * cache, for later retrieval.
     *
     * @param fullPath The path to store the body under.
     * @param response The response to store.
     */
    private void attemptCacheStorage(String fullPath, Response response) {
      if (!cache.checkCache(fullPath)) {
        cache.putCache(response.getBody(), fullPath, 60);
      }
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
        return Response.okHtml(cache.getCachedObject(request.getFullPath()));
      }

      RouteTarget handler = WebServer.this.getRoute(request.getPath());

      if (handler != null) {
        // Return the accepted response
        return handler.accept(request);
      } else {
        // Generate the failed response
        return Response.notFoundHtml(request.getPath());
      }
    }
  }
}
