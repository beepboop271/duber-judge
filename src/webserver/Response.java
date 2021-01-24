package webserver;

import java.util.Map;

/**
 * This class represents an HTTP {@code Response} object.
 * <p>
 * Since the web server is expected to return a properly
 * formatted HTTP response object, this class overrides and
 * implements {@link #toString()} which will convert this
 * object to a properly formatted HTTP response string,
 * ready to be sent to the client.
 * <p>
 * Created <b> 2020-12-28 </b>
 *
 * @since 0.0.1
 * @version 0.0.4
 * @author Joseph Wang
 * @see <a href=
 *      "https://developer.mozilla.org/en-US/docs/Web/HTTP/Messages#HTTP_Responses">More
 *      information about HTTP Responses (Mozilla)</a>
 */
public class Response extends HttpMessage {
  /** The status of the response. */
  private String statusString;

  /**
   * Generates a generic HTML page to represent a
   * {@code 404 Not Found} response, with the associated
   * headers and the appropriate headers.
   *
   * @param resource The resource requested.
   * @return a 404 HTTP response object.
   */
  public static Response notFoundHtml(String resource) {
    String body =
      "<html><head><title>404</title></head><body>404: "
        +resource
        +" not found. </body></html>";

    Response response = new Response(404, body);
    response.headers.put("Content-Type", "text/html");
    response.headers.put("Content-Length", Integer.toString(body.length()));

    return response;
  }

  /**
   * Generates a generic HTML page to represent a
   * {@code 400 Bad Request} response with the associated
   * cause and the appropriate headers.
   *
   * @param reason The cause of the bad response.
   * @return a 400 HTTP response object.
   */
  public static Response badRequestHtml(String reason) {
    String body =
      "<html><head><title>400</title></head><body>400: Bad Request - "
        +reason
        +"</body></html>";

    Response response = new Response(400, body);
    response.headers.put("Content-Type", "text/html");
    response.headers.put("Content-Length", Integer.toString(body.length()));

    return response;
  }

  /**
   * Generates a generic
   * {@code 505 HTTP Version Not Supported} HTTP response with
   * the appropriate headers.
   *
   * @return a 505 HTTP response object.
   */
  public static Response unsupportedVersion() {
    Response response = new Response(505);

    return response;
  }

  /**
   * Generates a generic {@code 400 Bad Request} HTTP response
   * with the appropriate headers.
   *
   * @return a 400 HTTP response object.
   */
  public static Response badRequest() {
    Response response = new Response(400);

    return response;
  }

  /**
   * Generates a {@code 200 OK} html HTTP response with the
   * appropriate headers.
   *
   * @param html The html file to send in the Response.
   * @return a 200 HTTP response object.
   */
  public static Response okHtml(String html) {
    Response response = new Response(200, html);
    response.headers.put("Content-Type", "text/html");
    response.headers.put("Content-Length", Integer.toString(html.length()));

    return response;
  }

  /**
   * Generates a {@code 200 OK} html HTTP response with the
   * appropriate headers, and informing the browser not to
   * cache this page.
   *
   * @param html The html file to send in the Response.
   * @return a 200 HTTP response object.
   */
  public static Response okNoCacheHtml(String html) {
    Response response = Response.okHtml(html);
    response.headers.put("Cache-Control", "no-store, max-age=0");

    return response;
  }

  /**
   * Generates a {@code 200 OK} html HTTP response with the
   * appropriate headers, and sets the specified cookie in the
   * as a session cookie, which is removed upon client
   * shutdown.
   *
   * @param html  The html file to send in the Response.
   * @param name  The name of the cookie.
   * @param value The value of the cookie.
   * @return a 200 HTTP response object.
   */
  public static Response okSetCookieHtml(
    String html,
    String name,
    String value
  ) {
    if (name.equals("") || value.equals("")) {
      throw new IllegalArgumentException("The provided cookie is invalid.");
    }

    Response response = Response.okHtml(html);
    response.headers.put("Set-Cookie", name+"="+value);

    return response;
  }

  /**
   * Generates a {@code 200 OK} html HTTP response with the
   * appropriate headers, and sets the specified cookie in the
   * as a cookie, with the provided max age.
   *
   * @param html   The html file to send in the Response.
   * @param name   The name of the cookie.
   * @param value  The value of the cookie.
   * @param maxAge The amount of time for the cookie to live,
   *               in seconds.
   * @return a 200 HTTP response object.
   */
  public static Response okSetCookieHtml(
    String html,
    String name,
    String value,
    int maxAge
  ) {
    if (name.equals("") || value.equals("")) {
      throw new IllegalArgumentException("The provided cookie is invalid.");
    }

    Response response = Response.okHtml(html);
    response.headers.put("Set-Cookie", name+"="+value+"; Max-Age="+maxAge);

    return response;
  }

  /**
   * Generates a {@code 308 Permanent Redirect} HTTP response
   * with the appropriate {@code Location} header, redirecting
   * to the provided URI.
   *
   * @param newUri The new URI to redirect the client to.
   * @return a 308 HTTP tesponse object.
   */
  public static Response permanentRedirect(String newUri) {
    if (newUri == null || newUri.equals("")) {
      throw new IllegalArgumentException("A uri must be provided.");
    }

    Response response = new Response(308);
    response.headers.put("Location", newUri);

    return response;
  }

  /**
   * Generates a {@code 303 See Other} HTTP response with the
   * appropriate {@code Location} header, redirecting to the
   * provided URI that has another page.
   * <p>
   * This should normally be sent back as a result of a POST
   * or PUT to redirect to another page, etc.
   *
   * @param newUri The new URI to redirect the client to.
   * @return a 303 HTTP tesponse object.
   */
  public static Response seeOther(String newUri) {
    if (newUri == null || newUri.equals("")) {
      throw new IllegalArgumentException("A uri must be provided.");
    }

    Response response = new Response(303);
    response.headers.put("Location", newUri);

    return response;
  }

  /**
   * Constructs a new Response, without any body or headers.
   * <p>
   * The body will be initialized as an empty string.
   *
   * @param statusCode The status code of the response (eg.
   *                   201, 404, etc)
   */
  public Response(int statusCode) {
    this(statusCode, "");
  }

  /**
   * Constructs a new Response, without any headers.
   *
   * @param statusCode The status code of the response (eg.
   *                   201, 404, etc)
   * @param body       The body of the response.
   */
  public Response(int statusCode, String body) {
    super(body);

    this.statusString = "HTTP/1.1 "+statusCode;
  }

  /**
   * Constructs a new Response with the specified map of
   * headers.
   *
   * @param statusCode The status code of the response (eg.
   *                   201, 404, etc)
   * @param headers    A map with the headers of the response.
   * @param body       The body of the response.
   * @throws InvalidHeaderException if an improperly formatted
   *                                header or value is
   *                                provided in the headers
   *                                map.
   */
  public Response(int statusCode, Map<String, String> headers, String body)
    throws InvalidHeaderException {
    super(headers, body);

    this.statusString = "HTTP/1.1 "+statusCode;
  }

  /**
   * Constructs a new Response with the specified string array
   * of headers.
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
   * @param statusCode The status code of the response (eg.
   *                   201, 404, etc)
   * @param headers    A string array with the headers of the
   *                   response.
   * @param body       The body of the response.
   * @throws InvalidHeaderException if an improperly formatted
   *                                header is provided.
   */
  public Response(int statusCode, String[] headers, String body)
    throws InvalidHeaderException {
    super(headers, body);

    this.statusString = "HTTP/1.1 "+statusCode;
  }

  /**
   * Constructs a new Response with the specified map of
   * headers and no body.
   *
   * @param statusCode The status code of the response (eg.
   *                   201, 404, etc)
   * @param headers    A map with the headers of the response.
   * @throws InvalidHeaderException if an improperly formatted
   *                                header or value is
   *                                provided in the headers
   *                                map.
   */
  public Response(int statusCode, Map<String, String> headers)
    throws InvalidHeaderException {
    this(statusCode, headers, "");
  }

  /**
   * Constructs a new Response with the specified string array
   * of headers and no body.
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
   * @param statusCode The status code of the response (eg.
   *                   201, 404, etc)
   * @param headers    A string array with the headers of the
   *                   response.
   * @throws InvalidHeaderException if an improperly formatted
   *                                header is provided.
   */
  public Response(int statusCode, String[] headers)
    throws InvalidHeaderException {
    this(statusCode, headers, "");
  }

  /**
   * Retrieves this Response's status string.
   *
   * @return a string with this Response's status string.
   */
  public String getStatusString() {
    return this.statusString;
  }

  /**
   * {@inheritDoc}
   * <p>
   * This method will return a string that is formatted and
   * ready for output back to a client.
   */
  public String toString() {
    StringBuilder responseString = new StringBuilder(this.statusString+"\r\n");
    responseString.append(this.getHeadersString());

    if (!this.body.equals("")) {
      responseString.append(this.getBody());
    }

    return responseString.toString();
  }
}
