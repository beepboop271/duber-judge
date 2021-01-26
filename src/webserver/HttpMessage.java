package webserver;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * This class is the abstract server-side base
 * implementation class for HTTP messages, which include
 * response and requests.
 * <p>
 * HTTP messages both have headers and (occasionally)
 * bodies, and implementing sub classes are able to define
 * their own status line.
 * <p>
 * This message will accept both Strings and byte arrays as
 * valid forms of body. Strings will be converted into UTF-8
 * byte arrays. For the sake of convenience, the majority of
 * the constructors will use Strings for body, as byte
 * arrays are less readable and should only be used to
 * represent objects that cannot be Strings (eg. files).
 * <p>
 * Created <b> 2021-01-01 </b>
 *
 * @since 0.0.1
 * @version1.0.0
 * @author Joseph Wang
 */
abstract class HttpMessage {
  /** The headers of this HttpMessage. */
  protected Map<String, String> headers;
  /** The cookies associated with the message. */
  protected Map<String, String> cookies;
  /**
   * A byte array body of this HttpMessage, if present.
   * Strings should be encoded using UTF-8.
   */
  protected byte[] body;

  /**
   * A constructor for a new HttpMessage with no body or
   * headers, for invocation by implementing subclasses. The
   * headers will be initialized as empty.
   */
  public HttpMessage() {
    this("");
  }

  /**
   * A constructor for a new HttpMessage with a body but no
   * headers, for invocation by implementing subclasses.
   * <p>
   * The headers will be initialized as empty.
   *
   * @param body The body of the message.
   */
  public HttpMessage(String body) {
    this.headers = new HashMap<>();
    this.cookies = new HashMap<>();
    this.body = body.getBytes(StandardCharsets.UTF_8);
  }

  /**
   * A constructor for a new HttpMessage with a byte array
   * body and no headers, for invocation by implementing
   * subclasses.
   * <p>
   * The headers will be initialized as empty.
   *
   * @param body The byte array with the body.
   */
  public HttpMessage(byte[] body) {
    this.body = body;
    this.headers = new HashMap<>();
    this.cookies = new HashMap<>();
  }

  /**
   * A constructor for a new HttpMessage with headers but no
   * body, for invocation of implementing subclasses.
   *
   * @param headers A {@code Map<String, String>} with
   *                applicable headers.
   * @throws InvalidHeaderException if an improperly formatted
   *                                header or value is
   *                                provided.
   */
  public HttpMessage(Map<String, String> headers)
    throws InvalidHeaderException {
    this(headers, "");
  }

  /**
   * A constructor for a new HttpMessage with both a body and
   * headers, for invocation of implementing subclasses.
   *
   * @param headers A {@code Map<String, String>} with
   *                applicable headers.
   * @param body    The body of the message, if applicable.
   * @throws InvalidHeaderException if an improperly formatted
   *                                header or value is
   *                                provided in the headers
   *                                map.
   */
  public HttpMessage(Map<String, String> headers, String body)
    throws InvalidHeaderException {
    this.headers = new HashMap<>();
    this.addHeaders(headers);
    this.cookies = new HashMap<>();
    this.body = body.getBytes(StandardCharsets.UTF_8);
  }

  /**
   * A constructor for a new HttpMessage with headers but no
   * body, for invocation of implementing subclasses. This
   * constructor will accept a string array of headers,
   * separated by a colon.
   * <p>
   * For example:
   * {@code ["Connection: Keep-Alive", "Accept-Language: en-us"]}
   * will be parsed into
   * {@code Connection: "Keep-Alive", Accept-Language: "en-us"}
   * and can be retrieved on a call to
   * {@link #getHeader(String)} with the appropriate header
   * name.
   * <p>
   * An {@code InvalidHeaderException} will be thrown if an
   * improperly formatted header is provided.
   *
   * @param headers A {@code String} array with applicable
   *                headers, with key and value separated by
   *                {@code :} for each header string.
   * @throws InvalidHeaderException if an improperly formatted
   *                                header or value is
   *                                provided.
   */
  public HttpMessage(String[] headers) throws InvalidHeaderException {
    this(headers, "");
  }

  /**
   * A constructor for a new HttpMessage with headers and a
   * body, for invocation of implementing subclasses. This
   * constructor will accept a string array of headers,
   * separated by a colon.
   * <p>
   * For example:
   * {@code ["Connection: Keep-Alive", "Accept-Language: en-us"]}
   * will be parsed into
   * {@code Connection: "Keep-Alive", Accept-Language: "en-us"}
   * and can be retrieved on a call to
   * {@link #getHeader(String)} with the appropriate header
   * name.
   * <p>
   * An {@code InvalidHeaderException} will be thrown if an
   * improperly formatted header is provided.
   *
   * @param headers A {@code String} array with applicable
   *                headers, with key and value separated by
   *                {@code :} for each header string.
   * @throws InvalidHeaderException if an improperly formatted
   *                                header or value is
   *                                provided.
   */
  public HttpMessage(String[] headers, String body)
    throws InvalidHeaderException {
    this.headers = new HashMap<>();
    this.addHeaders(headers);
    this.cookies = new HashMap<>();
    this.body = body.getBytes(StandardCharsets.UTF_8);
  }

  /**
   * Adds a header and value to this message's list of
   * headers.
   * <p>
   * The header added cannot be an empty string or
   * {@code null}.
   * <p>
   * Headers should be compliant with <a href=
   * "https://tools.ietf.org/html/rfc2616#section-14">Section
   * 14 of RFC 2626.</a>
   *
   * @param header The name of the header to be added.
   * @param value  The value of the header.
   * @throws InvalidHeaderException if an empty or null header
   *                                or value is provided.
   */
  public void addHeader(String header, String value)
    throws InvalidHeaderException {
    if (header == null || header.equals("")) {
      throw new InvalidHeaderException("Cannot have an empty header.");
    } else if (value == null || value.equals("")) {
      throw new InvalidHeaderException("Cannot have an empty header value.");
    }

    this.headers.put(header, value);
  }

  /**
   * Parses and adds the header to this message's list of
   * headers.
   * <p>
   * The provided string should be a header and value,
   * separated by a colon.
   * <p>
   * Headers should be compliant with <a href=
   * "https://tools.ietf.org/html/rfc2616#section-14">Section
   * 14 of RFC 2626.</a>
   * <p>
   * An {@code InvalidHeaderException} will be thrown if an
   * improperly formatted header is provided.
   *
   * @param header The header string to parse and add.
   * @throws InvalidHeaderException if an improperly formatted
   *                                header is provided.
   */
  public void addHeader(String header) throws InvalidHeaderException {
    if (header.indexOf(":") != -1) {
      String headerName = header.substring(0, header.indexOf(":")).trim();
      String headerValue = header.substring(header.indexOf(":")).trim();

      this.addHeader(headerName, headerValue);
    } else {
      throw new InvalidHeaderException("No header value provided.");
    }
  }

  /**
   * Adds a group of headers to this message's list of
   * headers.
   * <p>
   * Headers should be compliant with <a href=
   * "https://tools.ietf.org/html/rfc2616#section-14">Section
   * 14 of RFC 2626.</a>
   *
   * @param headers A header-name header-detail map of headers
   *                to add.
   * @throws InvalidHeaderException if an improperly formatted
   *                                header or value is
   *                                provided.
   */
  public void addHeaders(Map<String, String> headers)
    throws InvalidHeaderException {
    for (String header : headers.keySet()) {
      this.addHeader(header, headers.get(header));
    }
  }

  /**
   * Adds a string array of headers to this message's list of
   * headers. Headers in the array will be parsed, separated
   * by a colon.
   * <p>
   * For example:
   * {@code ["Connection: Keep-Alive", "Accept-Language: en-us"]}
   * will be parsed into
   * {@code Connection: "Keep-Alive", Accept-Language: "en-us"}.
   * <p>
   * Headers should be compliant with <a href=
   * "https://tools.ietf.org/html/rfc2616#section-14">Section
   * 14 of RFC 2626.</a>
   * <p>
   * An {@code InvalidHeaderException} will be thrown if an
   * improperly formatted header is provided.
   *
   * @param headers A string array of headers.
   * @throws InvalidHeaderException if an improperly formatted
   *                                header or value is
   *                                provided.
   */
  public void addHeaders(String[] headers) throws InvalidHeaderException {
    for (String s : headers) {
      // Seperate the headers info and place into hashmap
      if (s.indexOf(":") != -1) {
        String headerName = s.substring(0, s.indexOf(":")).trim();
        String headerValue = s.substring(s.indexOf(":")).trim();

        if (headerName.equals("") && !headerValue.equals("")) {
          throw new InvalidHeaderException("Improper header provided.");
        }

        this.headers.put(headerName, headerValue);
      } else {
        throw new InvalidHeaderException("No header value provided.");
      }
    }
  }

  /**
   * Retrieves all of this message's headers.
   *
   * @return the original {@code Map<String, String>}
   *         containing this message's headers.
   */
  public Map<String, String> getHeaders() {
    return this.headers;
  }

  /**
   * Retrieves a specified header's value from this message's
   * headers.
   * <p>
   * As noted by the {@link java.util.Map#get(Object) get
   * method used}, {@code null} will be returned if no key was
   * found.
   *
   * @return the string value for the specified header, or
   *         {@code null} if it does not exist.
   */
  public String getHeader(String header) {
    return this.headers.get(header);
  }

  /**
   * Checks if this message has a specified header.
   *
   * @param header The header to check for.
   * @return true if this message has the specified header.
   */
  public boolean hasHeader(String header) {
    return this.headers.containsKey(header);
  }

  /**
   * Returns a string containing the headers, ready for output
   * stream.
   * <p>
   * An empty line will be inserted after, so manual insertion
   * is not required.
   *
   * @return a string with all the headers.
   */
  public String getHeadersString() {
    StringBuilder headerString = new StringBuilder();

    for (String s : this.headers.keySet()) {
      headerString.append(s+": "+this.headers.get(s)+"\r\n");
    }

    headerString.append("\r\n");
    return headerString.toString();
  }

  /**
   * Retrieves this message's body, as a byte array.
   * <p>
   * If this message has no body, an empty array will be
   * returned.
   *
   * @return this message's body, as a byte array.
   */
  public byte[] getBody() {
    return this.body;
  }

  /**
   * Adds a cookie to this message.
   *
   * @param name  The name of the cookie.
   * @param value The value of the cookie.
   * @throws InvalidCookieException if the cookie name or
   *                                value is null or empty.
   */
  public void addCookie(String name, String value)
    throws InvalidCookieException {
    if (name == null || name.equals("") || value == null || value.equals("")) {
      throw new InvalidCookieException("A cookie field is invalid.");
    }

    this.cookies.put(name, value);
  }

  /**
   * Adds a string of cookies to this message.
   * <p>
   * Cookies must be in {@code name=value} format, and
   * separated with a semicolon {@code ;}.
   *
   * @param cookieString The string of cookies to parse
   *                     through.
   * @throws InvalidCookieException if the cookie format is
   *                                incorrect.
   */
  public void addCookies(String cookieString) throws InvalidCookieException {
    String[] cookies = cookieString.split(";");

    for (String cookie : cookies) {
      String[] cookieTokens = cookie.trim().split("=");
      if (cookieTokens.length != 2) {
        throw new InvalidCookieException("Cookie formatted incorrectly.");
      }

      this.addCookie(cookieTokens[0], cookieTokens[1]);
    }
  }

  /**
   * Gets a specific cookie from this message's map of
   * cookies.
   * <p>
   * If the cookie is not found, {@code null} will be
   * returned.
   *
   * @param cookieName The cookie to retrieve.
   * @return the cookie, or {@code null} if not found.
   */
  public String getCookie(String cookieName) {
    return this.cookies.get(cookieName);
  }

  /**
   * Checks if this message has a specific cookie.
   *
   * @param cookieName The cookie to check for.
   * @return true if this message has the cookie.
   */
  public boolean hasCookie(String cookieName) {
    return this.cookies.containsKey(cookieName);
  }

  /**
   * Retrieves all of this message's cookies.
   *
   * @return the original {@code Map<String, String>}
   *         containing this message's cookies.
   */
  public Map<String, String> getCookies() {
    return this.cookies;
  }
}
