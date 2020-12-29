package webserver;

public interface RouteTarget {
  public Response accept(Request req);
}
