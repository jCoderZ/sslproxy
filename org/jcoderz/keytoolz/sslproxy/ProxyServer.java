package org.jcoderz.keytoolz.sslproxy;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

/**
 * The multithreaded proxy server class.
 *
 * @author Christian Loroff
 * @since June 7, 2006
 * @version 1.0
 */
public class ProxyServer extends Thread
{
  /** The local port of the server. */
  protected int mLocalPort = -1;
  
  /** The destination host. */
  protected InetAddress mRemoteHost = null;
  
  /** The destination port. */
  protected int mRemotePort = -1;
  
  /** The incoming protocol. */
  protected String mInProtocol = null;
  
  /** The outgoing protocol. */
  protected String mOutProtocol = null;
  
  /** The socket of the multithreaded server. */
  protected ServerSocket mServer = null;


  /**
   * Constructor setting some instance parameters.
   * @param remoteHost  The remote host to be contacted
   * @param remotePort  The remote port to be contacted
   */
  public ProxyServer(int localPort, InetAddress remoteHost, int remotePort, String inProtocol,
      String outProtocol)
  {
    mLocalPort = localPort;
    mRemoteHost = remoteHost;
    mRemotePort = remotePort;
    mInProtocol = inProtocol;
    mOutProtocol = outProtocol;
  }


  /**
   * The start method for the TCP server.
   * @throws IOException  If the TCP server can not be started
   */
  protected void startServer() throws IOException
  {
    mServer = new ServerSocket(mLocalPort);
  }


  /**
   * The start method for the SSL server.
   * @throws IOException  If the SSL server can not be started
   */
  protected void startSslServer() throws IOException
  {
    SSLServerSocketFactory factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
    mServer = factory.createServerSocket(mLocalPort);
    SSLServerSocket sslServer = (SSLServerSocket) mServer;
    for (int i = 0; i < sslServer.getSupportedProtocols().length; i++)
    {
      System.out.println("supported server protocol: " + sslServer.getSupportedProtocols()[i]);
    }
    for (int i = 0; i < sslServer.getEnabledProtocols().length; i++)
    {
      System.out.println("enabled server protocol: " + sslServer.getEnabledProtocols()[i]);
    }
    sslServer.setNeedClientAuth(false);
    sslServer.setWantClientAuth(false);
  }


  /**
   * The main thread of the multithreaded server. 
   * @see java.lang.Thread#run()
   */
  public void run()
  {
    System.out.println();
    System.out.println("starting " + mInProtocol.toUpperCase() + " proxy on port " + mLocalPort
        + "\nforwarding incoming " + "connections to " + mRemoteHost + ":" + mRemotePort
        + " using " + mOutProtocol.toUpperCase());
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
        Socket handler = mServer.accept();
        new ProxyClient(handler, mRemoteHost, mRemotePort, mOutProtocol).start();
      }
    }
    catch (IOException ioEx)
    {
      System.out.println("Warning: IOException (" + ioEx.toString() + ") occured, normally it can be ignored.");
    }
  }
}
