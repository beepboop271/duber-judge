package webserver;

import java.net.Socket;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.io.PrintWriter;
import java.io.IOException;

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
class ConnectionHandler implements Runnable {
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