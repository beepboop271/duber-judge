package webserver;

import java.util.Map;

/**
 * This class represents an HTTP {@code Response} object.
 * <p>
 * Since the web server is expected to return a properly formatted HTTP response
 * object, this class overrides and implements {@link #toString()} which will
 * convert this object to a properly formatted HTTP response string, ready to be
 * sent to the client.
 * <p>
 * Created <b> 2020-12-28 </b>
 * 
 * @since 0.0.1
 * @version 0.0.1
 * @author Joseph Wang
 * @see <a href=
 *      "https://developer.mozilla.org/en-US/docs/Web/HTTP/Messages#HTTP_Responses">More
 *      information about HTTP Responses (Mozilla)</a>
 */
public class Response extends HttpMessage {
  /** The status of the response. */
  private String statusString;

  /**
   * Constructs a new Response, without any body or headers.
   * <p>
   * The body will be initialized as an empty string.
   * 
   * @param statusCode the status code of the response (eg. 201, 404, etc)
   */
  public Response(int statusCode) {
    this(statusCode, "");
  }

  /**
   * Constructs a new Response, without any headers.
   * 
   * @param statusCode the status code of the response (eg. 201, 404, etc)
   * @param body       the body of the response.
   */
  public Response(int statusCode, String body) {
    super();

    this.statusString = "HTTP/1.1 " + statusCode;
    this.body = body;
  }

  /**
   * Constructs a new Response with the specified map of headers.
   * 
   * @param statusCode the status code of the response (eg. 201, 404, etc)
   * @param headers    a map with the headers of the response.
   * @param body       the body of the response.
   */
  public Response(int statusCode, Map<String, String> headers, String body) {
    super(headers);

    this.statusString = "HTTP/1.1 " + statusCode;
    this.body = body;
  }

  /**
   * Constructs a new Response with the specified string array of headers.
   * <p>
   * This constructor will accept a string array of headers, seperated by a colon.
   * <p>
   * For example: {@code ["Connection: Keep-Alive", "Accept-Language: en-us"]}
   * will be parsed into
   * {@code Connection: "Keep-Alive", Accept-Language: "en-us"}.
   * 
   * @param statusCode the status code of the response (eg. 201, 404, etc)
   * @param headers    a string array with the headers of the response.
   * @param body       the body of the response.
   */
  public Response(int statusCode, String[] headers, String body) {
    super(headers);

    this.statusString = "HTTP/1.1 " + statusCode;
    this.body = body;
  }

  /**
   * Constructs a new Response with the specified map of headers and no body.
   * 
   * @param statusCode the status code of the response (eg. 201, 404, etc)
   * @param headers    a map with the headers of the response.
   */
  public Response(int statusCode, Map<String, String> headers) {
    this(statusCode, headers, "");
  }

  /**
   * Constructs a new Response with the specified string array of headers and no
   * body.
   * <p>
   * This constructor will accept a string array of headers, seperated by a colon.
   * <p>
   * For example: {@code ["Connection: Keep-Alive", "Accept-Language: en-us"]}
   * will be parsed into
   * {@code Connection: "Keep-Alive", Accept-Language: "en-us"}.
   * 
   * @param statusCode the status code of the response (eg. 201, 404, etc)
   * @param headers    a string array with the headers of the response.
   */
  public Response(int statusCode, String[] headers) {
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
   * This method will return a string that is formatted and ready for output back
   * to a client.
   */
  public String toString() {
    String responseString = this.statusString + "\n";
    responseString += this.getHeadersString() + "\n";

    if (this.body != "") {
      responseString += this.getBody();
    }

    return responseString;
  }
}
