package webserver;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.HashMap;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;

import dubjhandlers.ProblemHandler;
import dubjhandlers.LeaderboardHandler;
public class WebServer {
  /** The port that this WebServer is hosted on. **/
  private int port;
  /** Whether the server is currently running or not. */
  private boolean running = true;

  /** The {@code ServerSocket} associated with this web server. */
  private ServerSocket sock;
  /** The routes that this WebServer has. */
  private HashMap<String, RouteTarget> routes;
  /**
   * The thread pool, to execute connections without needing to allocate a thread
   * to each connection.
   */
  private ExecutorService workers;

  public static void main(String[] args) {
    WebServer server = new WebServer(5000);
    server.route("/problems", new ProblemHandler());
    server.route("/leaderboard", new LeaderboardHandler());
    server.run();
  }

  /**
   * Constructs a new WebServer.
   * <p>
   * To actually start running the web server after constructing it, call
   * {@link #run()}.
   * 
   * @param port the port to host this web server on.
   */
  public WebServer(int port) {
    this.workers = Executors.newCachedThreadPool();
    this.routes = new HashMap<>();
    this.port = port;
  }

  /**
   * Begins running this web server, allowing it to accept and respond to
   * connections.
   */
  public void run() {
    try {
      this.sock = new ServerSocket(port);
      Socket client;

      while (running) {
        client = this.sock.accept();
        this.workers.execute(new ConnectionHandler(client));
      }

    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Shuts down this web server.
   * <p>
   * Any existing task remaining in the task queue will be discarded. No new tasks
   * will be accepted, and closing of the socket will be attempted.
   */
  public void shutdown() {
    this.running = false;
    // dispose of all tasks, and shuts down all workers
    this.workers.shutdownNow();

    try {
      this.sock.close();
    } catch (IOException e) {
      System.out.println("Could not shut down server socket!");
    }
  }

  /**
   * Adds a new routing to a {@code RouteTarget}.
   * 
   * @param route  the route associated with the {@code RouteTarget}.
   * @param target the RouteTarget to handle the request.
   * @see RouteTarget
   */
  public void route(String route, RouteTarget target) {
    this.routes.put(route, target);
  }

  /**
   * A connection handler designed to handle a connection from a specified client.
   * <p>
   * This class is designed to work alongside a webserver, and will manage all io
   * with the connected client (expected to be a browser of some sort). More
   * specifically, it will read in the client's HTTP request and return an HTTP
   * response to the client.
   * <p>
   * Created <b>2021-01-08</b>.
   * 
   * @since 0.0.1
   * @version 0.0.1
   * @author Joseph Wang
   */
  private class ConnectionHandler implements Runnable {
    /** The client to handle. */
    private Socket client;
    /** The input stream from the client. */
    private BufferedReader input;
    /** The output stream for the client. */
    private PrintWriter output;

    /**
     * Constructs a new ConnectionHandler to handle a specific client.
     * 
     * @param client the client to handle.
     */
    public ConnectionHandler(Socket client) {
      this.client = client;

      try {
        this.input = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
        this.output = new PrintWriter(this.client.getOutputStream());
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Starts this ConnectionHandler and begins handling a specific client.
     */
    public void run() {
    }
  }
}
