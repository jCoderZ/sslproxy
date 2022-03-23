package org.jcoderz.keytoolz.sslproxy;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * The client class of the proxy.
 * It creates the connection to the desired server and performs the SSL handshake if necessary.
 *
 * @author Christian Loroff
 * @since June 7, 2006
 * @version 1.0
 */
public class ProxyClient extends Thread
{
  /** The message counter */
  public static int mCounter = 0;

  /** The handler socket of the incoming connection */
  protected Socket mHandler;

  /** The remote host to be contacted */
  protected InetAddress mRemoteHost;

  /** The remote port to be contacted */
  protected int mRemotePort;

  /** Flag for indicating if a tunnel is used. */
  protected boolean mUseTunnel = false;

  /** The tunnel host. */
  protected InetAddress mTunnelHost = null;

  /** The tunnel port. */
  protected int mTunnelPort = -1;

  /** The outgoing protocol */
  protected String mOutProtocol;


  /**
   * Constructor setting some instance parameters.
   * @param handler      The socket of the incoming connection
   * @param remoteHost   The remote host to be contacted
   * @param remotePort   The remote port to be contacted
   * @param outProtocol  The protocol of the outgoing connection
   */
  public ProxyClient(Socket handler, InetAddress remoteHost, int remotePort, String outProtocol)
  {
    mHandler = handler;
    mRemoteHost = remoteHost;
    mRemotePort = remotePort;
    mOutProtocol = outProtocol;
  }

  /**
   * Constructor setting some instance parameters.
   * @param handler      The socket of the incoming connection
   * @param remoteHost   The remote host to be contacted
   * @param remotePort   The remote port to be contacted
   * @param outProtocol  The protocol of the outgoing connection
   */
  public ProxyClient(Socket handler, InetAddress remoteHost, int remotePort, InetAddress tunnelHost, int tunnelPort,
                     String outProtocol)
  {
    this(handler, remoteHost, remotePort, outProtocol);
    mTunnelHost = tunnelHost;
    mTunnelPort = tunnelPort;
    mUseTunnel = true;
  }

  /**
   * The run method of the client Thread.
   * Here the connection to the remote host is created and the SSL handshake is performed.
   * @see java.lang.Thread#run()
   */
  public void run()
  {
    System.out.println("accepted new connection from " + mHandler.getInetAddress());

    try
    {
      Socket proxyClient;
      if (mOutProtocol.equals("ssl"))
      {
        proxyClient = startSslClient();
      }
      else
      {
        proxyClient = new Socket(mRemoteHost, mRemotePort);
      }

      int messageId = mCounter++;
      System.out.println("established " + mOutProtocol + " connection for "
          + mHandler.getInetAddress());
      new ProxyPipe(messageId + "_client", mHandler.getInputStream(), proxyClient.getOutputStream())
          .start();
      new ProxyPipe(messageId + "_server", proxyClient.getInputStream(), mHandler.getOutputStream())
          .start();
    }
    catch (IOException ioe)
    {
      System.out.println("failed to establish " + mOutProtocol + " connection\n   from "
          + mHandler.getInetAddress() + " to " + mRemoteHost + ":" + mRemotePort);
      if (mUseTunnel)
      {
        System.out.println("   through the tunnel " + mTunnelHost + ":" + mTunnelPort);
      }
      ioe.printStackTrace();
    }
  }


  /**
   * Creates an SSL connection with the remote host.
   * @return              The client socket of the SSL connection
   * @throws IOException  If the connection can not be created
   */
  protected Socket startSslClient() throws IOException
  {
    SSLSocketFactory factory = (SSLSocketFactory) SSLSocketFactory.getDefault();
    SSLSocket sslSocket;
    if (mUseTunnel)
    {
      Socket tunnelSocket = new Socket(mTunnelHost, mTunnelPort);
      sslSocket = (SSLSocket) factory.createSocket(tunnelSocket, mRemoteHost.getHostName(), mRemotePort, true);
    }
    else
    {
      sslSocket = (SSLSocket) factory.createSocket(mRemoteHost, mRemotePort);
    }
    sslSocket.startHandshake();
    return sslSocket;
  }
}
