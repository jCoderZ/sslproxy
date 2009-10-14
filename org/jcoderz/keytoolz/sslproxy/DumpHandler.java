package org.jcoderz.keytoolz.sslproxy;

import java.io.IOException;
import java.net.Socket;

/**
 * Handler class for the Dump server.
 *
 * @author cloroff
 * @since Jun 26, 2006
 * @version 1.0
 */
public class DumpHandler extends ProxyClient
{
  /**
   * Constructor setting some instance parameters.
   * @param handler The socket to handle the incoming connection
   * @param outProtocol The protocol to be used for the handler socket
   */
  public DumpHandler(Socket handler)
  {
    super(handler, null, -1, null);
  }


  /**
   * The run method of the client Thread.
   * Here the connection to the remote host is created and the SSL
   * handshake is performed.
   */
  public void run()
  {
    System.out.println("accepted new connection from " + mHandler.getInetAddress());

    try
    {
      int messageId = mCounter++;
      new ProxyPipe(messageId + "_client", mHandler.getInputStream(), System.out).start();
    }
    catch (IOException ioEx)
    {
      System.out.println("failed to establish " + " connection\n   from "
          + mHandler.getInetAddress());
      ioEx.printStackTrace();
    }
  }

}
