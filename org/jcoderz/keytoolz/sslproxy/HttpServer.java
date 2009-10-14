package org.jcoderz.keytoolz.sslproxy;

import java.io.IOException;
import java.net.Socket;

/**
 * The multithreaded server class for the HTTP server.
 *
 * @author cloroff
 * @since Jul 15, 2009
 * @version 1.0
 */
public class HttpServer extends ProxyServer
{
  /**
   * Constructor setting some instance parameters.
   * @param localPort  The local port to be used as server
   * @param inProtocol  The connection protocol
   */
  public HttpServer(int localPort, String inProtocol)
  {
    super(localPort, null, -1, inProtocol, null);
  }


  /**
   * The main thread of the multithreaded server. 
   * @see java.lang.Thread#run()
   */
  public void run()
  {
    System.out.println();
    System.out.println("starting " + mInProtocol.toUpperCase() + " HTTP server on port "
        + mLocalPort);
    System.out.println();

    try
    {
      if (mInProtocol.equals("tcp"))
      {
        startServer();
      }
      else
      {
        startSslServer();
      }

      while (true)
      {
        Socket handler = null;
        handler = mServer.accept();
        new HttpHandler(handler, mInProtocol).start();
      }
    }
    catch (IOException ioEx)
    {
      ioEx.printStackTrace();
    }
  }
}
