package com.coldcore.coloradoftp.connection.impl;

import com.coldcore.coloradoftp.command.Reply;
import com.coldcore.coloradoftp.connection.*;
import com.coldcore.coloradoftp.factory.ObjectFactory;
import com.coldcore.coloradoftp.factory.ObjectName;
import com.coldcore.coloradoftp.session.Session;
import com.coldcore.coloradoftp.session.SessionAttributeName;
import org.apache.log4j.Logger;

import java.nio.ByteBuffer;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.channels.Channel;

/**
 * @see com.coldcore.coloradoftp.connection.DataConnection
 */
public class GenericDataConnection extends GenericConnection implements DataConnection {

  private static Logger log = Logger.getLogger(GenericDataConnection.class);
  protected ControlConnection controlConnection;
  protected ReadableByteChannel rbc;
  protected WritableByteChannel wbc;
  protected DataConnectionMode mode;
  protected String filename;
  protected boolean userAborted;
  protected boolean successful;
  protected boolean skipReply;
  protected DataConnectionCallback callback;


  public GenericDataConnection(int bufferSize) {
    super();

    //rbuffer = ByteBuffer.allocateDirect(bufferSize);
    rbuffer = ByteBuffer.allocate(bufferSize);
    rbuffer.flip();
  }


  /** Read data from user */
  protected void read() throws Exception {
    /* We will read data from the user and write it into the channel until the user
     * disconnects. There is no way to check if a complete file has been uploaded,
     * so we assume that every transfer is a success.
     */

    //Read data from user into the buffer if the buffer is empty
    if (!rbuffer.hasRemaining()) {
      rbuffer.clear();
      int i = sc.read(rbuffer); //Thread blocks here...
      rbuffer.flip();

      //Client disconnected?
      if (i == -1) {
        successful = true;
        throw new TransferCompleteException();
      }

      bytesRead += i;
      log.debug("Read from socket "+i+" bytes (total "+bytesRead+")");
    }

    //Forward the data into the channel
    wbc.write(rbuffer);
  }


  /** Write data to user */
  protected void write() throws Exception {
    /* We wiil read data from the channel and write it to the user until the
     * channel is empty (successful transfer). If user disconnects earlier than
     * all data is transferred then the transfer has failed.
     */

    //Read the data from the channel into the buffer if the buffer is empty
    if (!rbuffer.hasRemaining()) {
      rbuffer.clear();
      int i = rbc.read(rbuffer);
      rbuffer.flip();

      //File done?
      if (i == -1) {
        successful = true;
        throw new TransferCompleteException();
      }
    }

    //Forward the data to the user
    int i = sc.write(rbuffer); //Thread blocks here...

    //Client disconnected?
    if (i == -1) throw new TransferAbortedException();

    bytesWrote += i;
    log.debug("Wrote into socket "+i+" bytes (total "+bytesWrote+")");
  }


  /** Activate the connection if not active yet */
  protected void activate() {
    /* The connection will start to function as soon as it gets MODE and CHANNEL from
     * user session (we must get CHANNEL last as it starts read/write routines).
     * Those attributes then have to be removed or the next data connection will use them as well.
     * There is also a FILENAME attribute for file operations.
     */

    if (rbc != null || wbc != null) return;

    if (mode == null) {
      Session session = controlConnection.getSession();
      mode = (DataConnectionMode) session.getAttribute(SessionAttributeName.DATA_CONNECTION_MODE);
      if (mode != null) {
        log.debug("Mode extracted from user session");
      }
    }

    //Mode first
    if (mode == null) return;

    if (filename == null) {
      Session session = controlConnection.getSession();
      filename = (String) session.getAttribute(SessionAttributeName.DATA_CONNECTION_FILENAME);
      if (filename != null) {
        log.debug("Filename extracted from user session");
      }
    }

    //Filename second
    if (mode != DataConnectionMode.LIST && filename == null) return;

    //Channel third (also start an appropriate thread)
    if (rbc == null && wbc == null) {
      Session session = controlConnection.getSession();
      if (mode == DataConnectionMode.LIST || mode == DataConnectionMode.RETR) {
        rbc = (ReadableByteChannel) session.getAttribute(SessionAttributeName.DATA_CONNECTION_CHANNEL);
        startWriterThread(); //To write data to user
      } else {
        wbc = (WritableByteChannel) session.getAttribute(SessionAttributeName.DATA_CONNECTION_CHANNEL);
        startReaderThread(); //To read data from user
      }
      if (rbc != null || wbc != null) {
        log.debug("Channel extracted from user session (data transfer begins)");
      }
    }
  }


  public void service() throws Exception {
    //User aborted the transfer
    if (userAborted) throw new TransferAbortedException();

    //Try to activate the data transfer
    activate();
  }


  /** Close data channel */
  protected void closeDataChannel() {
    Session session = controlConnection.getSession();
    Channel odc = (Channel) session.getAttribute(SessionAttributeName.DATA_CONNECTION_CHANNEL);
    session.removeAttribute(SessionAttributeName.DATA_CONNECTION_FILENAME);
    try {
      if (odc != null) odc.close();
    } catch (Throwable e) {
      log.error("Error closing data channel (ignoring)", e);
    }
  }


  /** Send reply to user upon connection termination */
  protected void reply() {
    try {
      //Transfer aborted by user - send "426" and then "226"
      if (userAborted) {
        Reply reply = (Reply) ObjectFactory.getObject(ObjectName.REPLY);
        reply.setCode("426");
        reply.setText("Connection closed, transfer aborted.");
        controlConnection.reply(reply);

        reply = (Reply) ObjectFactory.getObject(ObjectName.REPLY);
        reply.setCode("226");
        reply.setText("Abort command successful.");
        controlConnection.reply(reply);

        log.debug("User aborted data transfer");
        return;
      }

      //Transfer failed
      if (!successful) {
        Reply reply = (Reply) ObjectFactory.getObject(ObjectName.REPLY);
        reply.setCode("426");
        reply.setText("Connection closed, transfer aborted.");
        controlConnection.reply(reply);

        log.debug("Data transfer failed");
        return;
      }

      //Transfer OK (note that STOU has a different code)
      Reply reply = (Reply) ObjectFactory.getObject(ObjectName.REPLY);
      if (mode == DataConnectionMode.STOU) reply.setCode("250");
      else reply.setCode("226");

      if (mode == DataConnectionMode.LIST) {
        reply.setText("Transfer completed.");
      } else {
        //Encode double-quated in the filename
        String encf = filename.replaceAll("\"", "\"\"");
        reply.setText("Transfer completed for \""+encf+"\".");
      }
      controlConnection.reply(reply);

      log.debug("Data transfer successful");

    } catch (Throwable e) {
      log.error("Error sending completion reply (ignoring)", e);
    }
  }



  public synchronized void destroy() {
    if (controlConnection != null) {
      closeDataChannel();

      //Hook for post-upload/download logic via a callback
      if (!skipReply && callback != null)
        try {
          if (successful) callback.onTransferComplete(this);
          else callback.onTransferAbort(this);
        } catch (Throwable e) {
          log.error("Callback error (ignoring)", e);
        }

      //When data transfer finishes, a reply must be send to a user
      if (!skipReply) reply();

      //Clear the attributes to prevent misuse by future instances
      Session session = controlConnection.getSession();
      session.removeAttribute(SessionAttributeName.DATA_CONNECTION_MODE);
      session.removeAttribute(SessionAttributeName.DATA_CONNECTION_CHANNEL);

      //Clear control connection reference
      controlConnection.setDataConnection(null);
    } else {
      log.warn("No control connection in the destroy method");
    }

    super.destroy();
  }


  public void destroyNoReply() {
    skipReply = true;
    destroy();
  }


  public void abort() {
    userAborted = true;
  }


  public ControlConnection getControlConnection() {
    return controlConnection;
  }


  public void setControlConnection(ControlConnection controlConnection) {
    this.controlConnection = controlConnection;
  }


  public DataConnectionCallback getDataConnectionCallback() {
    return callback;
  }


  public void setDataConnectionCallback(DataConnectionCallback callback) {
    this.callback = callback;
  }
}
