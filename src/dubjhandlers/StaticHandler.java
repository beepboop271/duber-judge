package dubjhandlers;

import java.io.FileNotFoundException;
import java.io.IOException;

import webserver.Request;
import webserver.Response;
import webserver.RouteTarget;
import webserver.WebServer;

/**
 * The class that handles requests to static files and
 * contest, like js or css files.
 * <p>
 * Created <b> 2020-01-25 </b>.
 *
 * @since 0.0.7
 * @version 1.0.0
 * @author Joseph Wang
 */
public class StaticHandler implements RouteTarget {
  /**
   * {@inheritDoc}
   * <p>
   * This method serves static content and files.
   */
  public Response accept(Request req) {
    switch (req.getMethod()) {
      case "GET":
        return this.handleRetrievalRequest(req, true);
      case "HEAD":
        return this.handleRetrievalRequest(req, false);
      default:
        return Response.methodNotAllowed();
    }
  }

  /**
   * Properly handles a retrieval request for a resource.
   * <p>
   * If only the headers are desired, {@code hasBody} should
   * be set to false.
   *
   * @param req     The request to handle.
   * @param hasBody Whether the response should have a body or
   *                not.
   * @return the proper response for the request.
   */
  private Response handleRetrievalRequest(Request req, boolean hasBody) {
    String file = req.getEndResource();
    String mimeType = "";
    // Get the proper mime name
    switch (file.substring(file.lastIndexOf("."))) {
      case ".css":
        mimeType = "text/css";
        break;
      case ".js":
        mimeType = "application/javascript";
        break;
      case ".woff":
        mimeType = "font/woff";
        break;
      default:
        // Disallow any other kind of retrieval requests
        return Response.forbidden();
    }

    try {
      byte[] fileBytes;

      if (req.getPath().contains("/static/")) {
        fileBytes = WebServer.loadFile("."+req.getPath());
      } else {
        fileBytes = WebServer.loadFile("./static/"+req.getPath());
      }

      return Response.ok(fileBytes, mimeType, hasBody);
    } catch (FileNotFoundException e) {
      // The server could not locate the file, return not found.
      return Response.notFound();
    } catch (IOException e) {
      // Server died trying to open file, return internal error
      return Response.internalError();
    }
  }
}
