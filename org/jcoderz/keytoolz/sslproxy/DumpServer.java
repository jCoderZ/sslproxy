package org.jcoderz.keytoolz.sslproxy;

import java.io.IOException;
import java.net.Socket;

/**
 * The multithreaded server class for the Dump server.
 *
 * @author cloroff
 * @since Jun 26, 2006
 * @version 1.0
 */
public class DumpServer extends ProxyServer
{
  /**
   * Constructor setting some instance parameters.
   * @param localPort  The local port to be used as server
   * @param inProtocol  The connection protocol
   */
  public DumpServer(int localPort, String inProtocol)
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
    System.out.println("starting " + mInProtocol.toUpperCase() + " dump server on port "
        + mLocalPort + "\nforwarding incoming " + "connections to /dev/null");
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
        Socket client = null;
        client = mServer.accept();
        new DumpHandler(client).start();
      }
    }
    catch (IOException ioEx)
    {
      ioEx.printStackTrace();
    }
  }
}
