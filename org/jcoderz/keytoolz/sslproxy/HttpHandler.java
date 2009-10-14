package org.jcoderz.keytoolz.sslproxy;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Handler class for the HTTP server.
 * @author cloroff
 * @since Jul 15, 2009
 * @version 1.0
 */
public class HttpHandler extends ProxyClient
{
  /** A default HTTP OK response. */
  private static final String OK = "HTTP/1.1 200 OK";

  /** A double carriage return line feed. */
  private static final String CR_LF_CR_LF = "\r\n\r\n";

  /** A temporary buffer for the incoming HTTP messages. */
  private ByteArrayOutputStream mServerbuffer = new ByteArrayOutputStream();
  
  /** The stream for the HTTP response. */
  private OutputStream mResponseStream;


  /**
   * Constructor setting some instance parameters.
   * @param handler The socket to handle the incoming connection
   * @param outProtocol The protocol to be used for the handler socket
   */
  public HttpHandler(Socket handler, String outProtocol)
  {
    super(handler, null, -1, outProtocol);
  }


  /**
   * Here the HTTP requests are handled and a response is generated.
   * @see java.lang.Thread#run()
   */
  public void run()
  {
    System.out.println("accepted new connection from " + mHandler.getInetAddress());

    try
    {
      int messageId = mCounter++;
      new ProxyPipe(messageId + "_client", mHandler.getInputStream(), mServerbuffer).start();
      mResponseStream = mHandler.getOutputStream();
    }
    catch (IOException ioe)
    {
      System.out.println("failed to establish " + " connection\n   from " + mHandler.getInetAddress());
      ioe.printStackTrace();
    }
    while (true)
    {
      byte[] temp = null;
      synchronized (mServerbuffer)
      {
        temp = mServerbuffer.toByteArray();
        mServerbuffer.reset();
      }
      if (temp.length > 0)
      {
        String message = new String(temp);
        System.out.println("received message:\n" + message);
        
        boolean closeConnection = false;
        int startPos = message.indexOf(CR_LF_CR_LF, 0);
        while (startPos > -1)
        {
          closeConnection = true;
          System.out.println("writing response");
          try
          {
            mResponseStream.write(OK.getBytes());
            mResponseStream.write(CR_LF_CR_LF.getBytes());
            mResponseStream.flush();
          }
          catch (IOException ioEx)
          {
            System.out.println("Problem while writing the response: " + ioEx.toString());
            ioEx.printStackTrace();
          }
          startPos = message.indexOf(CR_LF_CR_LF, startPos);
        }
        if (closeConnection)
        {
          try
          {
            mResponseStream.close();
          }
          catch (IOException ioEx)
          {
            System.out.println("Problem while closing the response stream: " + ioEx.toString());
            ioEx.printStackTrace();
          }
        }
      }
      else
      {
        try
        {
          sleep(100);
        }
        catch (InterruptedException ie)
        {
          // ignore
        }
      }
    }
  }
}
