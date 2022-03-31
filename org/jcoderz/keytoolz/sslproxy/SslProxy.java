package org.jcoderz.keytoolz.sslproxy;

import java.net.InetAddress;

/**
 * The start class of the SSL Proxy.
 *
 * @author Christian Loroff
 * @since Jun 7, 2006
 * @version 1.0
 */
public class SslProxy
{
  /** The local port of the server. */
  static int sLocalPort = -1;
  
  /** The destination host. */
  static InetAddress sRemoteHost = null;
  
  /** The destination port. */
  static int sRemotePort = -1;

  /** The tunnel/proxy host. */
  static InetAddress sTransferHost = null;

  /** The tunnel/proxy port. */
  static int sTransferPort = -1;

  /** The incoming protocol. */
  static String sInProtocol = null;

  /** The outgoing protocol. */
  static String sOutProtocol = null;


  /**
   * This is the entry point of the SSL proxy
   * @param  args       The command line parameters
   * @throws Exception  All Exceptions that occur while creating and opening
   *                    the ServerSocket will be passed to the VM.
   */
  public static void main(String[] args) throws Exception
  {
    ProxyServer server = null;

    try
    {
      sLocalPort = Integer.parseInt(args[0]);
      if (args[1].equals("dump"))
      {
        sInProtocol = args[2];
        server = new DumpServer(sLocalPort, sInProtocol);
      }
      else if (args[1].equals("http"))
      {
        sInProtocol = args[2];
        server = new HttpServer(sLocalPort, sInProtocol);
      }
      else if (args[1].equals("proxy"))
      {
        server = getProxyServer(args, false);
      }
      else if (args[1].equals("tunnel"))
      {
        server = getProxyServer(args, true);
      }
      else
      {
        server = getProxyServer(args);
      }
    }
    catch (ArrayIndexOutOfBoundsException aioobEx)
    {
      System.out.println("Not enough parameters entered.");
      usage(args);
      System.exit(1);
    }
    catch (Exception ex)
    {
      usage(args);
      ex.printStackTrace();
      System.exit(1);
    }
    server.start();
  }

  private static ProxyServer getProxyServer(String[] args) throws Exception {
    sRemoteHost = InetAddress.getByName(args[1]);
    sRemotePort = Integer.parseInt(args[2]);
    sInProtocol = args[3];
    sOutProtocol = args[4];
    if (!sInProtocol.equals("tcp") && !sInProtocol.equals("ssl")
        || (!sOutProtocol.equals("tcp") && !sOutProtocol.equals("ssl")))
    {
      throw new Exception("invalid protocol specified");
    }
    return new ProxyServer(sLocalPort, sRemoteHost, sRemotePort, sInProtocol, sOutProtocol);
  }

  private static ProxyServer getProxyServer(String[] args, boolean useTunnel) throws Exception {
    sRemoteHost = InetAddress.getByName(args[2]);
    sRemotePort = Integer.parseInt(args[3]);
    sTransferHost = InetAddress.getByName(args[4]);
    sTransferPort = Integer.parseInt(args[5]);
    sInProtocol = args[6];
    sOutProtocol = args[7];
    if (!sInProtocol.equals("tcp") && !sInProtocol.equals("ssl")
            || (!sOutProtocol.equals("tcp") && !sOutProtocol.equals("ssl")))
    {
      throw new Exception("invalid protocol specified");
    }
    return new ProxyServer(useTunnel, sLocalPort, sRemoteHost, sRemotePort, sTransferHost, sTransferPort, sInProtocol, sOutProtocol);
  }


  /**
   * Usage information for the command line parameters.
   */
  private static void usage(String[] args)
  {
	System.out.println("Call arguments:");
	for (int i = 0; i < args.length; i++)
	{
	  System.out.print(args[i] + " ");
	}
    System.out.println();
    System.out.println();
    System.out.println("Usage: ProxyServer <localPort> <remoteHost> <remotePort> <inProtocol> <outProtocol>");
    System.out.println("     | ProxyServer <localPort> proxy <remoteHost> <remotePort> <proxyHost> <proxyPort> <inProtocol> <outProtocol>");
    System.out.println("     | ProxyServer <localPort> tunnel <remoteHost> <remotePort> <tunnelHost> <tunnelPort> <inProtocol> <outProtocol>");
    System.out.println("     | ProxyServer <localPort> <serverType> <inProtocol>");
    System.out.println();
    System.out.println("   <inProtocol>, <outProtocol> = tcp|ssl");
    System.out.println("   <serverType> = dump|http");
    System.out.println("   The property \"javax.net.ssl.trustStore\" can be used\n"
        + "   to specify a certain trust store that contains the\n"
        + "   necessary trusted certificates for the SSL connection.");
  }
}
