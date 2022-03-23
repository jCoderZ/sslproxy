package org.jcoderz.keytoolz.sslproxy;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;

import org.jcoderz.commons.util.HexUtil;

/**
 * The pipe between the server and the client socket.
 * Two separate Threads are created for every incoming connection
 * to handle the input and output streams.
 *
 * @author Christian Loroff
 * @since June 7, 2006
 * @version 1.0
 */
public class ProxyPipe extends Thread
{
  /** The block size for SSL communications */
  private static final int SSL_BLOCK_SIZE = 16348;

  /** The InputStream for the communication */
  private InputStream mIs;

  /** The OutputStream for the communication */
  private OutputStream mOs;

  /** The prefix for log files */
  private String mLogPrefix;


  /**
   * Constructor setting some instance parameters.
   * @param logPrefix  The prefix for log file names
   * @param is         The InputStream for the communication
   * @param os         The OutputStream for the communication
   */
  public ProxyPipe(String logPrefix, InputStream is, OutputStream os)
  {
    mLogPrefix = logPrefix;
    mIs = is;
    mOs = os;
  }


  /**
   * The run method of the pipe Thread.
   * All available data is read from the InputStream and
   * immediately passed to the OutputStream, until a Socket
   * or Stream is closed from outside this class.
   * @see java.lang.Thread#run()
   */
  public void run()
  {
    boolean dumps = !"false".equalsIgnoreCase(System.getProperty("sslproxy.dumps"));
        
    FileOutputStream fosPlain = null;
    FileOutputStream fosHex = null;
    PrintWriter pw = null;
    try
    {
      if (dumps) {
      fosPlain = new FileOutputStream(mLogPrefix + "_plain_dump.log");
      fosHex = new FileOutputStream(mLogPrefix + "_hex_dump.log");
      pw = new PrintWriter(fosHex);
      }
      byte[] data = new byte[SSL_BLOCK_SIZE];
      while (true)
      {
        int readData = mIs.read(data);
        if (readData == -1)
        {
          System.out.println("connection terminated");
          break;
        }
        if (readData > 0)
        {
          if ("true".equalsIgnoreCase(System.getProperty("sslproxy.filter")))
          {
            String dataString = new String(data, 0, readData);
            int startPos = dataString.indexOf("Accept-Encoding:");
            int endPos = dataString.indexOf("\r\n", startPos);
            if (startPos > -1 && endPos > -1)
            {
                System.arraycopy(data, endPos, data, startPos + 16, readData - endPos);
                readData = readData - endPos + startPos + 16;;
            }
          }
          if (dumps)
          {
          byte[] dumpData = new byte[readData];
          System.arraycopy(data, 0, dumpData, 0, readData);
          pw.print(HexUtil.dump(dumpData));
          fosPlain.write(data, 0, readData);
          }
          mOs.write(data, 0, readData);
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
        mOs.flush();
        if (dumps)
        {
        pw.flush();
        fosHex.flush();
        fosPlain.flush();
      }
    }
    }
    catch (IOException ioEx)
    {
      System.out.println(ioEx.toString());
    }

    try
    {
      mOs.flush();
      if (dumps)
      {
      pw.flush();
      fosHex.flush();
      fosPlain.flush();
      pw.close();
      fosHex.close();
      fosPlain.close();
      }
    }
    catch (IOException ioEx)
    {
      ioEx.printStackTrace();
    }

    try
    {
      mIs.close();
    }
    catch (IOException ioEx)
    {
      ioEx.printStackTrace();
    }

    try
    {
      mOs.close();
    }
    catch (IOException ioEx)
    {
      ioEx.printStackTrace();
    }
  }
}
