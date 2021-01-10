package webserver;

import java.util.HashMap;

/**
 * This class represents an HTTP {@code Request} object.
 * <p>
 * The web server does not need to propagate this request to other HTTP servers,
 * and so does not store the exact request status line or implement a method
 * designed to format this request into a proper HTTP request string.
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
  /** The path for the request. */
  private String path;

  /**
   * Constructs a new Request without any headers.
   * 
   * @param method the HTTP method for the request.
   * @param path   the path for the request.
   */
  public Request(String method, String path) {
    this(method, path, new HashMap<String, String>());
  }

  /**
   * Constructs a new Request with specified headers.
   * 
   * @param method  the HTTP method for the request.
   * @param path    the path for the request.
   * @param headers the headers for this request.
   */
  public Request(String method, String path, HashMap<String, String> headers) {
    super(headers);

    this.method = method;
    this.path = path;
  }

  /**
   * Constructs a new Request with specified string array of headers.
   * <p>
   * This constructor will accept a string array of headers, seperated by a colon.
   * <p>
   * For example: {@code ["Connection: Keep-Alive", "Accept-Language: en-us"]}
   * will be parsed into
   * {@code Connection: "Keep-Alive", Accept-Language: "en-us"}.
   * 
   * @param method  the HTTP method for the request.
   * @param path    the path for the request.
   * @param headers the headers for this request.
   */
  public Request(String method, String path, String[] headers) {
    super(headers);

    this.method = method;
    this.path = path;
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
   * Retrieves this Request's method path.
   * 
   * @return a String with this Request's method path.
   */
  public String getPath() {
    return this.path;
  }
}