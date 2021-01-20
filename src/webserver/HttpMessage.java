package webserver;

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
 * Created <b> 2021-01-01 </b>
 *
 * @since 0.0.1
 * @version 0.0.4
 * @author Joseph Wang
 */
abstract class HttpMessage {
  /** The headers of this HttpMessage. */
  protected Map<String, String> headers;
  /** The body of this HttpMessage, if present. */
  protected String body;

  /**
   * A constructor for a new HttpMessage with no body or
   * headers, for invokation by implementing subclasses. The
   * headers will be initialized as empty.
   */
  public HttpMessage() {
    this("");
  }

  /**
   * A constructor for a new HttpMessage with a body but no
   * headers, for invokation by implementing subclasses. The
   * headers will be initialized as empty.
   *
   * @param body The body of the message.
   */
  public HttpMessage(String body) {
    this.headers = new HashMap<>();
    this.body = body;
  }

  /**
   * A constructor for a new HttpMessage with headers but no
   * body, for invokation of implementing subclasses.
   *
   * @param headers A {@code HashMap<String, String>} with
   *                applicable headers.
   */
  public HttpMessage(Map<String, String> headers) {
    this(headers, "");
  }

  /**
   * A constructor for a new HttpMessage with both a body and
   * headers, for invokation of implementing subclasses.
   *
   * @param headers A {@code HashMap<String, String>} with
   *                applicable headers.
   * @param body    The body of the message, if applicable.
   */
  public HttpMessage(Map<String, String> headers, String body) {
    this.headers = new HashMap<>(headers);
    this.body = body;
  }

  /**
   * A constructor for a new HttpMessage with headers but no
   * body, for invokation of implementing subclasses. This
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
   *                                header is provided.
   */
  public HttpMessage(String[] headers) throws InvalidHeaderException {
    this(headers, "");
  }

  /**
   * A constructor for a new HttpMessage with headers and a
   * body, for invokation of implementing subclasses. This
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
   *                                header is provided.
   */
  public HttpMessage(String[] headers, String body)
    throws InvalidHeaderException {
    this.headers = new HashMap<>();
    this.addHeaders(headers);
    this.body = "";
  }

  /**
   * Adds a header and value to this message's list of
   * headers.
   * <p>
   * The header added cannot be an empty string or
   * {@code null}.
   *
   * @param header  The name of the header to be added.
   * @param value The value of the header.
   * @throws InvalidHeaderException if an empty or null header
   *                                is provided.
   */
  public void addHeader(String header, String value) throws InvalidHeaderException {
    if (header == null || header.equals("")) {
      throw new InvalidHeaderException("Cannot have an empty header.");
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
    }
  }

  /**
   * Adds a group of headers to this message's list of
   * headers.
   *
   * @param headers A header-name header-detail map of headers
   *                to add.
   */
  public void addHeaders(Map<String, String> headers) {
    this.headers.putAll(headers);
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
   * An {@code InvalidHeaderException} will be thrown if an
   * improperly formatted header is provided.
   *
   * @param headers A string array of headers.
   * @throws InvalidHeaderException if an improperly formatted
   *                                header is provided.
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
      }
    }
  }

  /**
   * Retrieves all of this message's headers.
   *
   * @return the original {@code HashMap<String, String>}
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
   * Retrieves this message's body.
   * <p>
   * If this message has no body, an empty string will be
   * returned.
   *
   * @return this message's body.
   */
  public String getBody() {
    return this.body;
  }
}
