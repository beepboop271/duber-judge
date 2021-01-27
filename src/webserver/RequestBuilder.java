package webserver;

import java.util.HashMap;

/**
 * A class designed to build a Request, string by string.
 * <p>
 * This class was mainly designed for building a request
 * sent from a connection or input sockets, line by line, by
 * appending each line to this class's request string using
 * {@link #append(String)}.
 * <p>
 * This class also contains functionality for determining if
 * the stored request has been fully assembled or if the
 * request should be timed out. Knowledge about the assembly
 * status for the request can be used to determine if a
 * request is finished, and when handling of the request can
 * occur. This class will evaluate headers if a call to
 * {@link #hasCompletedRequest()} is made, as a completed
 * request can only be determined once the headers have been
 * evaluated once and the {@code Content-Length} header is
 * checked for. Once the headers have been evaluated, they
 * are stored and used for construction so that only one
 * parse is required.
 * <p>
 * Timing out functionality is mainly to handle scenarios
 * where the connection stream may be done sending data, but
 * the request send was incorrect and so
 * {@link #hasCompletedRequest()} will continue to return
 * false. More details can be found in
 * {@link #shouldTimeout()}.
 * <p>
 * A Request can be constructed from the assembled internal
 * request string using the {@code #construct()} method.
 * This method will fail if the assembled request is invalid
 * or malformed, as according to
 * <a href= "https://tools.ietf.org/html/rfc7230">RFC
 * 7230.</a> Strings can still be appended after
 * construction, and another call to {@link #construct()}
 * will yield a different request.
 *
 * @since 0.0.4
 * @version 1.0.0
 * @author Joseph Wang
 * @see Request
 */
public class RequestBuilder {
  /**
   * The default amount of time to wait before timeout, in ms.
   */
  public static final int TIMEOUT_TIME_MS = 10_000;

  private String method;
  private String path;
  private String protocol;
  private HashMap<String, String> headers;
  private StringBuilder body;

  /** The string with the request. */
  private StringBuilder requestString;
  /** When this RequestBuilder was constructed. */
  private long startTime;
  /** The amount of time to wait before timeout, in ms. */
  private int timeoutLimit;

  /**
   * The content length specified in the header, if present,
   * or {@code -1} if not evaluated or doesn't exist.
   */
  private int contentLength = -1;

  /**
   * Whether the headers have been evaluated and body
   * existance/size determined or not.
   */
  private boolean evaluatedHeaders = false;

  /**
   * Constructs a new RequestBuilder with the default timeout
   * limit.
   * <p>
   * Upon construction, the start time will be stored, but can
   * be reset with a call to {@link #resetTimeoutStart()}. If
   * the current time minus the start time is greater than the
   * limit set, the server should timeout the client.
   * <p>
   * Further details can be found in {@link #shouldTimeout()}.
   */
  public RequestBuilder() {
    this(RequestBuilder.TIMEOUT_TIME_MS);
  }

  /**
   * Constructs a new RequestBuilder with a specified timeout
   * limit.
   * <p>
   * Upon construction, the start time will be stored, but can
   * be reset with a call to {@link #resetTimeoutStart()}. If
   * the current time minus the start time is greater than the
   * limit set, the server should timeout the client.
   * <p>
   * Further details can be found in {@link #shouldTimeout()}.
   *
   * @param timeoutInSeconds The specified amount of time to
   *                         wait before a timeout should
   *                         happen.
   */
  public RequestBuilder(int timeoutInSeconds) {
    this.requestString = new StringBuilder();
    this.body = new StringBuilder("");
    this.headers = new HashMap<>();

    this.startTime = System.currentTimeMillis();
    this.timeoutLimit = timeoutInSeconds;
  }

  /**
   * Appends a string to the currently in-construction
   * request.
   *
   * @param str The string to add.
   */
  public void append(String str) {
    this.requestString.append(str);

    // If the header is properly evaluated then we need to
    // append this string to the body
    if (this.evaluatedHeaders) {
      this.body.append(str);
    }
  }

  /**
   * Does a preliminary parse and evaluation of the required
   * properties of the request to determine the kind of body
   * this request will contain, and stores the evaluated
   * results for future use.
   * <p>
   * Locating the {@code Content-Length} header indicates that
   * this request should have a body (as specified in <a href=
   * "https://tools.ietf.org/html/rfc7230#section-3.3">Section
   * 3.3 of RFC 7230</a>), and should be taken into account
   * when determining if the request is complete or not.
   * {@code Transfer-Encoding} with value {@code chunked} will
   * not be considered.
   * <p>
   * For efficiency, the status line and headers calculated
   * will be stored and will be used in {@link #construct()}.
   *
   * @throws HttpSyntaxException if one of the properties
   *                             (headers, status, body, etc)
   *                             is invalid.
   */
  private void evaluateProperties() throws HttpSyntaxException {
    if (this.requestString.length() == 0) {
      this.failSyntax("Request not fully formed.");
    }

    String[] reqTokens = this.requestString.toString().split("\r\n\r\n");

    if (reqTokens.length == 1) {
      this.evaluateHeaders(reqTokens[0]);
    } else if (reqTokens.length == 2) {
      this.evaluateHeaders(reqTokens[0]);
      this.evaluateBody(reqTokens[1]);
    } else {
      this.failSyntax("Too many CRLF in request.");
    }
  }

  /**
   * Does a preliminary parse and evaluation of the provided
   * header string, and stores the status line and headers for
   * future use.
   * <p>
   *
   * @param headerInfo The string with the status line and
   *                   headers.
   * @throws HttpSyntaxException if the request isn't fully
   *                             loaded, or a header or status
   *                             is invalid.
   */
  private void evaluateHeaders(String headerInfo) throws HttpSyntaxException {
    // Since body is not guaranteed to have a trailing CRLF, we
    // can't use BufferedReader to read next line or else it
    // will keep looping
    String[] headerStrings = headerInfo.split("\r\n");
    if (headerStrings.length <= 1) {
      this.failSyntax("Request not fully formed.");
    }

    String[] statusTokens = headerStrings[0].split(" ");
    if (statusTokens.length != 3) {
      this.failSyntax("Status string invalid.");
    }

    // Parse status details
    this.method = statusTokens[0];
    this.path = statusTokens[1];
    this.protocol = statusTokens[2];

    for (int i = 1; i < headerStrings.length; i++) {
      String[] headerTokens = headerStrings[i].split(": ");
      if (headerTokens.length != 2) {
        this.failSyntax("Malformed header.");
      }

      // Set the content length token, important for body
      if (
        headerTokens[0].equals("Content-Length")
          && headerTokens[1].matches("^\\d+$")
      ) {
        this.contentLength = Integer.parseInt(headerTokens[1]);
      }

      this.headers.put(headerTokens[0], headerTokens[1]);
    }

    this.evaluatedHeaders = true;
  }

  /**
   * Does a preliminary parse and evaluation of the provided
   * body.
   * <p>
   * If the request does not have a valid content length, this
   * method will throw an exception. Otherwise, it will simply
   * append the body to this builder's body string.
   *
   * @param bodyInfo The string with this request's body.
   * @throws HttpSyntaxException if there is no
   *                             {@code Content-Length}
   *                             header.
   */
  private void evaluateBody(String bodyInfo) throws HttpSyntaxException {
    if (this.contentLength == -1) {
      this.failSyntax("Body exists but no Content-Length header found.");
    }

    this.body.append(bodyInfo);
  }

  /**
   * Returns whether the request being built is a completed
   * request yet.
   * <p>
   * A request is considered completed once the proper body is
   * sent (if a body was specified). If not, the request is
   * considered completed upon receival of a {@code CRLF}
   * indicating the end of the header section, as defined by
   * <a href= "https://tools.ietf.org/html/rfc7230">RFC
   * 7230.</a>
   * <p>
   * This method will first attempt to evaluate the headers of
   * the request as well as the body before determining if the
   * request is completed. This is done because the request
   * cannot be properly determined as completed until the
   * headers have been parsed and associated headers
   * extracted.
   * <p>
   * If this method returns {@code false} and
   * {@link #construct()} is called, {@link #construct()} will
   * throw an {@code HttpSyntaxException}.
   * <p>
   *
   * @return true if this request is a valid HTTP request.
   */
  public boolean hasCompletedRequest() {
    // First attempt evaluation if needed
    if (this.attemptEvaluation() == false) {
      return false;
    }

    if (this.contentLength > -1) {
      // If content length exist, make sure body is same length as
      // content
      if (this.body.length() >= this.contentLength) {
        return true;
      }
      return false;
    } else {
      // If the above isn't true then there is no body, we
      // are done
      return true;
    }
  }

  /**
   * Checks to see if the headers are evaluated. If they are
   * not, attempts to evaluate the header and body.
   *
   * @return true if the headers were or have been evaluated.
   */
  private boolean attemptEvaluation() {
    if (!this.evaluatedHeaders) {
      // Always evaluate properties if haven't evaluated
      try {
        this.evaluateProperties();
      } catch (HttpSyntaxException e) {
        return false;
      }

      // If they still haven't been properly evaluated, return
      // false as this request is still being built
      if (!this.evaluatedHeaders) {
        return false;
      }
    }

    return true;
  }

  /**
   * Determines if enough time has ellapsed to signal a
   * timeout.
   * <p>
   * Upon construction, the start time will be stored. The
   * start time can be changed with a call to
   * {@link #resetTimeoutStart()}. If the current time minus
   * the start time is greater than the limit set, the server
   * should timeout the client.
   * <p>
   * This is only important if the server implementation uses
   * {@link #hasCompletedRequest()} to check if the request is
   * not fully received. If the client sends a malformed
   * request that never fulfills the requirements of a "valid
   * request", the server connection may be locked in an
   * infinite loop. This method serves as a backup to ensure
   * that if a "proper request" isn't assembled in the time
   * span, the connection should time out.
   * <p>
   * The timeout is solely used as an indicator to the
   * implementing server that enough time has elapsed to
   * consider the request a malformed one, and respond
   * accordingly, and is not used in this class. For example,
   * a {@code RequestBuilder} will not expire on timeout, and
   * every method will be available and function as normal,
   * even if timed out.
   * <p>
   * Note that if a valid request has been assembled (but not
   * necessarily constructed), this method will still return
   * true if the duration from start to current is larger than
   * the timeout limit.
   *
   * @return true if the assembly has taken enough time that
   *         the connection should time out.
   */
  public boolean shouldTimeout() {
    return (System.currentTimeMillis()-this.startTime >= this.timeoutLimit);
  }

  /**
   * Restarts this timeout start time to the current time.
   */
  public void resetTimeoutStart() {
    this.startTime = System.currentTimeMillis();
  }

  /**
   * Attempts to construct the assembled request stored.
   * <p>
   * If the assembled request stored is not complete or the
   * http format is incorrect, this method will throw an
   * {@code HttpSyntaxException}.
   * <p>
   * If the headers and body have not been evaluated yet, they
   * will be evaluated in this method. If the request is not
   * complete, this method will throw an exception.
   * <p>
   * This method should only be called once an request is a
   * valid request according to
   * {@link #hasCompletedRequest()}.
   *
   * @return the constructed request object.
   * @throws HttpSyntaxException if the request is incomplete
   *                             or the syntax of the request
   *                             is incomplete or invalid.
   */
  public Request construct() throws HttpSyntaxException {
    if (!this.hasCompletedRequest()) {
      this.failSyntax("The request is incomplete.");
    }

    try {
      Request req =
        new Request(
          this.method,
          this.path,
          this.protocol,
          this.headers,
          this.body.toString()
        );
      return req;
    } catch (InvalidHeaderException e) {
      throw new HttpSyntaxException("A header was invalid", e);
    }
  }

  /**
   * Checks if the assembled request is currently empty or
   * not.
   *
   * @return true if there is currently no assembled request.
   */
  public boolean isEmpty() {
    return this.requestString.length() == 0;
  }

  /**
   * Throws an exception with the provided message.
   *
   * @param message The detail message for the exception.
   * @throws HttpSyntaxException always, according to the
   *                             message.
   */
  private void failSyntax(String message) throws HttpSyntaxException {
    throw new HttpSyntaxException(message);
  }
}
