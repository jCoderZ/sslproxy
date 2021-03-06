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
      else
      {
        sRemoteHost = InetAddress.getByName(args[1]);
        sRemotePort = Integer.parseInt(args[2]);
        sInProtocol = args[3];
        sOutProtocol = args[4];
        if (!sInProtocol.equals("tcp") && !sInProtocol.equals("ssl")
            || (!sOutProtocol.equals("tcp") && !sOutProtocol.equals("ssl")))
        {
          throw new Exception("invalid protocol specified");
        }
        server = new ProxyServer(sLocalPort, sRemoteHost, sRemotePort, sInProtocol, sOutProtocol);
      }
    }
    catch (ArrayIndexOutOfBoundsException aioobEx)
    {
      System.out.println("Not enough parameters entered.");
      usage();
      System.exit(1);
    }
    catch (Exception ex)
    {
      usage();
      ex.printStackTrace();
      System.exit(1);
    }
    server.start();
  }


  /**
   * Usage information for the command line parameters.
   */
  private static void usage()
  {
    System.out.println();
    System.out.println("Usage: ProxyServer <localPort> <remoteHost> <remotePort> <inProtocol> <outProtocol>");
    System.out.println("     | ProxyServer <localPort> <serverType> <inProtocol>");
    System.out.println();
    System.out.println("   <inProtocol>, <outProtocol> = tcp|ssl");
    System.out.println("   <serverType> = dump|http");
    System.out.println("   The property \"javax.net.ssl.trustStore\" can be used\n"
        + "   to specify a certain trust store that contains the\n"
        + "   necessary trusted certificates for the SSL connection.");
  }
}
