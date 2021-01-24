package webserver;

import java.util.HashMap;
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
 * Note that any cookies stored in this class are expected
 * to be used as `Set-Cookie`, and this class will be
 * converted to a string with that in mind.
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
   * The max ages of the cookies to be set, to work in tandem
   * with {@link HttpMessage#cookies}. Any cookie that does
   * not have an age is set as a session cookie.
   */
  private HashMap<String, Integer> cookieAges;

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
   * Generates a {@code 405 Method Not Allowed} HTTP response
   * with the appropriate {@code Allow} header detailing
   * allowed methods.
   * <p>
   * This should never be returned on a {@code GET} or
   * {@code HEAD} method.
   * <p>
   * Allowed methods can be provided. Note that {@code GET}
   * and {@code HEAD} are already included as they should
   * never return a 405. They do not need to be re-included,
   *
   * @param allowedMethods Methods that this resource allows,
   *                       with exception to {@code GET} and
   *                       {@code HEAD} as they are already
   *                       included.
   * @return a 405 HTTP response object.
   */
  public static Response methodNotAllowed(String... allowedMethods) {
    StringBuilder allow = new StringBuilder("GET, HEAD");
    for (String s : allowedMethods) {
      // If GET or HEAD are re-included we do not need to fail
      if (!s.equals("GET") && !s.equals("HEAD")) {
        allow.append(", "+s);
      }
    }

    Response response = new Response(405);
    response.headers.put("Allow", allow.toString());

    return response;
  }

  /**
   * Generates a {@code 403 Forbidden} HTTP response.
   *
   * @return a 403 HTTP response object.
   */
  public static Response forbidden() {
    return new Response(403);
  }

  /**
   * Generates a {@code 200 OK} html HTTP response with the
   * appropriate headers and body.
   *
   * @param html The html file to send in the Response.
   * @return a 200 HTTP response object.
   */
  public static Response okHtml(String html) {
    return Response.okHtml(html, true);
  }

  /**
   * Generates a {@code 200 OK} html HTTP response with the
   * appropriate headers.
   * <p>
   * The html can be set as the body of the request by setting
   * {@code hasBody} to true. However, the client may simply
   * want the headers and nothing more, in which case
   * {@code hasBody} should be set to false.
   *
   * @param html    The html file to send in the Response.
   * @param hasBody Whether this request has a body or not.
   * @return a 200 HTTP response object.
   */
  public static Response okHtml(String html, boolean hasBody) {
    Response response;
    if (hasBody) {
      response = new Response(200, html);
    } else {
      response = new Response(200);
    }

    response.headers.put("Content-Type", "text/html");
    response.headers.put("Content-Length", Integer.toString(html.length()));

    return response;
  }

  /**
   * Generates a {@code 200 OK} html HTTP response with the
   * appropriate headers and body, and informs the browser not
   * to cache this page.
   *
   * @param html The html file to send in the Response.
   * @return a 200 HTTP response object.
   */
  public static Response okNoCacheHtml(String html) {
    return Response.okNoCacheHtml(html, true);
  }

  /**
   * Generates a {@code 200 OK} html HTTP response with the
   * appropriate headers, and informs the browser not to cache
   * this page.
   * <p>
   * The html can be set as the body of the request by setting
   * {@code hasBody} to true. However, the client may simply
   * want the headers and nothing more, in which case
   * {@code hasBody} should be set to false.
   *
   * @param html    The html file to send in the Response.
   * @param hasBody Whether this request has a body or not.
   * @return a 200 HTTP response object.
   */
  public static Response okNoCacheHtml(String html, boolean hasBody) {
    Response response = Response.okHtml(html, hasBody);
    response.headers.put("Cache-Control", "no-store, max-age=0");

    return response;
  }

  /**
   * Generates a {@code 200 OK} html HTTP response with the
   * appropriate headers and body, and sets the specified
   * cookie in the as a session cookie, which is removed upon
   * client shutdown.
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
    return Response.okSetCookieHtml(html, name, value, -1, true);
  }

  /**
   * Generates a {@code 200 OK} html HTTP response with the
   * appropriate headers, and sets the specified cookie in the
   * as a session cookie, which is removed upon client
   * shutdown.
   * <p>
   * The html can be set as the body of the request by setting
   * {@code hasBody} to true. However, the client may simply
   * want the headers and nothing more, in which case
   * {@code hasBody} should be set to false.
   *
   * @param html  The html file to send in the Response.
   * @param name  The name of the cookie.
   * @param value The value of the cookie.
   * @return a 200 HTTP response object.
   */
  public static Response okSetCookieHtml(
    String html,
    String name,
    String value,
    boolean hasBody
  ) {
    return Response.okSetCookieHtml(html, name, value, -1, hasBody);
  }

  /**
   * Generates a {@code 200 OK} html HTTP response with the
   * appropriate headers and body, and sets the specified
   * cookie in the as a cookie, with the provided max age.
   * <p>
   * If {@code maxAge} is provided as {@code -1}, the
   * specified cookie will be a session cookie, which is
   * removed upon client shutdown. To have a cookie that
   * expires immediately, set {@code maxAge} to {@code 0};
   *
   * @param html   The html file to send in the Response.
   * @param name   The name of the cookie.
   * @param value  The value of the cookie.
   * @param maxAge The amount of time for the cookie to live,
   *               in seconds, or -1 if this cookie should be
   *               a session cookie.
   * @return a 200 HTTP response object.
   */
  public static Response okSetCookieHtml(
    String html,
    String name,
    String value,
    int maxAge
  ) {
    return Response.okSetCookieHtml(html, name, value, maxAge, true);
  }

  /**
   * Generates a {@code 200 OK} html HTTP response with the
   * appropriate headers, and sets the specified cookie in the
   * as a cookie, with the provided max age.
   * <p>
   * If {@code maxAge} is provided as {@code -1}, the
   * specified cookie will be a session cookie, which is
   * removed upon client shutdown. To have a cookie that
   * expires immediately, set {@code maxAge} to {@code 0};
   * <p>
   * The html can be set as the body of the request by setting
   * {@code hasBody} to true. However, the client may simply
   * want the headers and nothing more, in which case
   * {@code hasBody} should be set to false.
   *
   * @param html    The html file to send in the Response.
   * @param name    The name of the cookie.
   * @param value   The value of the cookie.
   * @param maxAge  The amount of time for the cookie to live,
   *                in seconds, or -1 if this cookie should be
   *                a session cookie.
   * @param hasBody Whether this request has a body or not.
   * @return a 200 HTTP response object.
   */
  public static Response okSetCookieHtml(
    String html,
    String name,
    String value,
    int maxAge,
    boolean hasBody
  ) {
    if (name.equals("") || value.equals("")) {
      throw new IllegalArgumentException("The provided cookie is invalid.");
    }

    Response response = Response.okHtml(html, hasBody);
    if (maxAge == -1) {
      response.addCookie(name, value);
    } else {
      response.addCookie(name, value, maxAge);
    }

    return response;
  }

  /**
   * Generates a {@code 201 Created} html HTTP response with the
   * appropriate headers, and sets the resource link to a provided link.
   *
   * @param resourceLink The link to the newly created resource.
   * @return a 201 HTTP response object.
   */
  public static Response created(String resourceLink) {
    Response response = new Response(201);
    response.headers.put("Location", resourceLink);

    return response;
  }

  /**
   * Generates a {@code 308 Permanent Redirect} HTTP response
   * with the appropriate {@code Location} header, redirecting
   * to the provided URI.
   *
   * @param newUri The new URI to redirect the client to.
   * @return a 308 HTTP response object.
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
   * @return a 303 HTTP response object.
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
    this.cookieAges = new HashMap<>();
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
    this.cookieAges = new HashMap<>();
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
    this.cookieAges = new HashMap<>();
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
   * Note that cookies added this way will be session cookies.
   * To add a max age, use
   * {@link #addCookie(String, String, int)} instead.
   *
   * @param name  The name of the cookie.
   * @param value The value of the cookie.
   */
  @Override
  public void addCookie(String name, String value) {
    try {
      super.addCookie(name, value);

    } catch (InvalidCookieException e) {
      // Response cookies should not stem from a stream, so we can
      // convert this to a runtime exception as all Response
      // cookie errors should come from programmer implementation.
      throw new IllegalArgumentException("Improper cookie input", e);
    }
  }

  /**
   * Adds a cookie to this message's cookie map.
   * <p>
   * The cookie will have a max age setting equal to the
   * provided seconds to live.
   *
   * @param name          The name of the cookie to add.
   * @param value         The value of the cookie.
   * @param secondsToLive The amount of seconds for the cookie
   *                      to live.
   */
  public void addCookie(String name, String value, int secondsToLive) {
    try {
      super.addCookie(name, value);
      this.cookieAges.put(name, secondsToLive);
    } catch (InvalidCookieException e) {
      // Response cookie errors should all stem from programmer
      // errors
      throw new IllegalArgumentException("Improper cookie input", e);
    }
  }

  /**
   * {@inheritDoc}
   * <p>
   * This method will return a string that is formatted and
   * ready for output back to a client.
   */
  public String toString() {
    StringBuilder responseString = new StringBuilder(this.statusString+"\r\n");

    for (String cookie : this.cookies.keySet()) {
      if (this.cookieAges.containsKey(cookie)) {
        responseString.append(
          "Set-Cookie: "
            +cookie
            +"="
            +this.cookies.get(cookie)
            +"; Max-Age="
            +this.cookieAges.get(cookie)
            +"\r\n"
        );
      } else {
        responseString.append(
          "Set-Cookie: "
            +cookie
            +"="
            +this.cookies.get(cookie)
            +";\r\n"
        );
      }
    }

    responseString.append(this.getHeadersString());

    if (!this.body.equals("")) {
      responseString.append(this.getBody());
    }

    return responseString.toString();
  }
}
