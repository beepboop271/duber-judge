package webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

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
 * occur.
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
 * 7230.</a>
 *
 * @since 0.0.4
 * @version 0.0.4
 * @author Joseph Wang
 * @see Request
 */
public class RequestBuilder {
  /**
   * The default amount of time to wait before timeout, in ms.
   */
  public static final int TIMEOUT_TIME_MS = 10_000;

  /** The string with the request. */
  private StringBuilder requestString;
  /** When this RequestBuilder was constructed. */
  private long startTime;
  /** The amount of time to wait before timeout, in ms. */
  private int timeoutLimit;

  /**
   * The content length specified in the header, if present.
   */
  private int contentLength = -1;
  /**
   * The current length of the body, used alongside
   * {@link #contentLength}.
   */
  private int currentLength = -1;

  /** Whether the request body is encoded in chunks or not. */
  private boolean chunked = false;
  /**
   * If the body chunks have ended, used alongside
   * {@link #chunked}.
   */
  private boolean chunkedEnd = false;
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
    this.startTime = System.currentTimeMillis();
    this.timeoutLimit = timeoutInSeconds;
  }

  /**
   * Appends a string as it is to the currently
   * in-construction request.
   *
   * @param seq The string to add.
   */
  public void append(String seq) {
    this.requestString.append(seq);

    if (seq.equals("\r\n")) {
      if (!this.evaluatedHeaders) {
        // Once we get the first CRLF to indicate end of header we
        // need to parse headers to get the correct body
        this.evaluateHeaders();
      } else if (this.chunked) {
        // End of a chunked body is indicated by another CRLF
        this.chunkedEnd = true;
      }
    } else if (contentLength > 0) {
      this.currentLength += seq.length();
    }
  }

  /**
   * Does a preliminary parse and evaluation of the headers to
   * determine the kind of body this request will contain.
   * <p>
   * Locating either the {@code Content-Length} header or
   * {@code Transfer-Encoding} with value {@code chunked}
   * indicate that this request should have a body (as
   * specified in <a href=
   * "https://tools.ietf.org/html/rfc7230#section-3.3">Section
   * 3.3 of RFC 7230</a>), and should be taken into account
   * when determining if the request is complete or not.
   * <p>
   * These headers are not checked for validity (eg. not empty
   * string, etc) and are not stored. They will be reparsed
   * once {@link #construct()} is called due to
   * {@link #construct()} implementation.
   */
  private void evaluateHeaders() {
    String req = this.requestString.toString();
    BufferedReader requestReader = new BufferedReader(new StringReader(req));

    try {
      String headerString = requestReader.readLine();
      while (headerString != null) {
        String[] header = headerString.split(": ?");

        if (header.length == 2) {
          if (
            header[0].equals("Content-Length") && header[1].matches("^\\d+$")
          ) {
            this.contentLength = Integer.parseInt(header[1]);
          } else if (
            header[0].equals("Transfer-Encoding")
              && header[1].contains("chunked")
          ) {
            this.chunked = true;
          }
        }

        headerString = requestReader.readLine();
      }

      this.evaluatedHeaders = true;

    } catch (IOException e) {
      System.out.println("An error occured while reading the headers.");
    }
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
   * If this method returns {@code false} and
   * {@link #construct()} is called, {@link #construct()} will
   * throw an {@code HttpSyntaxException}.
   * <p>
   *
   * @return true if this request is a valid HTTP request.
   */
  public boolean hasCompletedRequest() {
    if (this.evaluatedHeaders) {
      if (chunked) {
        return chunkedEnd;
      } else if (contentLength > 0) {
        if (this.currentLength >= contentLength) {
          return true;
        }
      }
      // If neither of the above is true then there is no body, we
      // are done
      return true;
    }

    return false;
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
   * This method should only be called once an request has
   * been assembled through {@link #append(String)} and is a
   * valid request according to
   * {@link #hasCompletedRequest()}.
   *
   * @return the constructed request object.
   * @throws HttpSyntaxException if the request syntax of the
   *                             constructed request is
   *                             incomplete or invalid.
   */
  public Request construct() throws HttpSyntaxException {
    if (!this.hasCompletedRequest()) {
      throw new HttpSyntaxException("Request incomplete.");
    }

    // Initialize the request and the reader
    String req = this.requestString.toString();
    BufferedReader requestReader = new BufferedReader(new StringReader(req));

    try {
      // Get status string
      String statusString = requestReader.readLine();
      String[] statusTokens = statusString.split(" ");
      if (statusTokens.length != 3) {
        throw new HttpSyntaxException("Status string invalid.");
      }

      // Parse and add status details
      String method = statusTokens[0];
      String path = statusTokens[1];
      String protocol = statusTokens[2];
      Request newRequest = new Request(method, path, protocol);

      // Add headers
      String header = requestReader.readLine();
      // Stop when we hit the empty line
      while (header.length() > 0) {
        try {
          newRequest.addHeader(header);
          header = requestReader.readLine();
        } catch (InvalidHeaderException e) {
          throw new HttpSyntaxException("Malformed header.", e);
        }
      }

      // Add the body, accounting for new lines
      String body = requestReader.readLine();
      StringBuilder bodyString = new StringBuilder("");
      while (body != null) {
        bodyString.append(body);
      }
      newRequest.body = bodyString.toString();

      return newRequest;
    } catch (IOException e) {
      // This catch should never happen
      System.out.println("An error occured while reading the request.");
      return null;
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
}
