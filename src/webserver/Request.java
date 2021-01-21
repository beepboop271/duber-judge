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
 * This request object stores both the full path (with query
 * strings), and a modified path that does not include query
 * strings. Query strings are parsed on initialization and
 * can be retrieved using {@link #getQuery(String)}.
 * <p>
 * Created <b> 2020-12-28 </b>
 *
 * @since 0.0.1
 * @version 0.0.1
 * @author Joseph Wang
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
  /** The query strings in the path. */
  private Map<String, String> queryStrings;
  /** The HTTP protocol for this request. */
  private String protocol;

  /**
   * Constructs a new Request without any headers or a body.
   * <p>
   * The body will be initialized as an empty string.
   *
   * @param method   The HTTP method for the request.
   * @param fullPath The full path for the request.
   * @param protocol The HTTP protocol for this request.
   * @throws HttpSyntaxException if the path provided is
   *                                invalid.
   */
  public Request(String method, String fullPath, String protocol)
    throws HttpSyntaxException {
    this(method, fullPath, protocol, new HashMap<String, String>(), "");
  }

  /**
   * Constructs a new Request with a body but no headers.
   *
   * @param method   The HTTP method for the request.
   * @param fullPath The full path for the request.
   * @param protocol The HTTP protocol for this request.
   * @param body     The body of the request.
   * @throws HttpSyntaxException if the path provided is
   *                                invalid.
   */
  public Request(String method, String fullPath, String protocol, String body)
    throws HttpSyntaxException {
    this(method, fullPath, protocol, new HashMap<String, String>(), body);
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
   * @throws HttpSyntaxException if the path provided is
   *                                invalid.
   */
  public Request(
    String method,
    String fullPath,
    String protocol,
    Map<String, String> headers
  ) throws HttpSyntaxException {
    this(method, fullPath, protocol, headers, "");
  }

  /**
   * Constructs a new Request with specified string array of
   * headers but no body.
   * <p>
   * The body will be initialzed as an empty string.
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
   * @throws HttpSyntaxException if the path provided is
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
   * @throws HttpSyntaxException if the path provided is
   *                                invalid.
   */
  public Request(
    String method,
    String fullPath,
    String protocol,
    Map<String, String> headers,
    String body
  ) throws HttpSyntaxException {
    super(headers, body);

    this.method = method;
    this.path = fullPath;
    this.protocol = protocol;
    this.queryStrings = new HashMap<>();

    try {
      URI pathUri = new URI(fullPath);
      this.parseQueryStrings(pathUri.getQuery());
      this.path = pathUri.getPath();
    } catch (URISyntaxException e) {
      throw new HttpSyntaxException("Provided path is invalid.", e);
    }
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
   * @throws HttpSyntaxException if the path provided is
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

    // Use URI class for proper decoding and query processing
    try {
      URI pathUri = new URI(fullPath);
      if (pathUri.getQuery() != null) {
        this.parseQueryStrings(pathUri.getQuery());
      }
      this.path = pathUri.getPath();
    } catch (URISyntaxException e) {
      throw new HttpSyntaxException("Provided path is invalid.", e);
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
}
