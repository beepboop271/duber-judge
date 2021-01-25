package webserver;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * This class represents an HTTP {@code Request} object.
 * <p>
 * The web server does not need to propagate this request to
 * other HTTP servers, and so does not store the exact
 * request status line or implement a method designed to
 * format this request into a proper HTTP request string.
 * <p>
 * This request will convert body strings into UTF-8 byte
 * arrays. For the sake of convenience, the majority of
 * the constructors will use Strings for body, as byte arrays are
 * less readable and should only be used to represent
 * objects that cannot be Strings (eg. files).
 * <p>
 * This request object stores both the full path (with query
 * strings), and a modified path that does not include query
 * strings. Query strings are parsed on initialization and
 * can be retrieved using {@link #getQuery(String)}.
 * <p>
 * Created <b> 2020-12-28 </b>
 *
 * @since 0.0.1
 * @version 0.0.1
 * @author Joseph Wang, Shari Sun
 * @see <a href=
 *      "https://developer.mozilla.org/en-US/docs/Web/HTTP/Messages#HTTP_Requests">More
 *      information about HTTP Requests (Mozilla)</a>
 */
public class Request extends HttpMessage {
  /** The related HTTP method, like GET or POST. */
  private String method;
  /**
   * The path for the request, minus query strings and percent
   * encoding.
   */
  private String path;
  /** The full path for the request. */
  private String fullPath;
  /** The resource to fetch, aka the last part of the path. */
  private String endResource;
  /** The query strings in the path. */
  private Map<String, String> queryStrings;
  /** The HTTP protocol for this request. */
  private String protocol;
  /** Stores the route path parameters. */
  private HashMap<String, String> params;

  /**
   * Constructs a new Request without any headers or a body.
   * <p>
   * The body will be initialized as an empty string.
   *
   * @param method   The HTTP method for the request.
   * @param fullPath The full path for the request.
   * @param protocol The HTTP protocol for this request.
   * @throws HttpSyntaxException if the path provided is
   *                             invalid.
   */
  public Request(String method, String fullPath, String protocol)
    throws HttpSyntaxException {
    super();

    this.method = method;
    this.fullPath = fullPath;
    this.protocol = protocol;
    this.queryStrings = new HashMap<>();
    this.params = new HashMap<>();

    this.initializePathAndQueries(fullPath);
    this.initializeCookies();
  }

  /**
   * Constructs a new Request with a body but no headers.
   *
   * @param method   The HTTP method for the request.
   * @param fullPath The full path for the request.
   * @param protocol The HTTP protocol for this request.
   * @param body     The body of the request.
   * @throws HttpSyntaxException if the path provided is
   *                             invalid.
   */
  public Request(String method, String fullPath, String protocol, String body)
    throws HttpSyntaxException {
    super(body);

    this.method = method;
    this.fullPath = fullPath;
    this.protocol = protocol;
    this.queryStrings = new HashMap<>();
    this.params = new HashMap<>();

    this.initializePathAndQueries(fullPath);
    this.initializeCookies();
  }

  /**
   * Constructs a new Request with a byte array body but no headers.
   *
   * @param method   The HTTP method for the request.
   * @param fullPath The full path for the request.
   * @param protocol The HTTP protocol for this request.
   * @param body     The byte array body of the request.
   * @throws HttpSyntaxException if the path provided is
   *                             invalid.
   */
  public Request(String method, String fullPath, String protocol, byte[] body)
    throws HttpSyntaxException {
    super(body);

    this.method = method;
    this.fullPath = fullPath;
    this.protocol = protocol;
    this.queryStrings = new HashMap<>();
    this.params = new HashMap<>();

    this.initializePathAndQueries(fullPath);
    this.initializeCookies();
  }

  /**
   * Constructs a new Request with specified headers but no
   * body.
   * <p>
   * The body will be initialized as an empty string.
   *
   * @param method   The HTTP method for the request.
   * @param fullPath The full path for the request.
   * @param protocol The HTTP protocol for this request.
   * @param headers  The headers for this request.
   * @throws HttpSyntaxException    if the path provided is
   *                                invalid.
   * @throws InvalidHeaderException if an improperly formatted
   *                                header or value is
   *                                provided in the headers
   *                                map.
   */
  public Request(
    String method,
    String fullPath,
    String protocol,
    Map<String, String> headers
  ) throws HttpSyntaxException,
    InvalidHeaderException {
    this(method, fullPath, protocol, headers, "");
  }

  /**
   * Constructs a new Request with specified string array of
   * headers but no body.
   * <p>
   * The body will be initialized as an empty string.
   * <p>
   * This constructor will accept a string array of headers,
   * separated by a colon.
   * <p>
   * For example:
   * {@code ["Connection: Keep-Alive", "Accept-Language: en-us"]}
   * will be parsed into
   * {@code Connection: "Keep-Alive", Accept-Language: "en-us"}.
   * <p>
   * An {@code InvalidHeaderException} will be thrown if an
   * improperly formatted header is provided.
   *
   * @param method   The HTTP method for the request.
   * @param fullPath The full path for the request.
   * @param protocol The HTTP protocol for this request.
   * @param headers  The headers for this request.
   * @throws InvalidHeaderException if an improperly formatted
   *                                header is provided.
   * @throws HttpSyntaxException    if the path provided is
   *                                invalid.
   */
  public Request(
    String method,
    String fullPath,
    String protocol,
    String[] headers
  ) throws InvalidHeaderException,
    HttpSyntaxException {
    this(method, fullPath, protocol, headers, "");
  }

  /**
   * Constructs a new Request with a specified map of headers
   * and a body.
   *
   * @param method   The HTTP method for the request.
   * @param fullPath The full path for the request.
   * @param protocol The HTTP protocol for this request.
   * @param headers  The headers for this request.
   * @param body     The body of the request.
   * @throws HttpSyntaxException    if the path provided is
   *                                invalid.
   * @throws InvalidHeaderException if an improperly formatted
   *                                header or value is
   *                                provided in the headers
   *                                map.
   */
  public Request(
    String method,
    String fullPath,
    String protocol,
    Map<String, String> headers,
    String body
  ) throws HttpSyntaxException,
    InvalidHeaderException {
    super(headers, body);

    this.method = method;
    this.fullPath = fullPath;
    this.protocol = protocol;
    this.queryStrings = new HashMap<>();
    this.params = new HashMap<>();

    this.initializePathAndQueries(fullPath);
    this.initializeCookies();
  }

  /**
   * Constructs a new Request with specified string array of
   * headers and a body.
   * <p>
   * This constructor will accept a string array of headers,
   * separated by a colon.
   * <p>
   * For example:
   * {@code ["Connection: Keep-Alive", "Accept-Language: en-us"]}
   * will be parsed into
   * {@code Connection: "Keep-Alive", Accept-Language: "en-us"}.
   * <p>
   * An {@code InvalidHeaderException} will be thrown if an
   * improperly formatted header is provided.
   *
   * @param method   The HTTP method for the request.
   * @param fullPath The full path for the request.
   * @param headers  The headers for this request.
   * @param body     The body of the request.
   * @throws InvalidHeaderException if an improperly formatted
   *                                header is provided.
   * @throws HttpSyntaxException    if the path provided is
   *                                invalid.
   */
  public Request(
    String method,
    String fullPath,
    String protocol,
    String[] headers,
    String body
  ) throws InvalidHeaderException,
    HttpSyntaxException {
    super(headers, body);

    this.method = method;
    this.fullPath = fullPath;
    this.protocol = protocol;
    this.queryStrings = new HashMap<>();
    this.params = new HashMap<>();

    this.initializePathAndQueries(fullPath);
    this.initializeCookies();
  }

  /**
   * Attempts to initialize the proper path and query strings.
   *
   * @param fullPath The full path provided in the request.
   * @throws HttpSyntaxException if the path could not be
   *                             parsed as a request status
   *                             path.
   */
  private void initializePathAndQueries(String fullPath)
    throws HttpSyntaxException {
    try {
      URI pathUri = new URI(fullPath);
      if (pathUri.getQuery() != null) {
        this.parseQueryStrings(pathUri.getQuery());
      }
      this.path = pathUri.getPath();

      int lastIndex = this.path.lastIndexOf("/");
      this.endResource = this.path.substring(lastIndex+1);
    } catch (URISyntaxException e) {
      throw new HttpSyntaxException("Provided path is invalid.", e);
    }
  }

  /**
   * Attempts to initialize the cookies from this request's
   * list of headers.
   * <p>
   * Ensure that this method is called after initializing
   * headers.
   *
   * @throws HttpSyntaxException if a cookie is malformed, as
   *                             cookies come from the header.
   */
  private void initializeCookies() throws HttpSyntaxException {
    if (!this.headers.containsKey("Cookie")) {
      return;
    }

    try {
      String cookieString = this.headers.get("Cookie");
      this.addCookies(cookieString);

    } catch (InvalidCookieException e) {
      throw new HttpSyntaxException("A cookie was invalid.");
    }
  }

  /**
   * Takes in a decoded string with all the query strings from
   * a path, parses through them, and adds them to this
   * Request's map of queries.
   *
   * @param queryString The decoded string with queries,
   *                    without the beginning {@code ?}
   */
  private void parseQueryStrings(String queryString) {
    // TODO: reimplement parsing to make encoded & and = work
    String[] queries = queryString.split("&");
    for (String query : queries) {
      String[] queryInfo = query.split("=");

      // Ignore improperly formatted queries
      if (queryInfo.length == 2) {
        this.queryStrings.put(queryInfo[0], queryInfo[1]);
      }
    }
  }

  /**
   * Retrieves this Request's HTTP method.
   *
   * @return a string with this Request's HTTP method.
   */
  public String getMethod() {
    return this.method;
  }

  /**
   * Retrieves this Request's method full path, including
   * query strings.
   *
   * @return a String with this Request's method path.
   */
  public String getFullPath() {
    return this.fullPath;
  }

  /**
   * Retrieves this Request's method shortened path, without
   * query strings and percent encoding.
   * <p>
   * Query strings can be accessed easily using
   * {@link #getQuery(String)}.
   *
   * @return a String with this Request's method path.
   */
  public String getPath() {
    return this.path;
  }

  /**
   * Retrieves a specific param's value, or {@code null} if
   * the param does not exist.
   * <p>
   * The keys of the param may come from regex capturing
   * groups, such as {@code *, (group)}, and are denoted with
   * incrementing keys like {@code "0", "1", "2"}. Results are
   * retrieved using {@code .getParam("0")}.
   * <p>
   * Another option is to specify param keys using
   * {@code :paramName} and the value may be retrieved using
   * {@code .getParam("paramName")}.
   *
   * @param key The key of the param.
   * @return The value of the param.
   */
  public String getParam(String key) {
    return this.params.get(key);
  }

  /**
   * Sets a specific param's value.
   * <p>
   * The keys of the param may come from regex capturing
   * groups, such as {@code *, (group)}, and are denoted with
   * incrementing keys like {@code "0", "1", "2"}. Params will
   * be set using {@code .setParam("0", "value")}.
   * <p>
   * Another option is to specify param keys using
   * {@code :paramName} and the param may be set using
   * {@code .setParam("paramName", "value")}.
   *
   * @param key   The key of the param.
   * @param value The value of the param.
   */
  public void setParam(String key, String value) {
    this.params.put(key, value);
  }

  /**
   * Retrieves a specific query's details, or {@code null} if
   * the query cannot be found.
   *
   * @param query The query to look for.
   * @return the specified query's details, or {@code null} if
   *         not found.
   */
  public String getQuery(String query) {
    return queryStrings.get(query);
  }

  /**
   * Retrieves a set with all the queries stored in this
   * request object.
   *
   * @return a {@code Set} with all the queries stored in this
   *         request object.
   */
  public Set<String> getAllQueries() {
    return queryStrings.keySet();
  }

  /**
   * Checks if a specific query is stored in this request
   * object.
   *
   * @param query The query to check.
   * @return true if this request object has details for the
   *         specified query.
   */
  public boolean hasQuery(String query) {
    return queryStrings.containsKey(query);
  }

  /**
   * Retrieves this request's HTTP protocol.
   *
   * @return this request's HTTP protocol.
   */
  public String getProtocol() {
    return this.protocol;
  }

  /**
   * Retrieves this request's requested resource, which can be
   * found at the end of the path.
   *
   * @return this request's requested resource.
   */
  public String getEndResource() {
    return this.endResource;
  }
}
