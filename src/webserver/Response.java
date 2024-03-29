package webserver;

import java.nio.charset.StandardCharsets;
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
 * This response will accept both Strings and byte arrays as
 * valid forms of body. Strings will be converted into UTF-8
 * byte arrays. For the sake of convenience, the majority of
 * the constructors and generic static methods will use
 * Strings for body, as byte arrays are less readable and
 * should only be used to represent objects that cannot be
 * Strings (eg. files).
 * <p>
 * Note that any cookies stored in this class are expected
 * to be used as `Set-Cookie`, and this class will be
 * converted to a string with that in mind.
 * <p>
 * Created <b> 2020-12-28 </b>
 *
 * @since 0.0.1
 * @version 1.0.0
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
   * Generates a {@code 500 Internal Server Error} HTTP
   * response, to indicate server failure to the client.
   *
   * @return a 500 HTTP response object.
   */
  public static Response internalError() {
    return new Response(500);
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
   * Generates a {@code 201 Created} html HTTP response with
   * the appropriate headers, and sets the resource link to a
   * provided link.
   *
   * @param resourceLink The link to the newly created
   *                     resource.
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
   * Generates a {@code 308 Temporary Redirect} HTTP response
   * with the appropriate {@code Location} header, redirecting
   * to the provided URI temporarily.
   *
   * @param newUri The new URI to redirect the client to.
   * @return a 308 HTTP response object.
   */
  public static Response temporaryRedirect(String newUri) {
    if (newUri == null || newUri.equals("")) {
      throw new IllegalArgumentException("A uri must be provided.");
    }

    Response response = new Response(307);
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
   * Generates a {@code 200 OK} HTTP response with the
   * appropriate headers and body array.
   * <p>
   * The MIME type of the body must be specified (see <a href=
   * "https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types">MDN
   * docs</a> for more information).
   * <p>
   * The html can be set as the body of the request by setting
   * {@code hasBody} to true. If false, only the headers are
   * returned, without the body.
   *
   * @param body    The byte array file to send in the
   *                Response.
   * @param mime    The mime type of this file.
   * @param hasBody Whether this request has a body or not.
   * @return a 200 HTTP response object.
   */
  public static Response ok(byte[] body, String mime, boolean hasBody) {
    Response response;
    if (hasBody) {
      response = new Response(200, body);
    } else {
      response = new Response(200);
    }

    response.headers.put("Content-Type", mime);
    response.headers.put("Content-Length", Integer.toString(body.length));

    return response;
  }

  /**
   * Generates a {@code 200 OK} HTTP response with the
   * appropriate headers and body array.
   * <p>
   * The MIME type of the body must be specified (see <a href=
   * "https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types">MDN
   * docs</a> for more information).
   *
   * @param body The byte array file to send in the Response.
   * @param mime The mime type of this file.
   * @return a 200 HTTP response object.
   */
  public static Response ok(byte[] body, String mime) {
    return Response.ok(body, mime, true);
  }

  /**
   * Generates a {@code 200 OK} HTTP response with the
   * appropriate headers and body.
   * <p>
   * The MIME type of the body must be specified (see <a href=
   * "https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types">MDN
   * docs</a> for more information).
   * <p>
   * The html can be set as the body of the request by setting
   * {@code hasBody} to true. If false, only the headers are
   * returned, without the body.
   *
   * @param body    The file to send in the Response.
   * @param mime    The mime type of this file.
   * @param hasBody Whether this request has a body or not.
   * @return a 200 HTTP response object.
   */
  public static Response ok(String body, String mime, boolean hasBody) {
    return Response.ok(body.getBytes(StandardCharsets.UTF_8), mime, hasBody);
  }

  /**
   * Generates a {@code 200 OK} HTTP response with the
   * appropriate headers and body.
   * <p>
   * The MIME type of the body must be specified (see <a href=
   * "https://developer.mozilla.org/en-US/docs/Web/HTTP/Basics_of_HTTP/MIME_types">MDN
   * docs</a> for more information).
   *
   * @param body The file to send in the Response.
   * @param mime The mime type of this file.
   * @return a 200 HTTP response object.
   */
  public static Response ok(String body, String mime) {
    return Response.ok(body, mime, true);
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
   * {@code hasBody} to true. If false, only the headers are
   * returned, without the body.
   *
   * @param html    The html file to send in the Response.
   * @param hasBody Whether this request has a body or not.
   * @return a 200 HTTP response object.
   */
  public static Response okHtml(String html, boolean hasBody) {
    return Response.okByteHtml(html.getBytes(StandardCharsets.UTF_8), hasBody);
  }

  /**
   * Generates a {@code 200 OK} html HTTP response with the
   * appropriate headers and body.
   *
   * @param body The byte array body to send in the Response.
   * @return a 200 HTTP response object.
   */
  public static Response okByteHtml(byte[] body) {
    return Response.okByteHtml(body, true);
  }

  /**
   * Generates a {@code 200 OK} html HTTP response with the
   * appropriate headers.
   * <p>
   * The html can be set as the body of the request by setting
   * {@code hasBody} to true. If false, only the headers are
   * returned, without the body.
   *
   * @param body    The byte array body to send in the
   *                Response.
   * @param hasBody Whether this request has a body or not.
   * @return a 200 HTTP response object.
   */
  public static Response okByteHtml(byte[] body, boolean hasBody) {
    return Response.ok(body, "text/html", hasBody);
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
   * {@code hasBody} to true. If false, only the headers are
   * returned, without the body.
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
   * {@code hasBody} to true. If false, only the headers are
   * returned, without the body.
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
   * {@code hasBody} to true. If false, only the headers are
   * returned, without the body.
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
   * Generates a generic {@code 404 Not Found} response, with
   * no body.
   *
   * @return a 404 HTTP response object.
   */
  public static Response notFound() {
    return new Response(404);
  }

  /**
   * Generates a generic HTML page to represent a
   * {@code 404 Not Found} response, with the associated
   * headers and the appropriate headers.
   *
   * @param resource The resource requested.
   * @return a 404 HTTP response object.
   */
  public static Response notFoundHtml(String resource) {
    return Response.notFoundHtml(resource, true);
  }

  /**
   * Generates a generic HTML page to represent a
   * {@code 404 Not Found} response, with the associated
   * headers and the appropriate headers.
   * <p>
   * The html can be set as the body of the request by setting
   * {@code hasBody} to true. If false, only the headers are
   * returned, without the body.
   *
   * @param resource The resource requested.
   * @param hasBody  Whether this resource has a body or not.
   * @return a 404 HTTP response object.
   */
  public static Response notFoundHtml(String resource, boolean hasBody) {
    Response response;
    String body =
      "<html><head><title>404</title></head><body>404: "
        +resource
        +" not found. </body></html>";

    if (hasBody) {
      response = new Response(404, body);
    } else {
      response = new Response(404);
    }

    response.headers.put("Content-Type", "text/html");
    response.headers.put("Content-Length", Integer.toString(body.length()));

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
   * Constructs a new Response, without any headers.
   *
   * @param statusCode The status code of the response (eg.
   *                   201, 404, etc)
   * @param body       The body byte array with the response.
   */
  public Response(int statusCode, byte[] body) {
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
   * Generates the output bytes for this response object with
   * its headers, body, and status line.
   * <p>
   * This method should be the primary way to convert this
   * response to an object suitable for stream output through
   * a connection.
   *
   * @return a byte array representation of this response.
   */
  public byte[] toOutputBytes() {
    byte[] headerBytes = this.toHeadString().getBytes(StandardCharsets.UTF_8);
    byte[] fullOutput = new byte[headerBytes.length+this.body.length];

    System.arraycopy(headerBytes, 0, fullOutput, 0, headerBytes.length);
    System.arraycopy(
      this.body.length,
      0,
      fullOutput,
      headerBytes.length,
      this.body.length
    );
    return fullOutput;
  }

  /**
   * Returns a formatted string of this response's headers and
   * status line, but no body even if a body is present.
   * <p>
   * As the body of a response may not necessarily be
   * convertible to a legible string, output of a response
   * should be done using the {@link #toOutputBytes()}.
   */
  public String toHeadString() {
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
        responseString
          .append("Set-Cookie: "+cookie+"="+this.cookies.get(cookie)+";\r\n");
      }
    }

    responseString.append(this.getHeadersString());

    return responseString.toString();
  }

  /**
   * {@inheritDoc}
   * <p>
   * This method will return a formatted representation of
   * this response's headers, status line, and body.
   * <p>
   * Getting response for output should not be done with this
   * method. Use {@link #toOutputBytes()} for the proper
   * output, as the bytes for the body may have come from a
   * file, etc that may not be converted properly to readable
   * text.
   */
  public String toString() {
    StringBuilder responseString = new StringBuilder(this.toHeadString());

    if (this.body.length != 0) {
      responseString.append(new String(this.getBody(), StandardCharsets.UTF_8));
    }

    return responseString.toString();
  }
}
