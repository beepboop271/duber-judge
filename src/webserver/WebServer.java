package webserver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import webserver.webcache.WebLruCache;

/**
 * A server designed to route requests to the correct
 * handler and serve responses back to the connecting
 * client.
 * <p>
 * Routes to handlers can be set by calling
 * {@link #route(String, RouteTarget)}. Upon a received
 * request, the server will attempt to match the path to a
 * {@code RouteTarget} to handle. More details about routing
 * can be found in {@link #route(String, RouteTarget)}.
 * <p>
 * This server also serves connections using a
 * ThreadPoolExecutor with a min and max bound, to avoid
 * spawning an unreasonable amount of threads, but to also
 * have a minimum amount of threads to serve requests
 * without spawning new threads.
 * <p>
 * Created <b> 2020-12-28 </b>
 *
 * @since 0.0.4
 * @version1.0.0
 * @author Joseph Wang, Shari Sun
 */
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
  private HashMap<Pattern, RouteTarget> routes;
  /**
   * The param keys of each route.
   *
   * @see Request#setParam(String, String)
   * @see Request#getParam(String)
   */
  private HashMap<Pattern, ArrayList<String>> routeParamKeys;
  /**
   * The web cache to store cached pages. This cache will only
   * be used to store html files.
   */
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
    this.routeParamKeys = new HashMap<>();
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
   * Checks whether this is a valid character to be part of a
   * named parameter.
   *
   * @param character The character to check for.
   * @return Whether or not it's valid.
   */
  private boolean validParamChar(char character) {
    return ((character >= 'a' && character <= 'z')
      || (character >= 'A' && character <= 'Z')
      || (character >= '0' && character <= '9')
      || (character == '_'));
  }

  /**
   * Adds a new routing to a {@code RouteTarget}.
   * <p>
   * {@code *} and {@code +} are specifically used as
   * wildcards matching anything 0-unlimited and 1-unlimited,
   * respectively. The hyphen {@code -} and dot {@code .}
   * character are interpreted literally. All other symbols
   * will be their regex counterparts.
   * <p>
   * Wildcards and capture groups can be retreived using
   * {@code req.getParam("0")}, where 0 is the index but as a
   * string. Named parameters can be created like
   * {@code :myParamName} and is retrievable through
   * {@code req.getParam("myParamName")}. Additionally, if you
   * combine a named parameter with a capture group, you can
   * retrieve the value of the capture group given the
   * parameter name.
   * <p>
   * All route will be matched as is, from start to end (akin
   * to placing {@code ^$} symbols). To match every file under
   * a path, use {@code *} to indicate that everything under
   * that path should be matched using this route handler.
   * <p>
   * Query strings should not be included in the path as they
   * are individually parsed separately and can be found in
   * the request object.
   * <p>
   * If the same path is routed twice, the latter path will
   * override the previous route, and all parameters will
   * override as well. {@code /problems/:problemId} and
   * {@code /problems/:id} will route to the same path.
   * <p>
   * A brief example:
   *
   * <pre>
   * WebServer server = new WebServer(5000);
   * // This will only match the /static path
   * server.route("/static", new StaticHandler());
   *
   * // Wildcard match
   * // /problems {0: null}
   * // /problems/problem1 {0: problem1}
   * // /problems/problem2/123 {0: problem2/123}
   * server.route("/problems/*?", new ProblemHandler());
   *
   * // Optional
   * // /problems/id {0: id}
   * // /problems/ {0: null}
   * server.route("/problems/(id)?", new ProblemHander());
   *
   * // Named parameters
   * // /problems/1234abc {problemId: 1234abc}
   * server.route("/problems/:problemId", new ProblemHander());
   *
   * // Optional named parameters
   * // /problems/1234abc {problemId: 1234abc}
   * // /problems/ {problemId: null}
   * server.route("/problems/:problemId?", new ProblemHander());
   *
   * // Capture groups
   * // /problems/math {0: math}
   * // /problems/english does not match
   * server.route("/problems/(math)", new ProblemHander());
   *
   * // Named parameters combined with capture groups
   * // /problems/good {type: good}
   * // /problems/bad {type: bad}
   * // /problems/cool does not match
   * server.route("/problems/:type(good|bad)", new ProblemHander());
   * </pre>
   *
   * @param route  The route associated with the
   *               {@code RouteTarget}.
   * @param target The RouteTarget to handle the request.
   * @see RouteTarget
   * @see Request#getParam(String)
   * @throws PatternSyntaxException When the regex path is
   *                                invalid.
   */
  public void route(String route, RouteTarget target) {
    StringBuilder cleanedRoute = new StringBuilder('^');
    char charAt = '/';
    // a buffer to hold the named params
    StringBuilder paramName = new StringBuilder();
    // the incremental keys for the params
    int keyI = 0;
    // a list of param keys, including both named and
    // incremental/automatic
    ArrayList<String> paramKeys = new ArrayList<>();
    // indicating the following characters are parameter names
    boolean isParamName = false;
    // something like /:user(me|you) should match me|you and
    // stored in user
    boolean paramCaptureGroup = false;

    for (int i = 0; i < route.length(); i++) {
      charAt = route.charAt(i);

      // if this character is part of a user-defined named
      // parameter
      if (isParamName && this.validParamChar(charAt)) {
        paramName.append(charAt);
      } else {
        // if this character no longer belong to the param name,
        // push the param name to the end of the paramKeys list
        if (isParamName) {
          isParamName = false;
          paramKeys.add(paramName.toString());
          if (charAt == '(') {
            paramCaptureGroup = true;
          } else {
            cleanedRoute.append("([^/]+?)");
          }
        }

        switch (charAt) {
          // These characters are literals
          case '.':
            cleanedRoute.append("\\.");
            break;
          case '-':
            cleanedRoute.append("\\-");
            break;
          // the * and + are wildcards
          case '*':
            cleanedRoute.append("(.*)");
            paramKeys.add(String.valueOf(keyI++));
            break;
          case '+':
            cleanedRoute.append("(.+)");
            paramKeys.add(String.valueOf(keyI++));
            break;
          // assign keys to user defined capture groups
          case '(':
            cleanedRoute.append('(');
            if (paramCaptureGroup) {
              paramCaptureGroup = false;
            } else {
              paramKeys.add(String.valueOf(keyI++));
            }
            break;
          // named parameter
          // it will be treated as a literal unless there is a
          // character/number/underscore after it
          case ':':
            if (
              i+1 < route.length() && this.validParamChar(route.charAt(i+1))
            ) {
              paramName.setLength(0); // clears the old param name
              isParamName = true;
            } else {
              cleanedRoute.append(charAt);
            }
            break;
          default:
            cleanedRoute.append(charAt);
            break;
        }
      }

    }

    // if this character no longer belong to the param name,
    // push the param name to the end of the paramKeys list
    // chances are the param key may go all the way until the
    // end
    // of the route name so added another check just in case
    if (isParamName) {
      isParamName = false;
      paramKeys.add(paramName.toString());
      cleanedRoute.append("([^/]+?)");
    }

    // make sure that if the path didn't end with /, / will be
    // matched as well
    if (charAt != '/') {
      cleanedRoute.append("(?=/|$)");
    }
    Pattern routePattern = Pattern.compile(cleanedRoute.toString());

    this.routes.put(routePattern, target);
    this.routeParamKeys.put(routePattern, paramKeys);
  }

  /**
   * Gets the appropriate route target for the specified path.
   * <p>
   * The worst case speed to find a matching route target is
   * linear time compared to the amount of routes initialized.
   * <p>
   * It also adds the path parameters specified in
   * {@link #route(String, RouteTarget)} if any.
   *
   * @param request The original request.
   * @return the proper route target to handle the request, or
   *         {@code null} if none exists.
   */
  private RouteTarget getRoute(Request request) {
    String route = request.getPath();
    for (Pattern routePattern : this.routes.keySet()) {
      Matcher matcher = routePattern.matcher(route);
      if (matcher.matches()) {
        int i = 1;
        for (String paramKey : this.routeParamKeys.get(routePattern)) {
          request.setParam(paramKey, matcher.group(i++));
        }
        return this.routes.get(routePattern);
      }
    }

    return null;
  }

  /**
   * Loads a provided file and returns the file bytes.
   *
   * @param path The path to find the file.
   * @return a byte array with the file data.
   * @throws IOException           if an IO error occurs while
   *                               reading the file.
   * @throws FileNotFoundException If the file cannot be
   *                               found.
   */
  public static byte[] loadFile(String path) throws IOException,
    FileNotFoundException {
    File file = new File(path);
    if (file.exists()) {
      InputStream in = new FileInputStream(path);
      ByteArrayOutputStream bout = new ByteArrayOutputStream();
      DataOutputStream dout = new DataOutputStream(bout);
      byte[] buffer = new byte[4096];

      int data = in.read(buffer);
      while (data >= 0) {
        dout.write(buffer, 0, data);
        data = in.read(buffer);
      }

      dout.flush();
      dout.close();
      in.close();
      return bout.toByteArray();
    } else {
      throw new FileNotFoundException("File not found.");
    }
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
   * @version1.0.0
   * @author Joseph Wang
   */
  private class ConnectionHandler implements Runnable {
    /**
     * The default amount of time to wait before terminating a
     * connection.
     */
    public static final int DEFAULT_TIMEOUT_MS = 15_000;
    /**
     * The default amount of requests that can be sent on one
     * connection.
     */
    public static final int MAX_REQ = 100;

    /**
     * The amount of time to wait before terminating a
     * connection.
     */
    private int timeoutMs = ConnectionHandler.DEFAULT_TIMEOUT_MS;
    /**
     * The amount of requests that can be sent on one
     * connection.
     */
    private int maxReq = ConnectionHandler.MAX_REQ;
    /** When the connection was opened, in ms. */
    private long connectionOpenTime;
    /** The current amount of requests handled. */
    private int curReq;
    /** If the connection should be closed immediately. */
    private boolean closeImmediately = false;

    /** The client to handle. */
    private Socket client;
    /** The input stream from the client. */
    private BufferedInputStream input;
    /** The output stream for the client. */
    // private PrintWriter output;
    private BufferedOutputStream output;
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
        // We use a buffered input stream instead of a buffered
        // reader because the body is not required to have a
        // trailing CRLF, which would cause the
        // BufferedReader.nextLine() to forever read
        this.input = new BufferedInputStream(this.client.getInputStream());

        // Use a buffered output stream to properly send files
        this.output = new BufferedOutputStream(this.client.getOutputStream());

        this.connectionOpenTime = System.currentTimeMillis();
        this.curReq = 0;

      } catch (SocketException e) {
        System.out.println("A stream failed to connect.");
        e.printStackTrace();
      } catch (IOException e) {
        System.out.println("An I/O error occurred while handling the streams.");
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
          // Create a new request builder for each request
          RequestBuilder rb = new RequestBuilder();

          // Wait for initial request
          boolean waitingForConnection = true;
          while (waitingForConnection) {
            if (this.input.available() > 0) {
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
          byte[] outputResponse =
            res.toString().getBytes(StandardCharsets.UTF_8);
          this.output.write(outputResponse, 0, outputResponse.length);
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
     * <p>
     * If the headers do not exist, the connection persists for
     * the default amount of time or requests.
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

      // If neither of the above are true, just make sure we don't
      // need to close immediately
      return this.closeImmediately;
    }

    /**
     * Initializes information about the connection with the
     * client.
     * <p>
     * This method will use {@code Connection} and
     * {@code Keep-Alive} headers to determine how to manage the
     * connection. If these headers are not present, the
     * connection will persist for the default amount of time or
     * requests.
     * <p>
     * Refer to
     * <a href="https://tools.ietf.org/html/rfc2616">RFC
     * 2616</a> for more details.
     *
     * @param res The response to use to parse information about
     *            the connection.
     * @throws HttpSyntaxException if the {@code Keep-Alive}
     *                             header is invalid.
     */
    private void initializeConnectionInformation(Request res)
      throws HttpSyntaxException {
      // Connection header does not exist
      if (!res.hasHeader("Connection")) {
        return;
      }

      // Connection header exists
      String connectionValue = res.getHeader("Connection");

      // Return if we are to close immediately
      if (connectionValue.contains("close")) {
        this.closeImmediately = true;
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
      // If connection has neither close or keep-alive, use
      // default
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
        if (this.input.available() > 0) {
          byte[] buffArr = new byte[this.input.available()];
          this.input.read(buffArr, 0, this.input.available());

          rb.append(new String(buffArr, StandardCharsets.UTF_8));

          // Make sure to exit if we complete the request
          if (rb.hasCompletedRequest()) {
            return;
          }
        }

        // If this loops for long enough, return early
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

    /**
     * Attempts to store the provided response body in the web
     * cache, for later retrieval.
     * <p>
     * If the response {@code Cache-Control} header contains
     * {@code no-store} or {@code no-cache}, the body will not
     * be cached.
     *
     * @param fullPath The path to store the body under.
     * @param response The response to store.
     */
    private void attemptCacheStorage(String fullPath, Response response) {
      // Ensure the body exists and has content
      if (!response.hasHeader("Content-Type")) {
        return;
      }

      // TODO: improve later
      // Only store html files in the cache
      if (!response.getHeader("Content-Type").equals("text/html")) {
        return;
      }

      if (response.hasHeader("Cache-Control")) {
        // Do not cache if it is labelled as "do not cache"
        String cacheControl = response.getHeader("Cache-Control");
        if (
          cacheControl.contains("no-store") || cacheControl.contains("no-cache")
        ) {
          return;
        }
      }
      // TODO check if the request is a HEAD, and if so, dont
      // actually use the body
      if (!cache.checkCache(fullPath)) {
        cache.putCache(response.getBody(), fullPath, 60);
      }
    }

    /**
     * Generates a new response given an HTTP request.
     * <p>
     * This response can stem from the cache or a handler. If
     * neither can handle the request, a generic fail page will
     * be returned alongside a 404 response.
     *
     * @param request The HTTP request to handle.
     * @return an HTTP response to return to the user.
     */
    private Response generateResponseFromRequest(Request request) {
      // Get handler and initialize parameters
      RouteTarget handler = WebServer.this.getRoute(request);

      // The cache should only store html files because those are
      // the templated ones
      byte[] cachedBody = cache.getCachedObject(request.getFullPath());
      if (cachedBody != null) {
        // Retrieve the cached object
        if (request.getMethod().equals("HEAD")) {
          return Response.okByteHtml(cachedBody, false);
        } else if (request.getMethod().equals("GET")) {
          return Response.okByteHtml(cachedBody);
        }
      }

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
