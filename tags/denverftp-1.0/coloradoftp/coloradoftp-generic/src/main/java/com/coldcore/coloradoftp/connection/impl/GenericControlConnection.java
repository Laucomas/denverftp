package com.coldcore.coloradoftp.connection.impl;

import com.coldcore.coloradoftp.command.Command;
import com.coldcore.coloradoftp.command.CommandFactory;
import com.coldcore.coloradoftp.command.CommandProcessor;
import com.coldcore.coloradoftp.command.Reply;
import com.coldcore.coloradoftp.connection.*;
import com.coldcore.coloradoftp.factory.ObjectFactory;
import com.coldcore.coloradoftp.factory.ObjectName;
import com.coldcore.coloradoftp.session.Session;
import com.coldcore.coloradoftp.session.SessionAttributeName;
import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.channels.Channel;

/**
 * @see com.coldcore.coloradoftp.connection.Connection
 *
 * Control connections usualy do not eat much network traffic (that is a job of
 * data connections), so there is no need to control network overhead for this type
 * of connection. Control connection always have lower priority than data connections
 * and their execution does not take place on every round of lyfe cycle thread.
 */
public class GenericControlConnection extends GenericConnection implements ControlConnection {

  private static Logger log = Logger.getLogger(GenericControlConnection.class);
  protected ByteArrayOutputStream warray;
  protected ByteArrayOutputStream rarray;
  protected boolean rarrayComplete;
  protected StringBuffer incomingBuffer;
  protected StringBuffer outgoingBuffer;
  protected boolean interruptState;
  protected CommandProcessor commandProcessor;
  protected CommandFactory commandFactory;
  protected Session session;
  protected DataConnection dataConnection;
  protected DataConnectionInitiator dataConnectionInitiator;
  protected boolean utf8;

  public static final String CHARSET_UTF8 = "UTF-8";
  public static final String CHARSET_ASCII = "US-ASCII";

  public static final char UTF8_MAGIC_NUMBER = (char)65279;


  public GenericControlConnection(int bufferSize) {
    super();

    utf8 = true;

    incomingBuffer = new StringBuffer();
    outgoingBuffer = new StringBuffer();

    rbuffer = ByteBuffer.allocate(bufferSize);
    rbuffer.flip();

    wbuffer = ByteBuffer.allocate(bufferSize);
    wbuffer.flip();

    warray = new ByteArrayOutputStream();
    rarray = new ByteArrayOutputStream();
  }


  public synchronized void initialize(SocketChannel channel) {
    super.initialize(channel);

    //ObjectFactory cannot be used in a constructor, so we create the rest of the objects here
    commandProcessor = (CommandProcessor) ObjectFactory.getObject(ObjectName.COMMAND_PROCESSOR);
    commandFactory = (CommandFactory) ObjectFactory.getObject(ObjectName.COMMAND_FACTORY);
    session = (Session) ObjectFactory.getObject(ObjectName.SESSION);
    dataConnectionInitiator = (DataConnectionInitiator) ObjectFactory.getObject(ObjectName.DATA_CONNECTION_INITIATOR);
    dataConnectionInitiator.setControlConnection(this);

    startReaderThread();
    startWriterThread();
  }


  /** Try to flush the content of the read array to the incoming buffer.
   * @return TRUE is flushed, FALSE otherwise
   */
  protected boolean flushReadArray() throws Exception {
    if (!rarrayComplete) return false;

    //Decode and feed collected bytes to the incoming buffer (also remove the UTF-8 magic number if present)
    String s = new String(rarray.toByteArray(), utf8 ? CHARSET_UTF8 : CHARSET_ASCII);
    char[] carr = s.toCharArray();
    synchronized (incomingBuffer) {
      for (char c : carr)
        if (c != UTF8_MAGIC_NUMBER) incomingBuffer.append(c);
    }

    //Reset the read array
    rarray.reset();
    rarrayComplete = false;

    return true;
  }


  /** Decode and write the next part of user's input to the incoming buffer.
   * Used by the "read" routine to receive UFT-8 bytes.
   * @param arr Byte array containing the next part of user's input
   * @param stopIndex User's input stops at this index in the byte array
   */
  protected void pushIncomingBuffer(byte[] arr, int stopIndex) throws Exception {
    /* We will feed user input to the read array one byte at a time till we hit any 1-byte character.
     * Only then the bytes in the read array may be safely decoded to UTF-8 (where one char may be
     * represented by 2-3 bytes) and written to the incoming buffer. For performance we will not flush
     * the read array every time we encounter 1-byte character, but we will do so before any 2-3 bytes
     * UTF-8 character and in the end if possible (rarrayComplete). If the read array cannot be decoded
     * then the bytes in it will wait till the next "read" routine.
     */
    for (int z = 0; z < stopIndex; z++) {
      byte b = arr[z];

      //x00-x7F is a 1-byte character (UTF-8/ASCII), otherwise the character takes 2-3 bytes
      if (b >= 0 && b <= 127) {
        //Add the byte to the read array and proceed (read array may now be decoded)
        rarray.write(b);
        rarrayComplete = true;
      } else {
        //Try to flush the read array then add the byte to it and proceed
        flushReadArray();
        rarray.write(b);
      }
    }

    //Try to flush the read array one more time in the end
    flushReadArray();
  }


  protected void read() throws Exception {
    /* We must not read anything if:
     * 1. There is some data in outgoing buffer waiting to be send to the user
     * 2. User did not receive a welcome message yet and it is not yet in the outgoing buffer
     * 3. Connection is poisoned
     */
    if (getOutgoingBufferSize() > 0 || bytesWrote == 0 || poisoned) {
      Thread.sleep(sleep);
      return;
    }

    //Read data from socket and append it to the incoming buffer.
    rbuffer.clear();
    int i = sc.read(rbuffer); //Thread blocks here...

    //Client disconnected?
    if (i == -1) throw new BrokenPipeException();

    bytesRead += i;
    log.debug("Read from socket "+i+" bytes (total "+bytesRead+")");

    //This will add user input to the incoming buffer decoded with proper charset
    byte[] barr = rbuffer.array();
    pushIncomingBuffer(barr, i);

    //Execute commands waiting in the buffer
    executeCommands();
  }


  /** Encode and return the next part of server's response from the the outgoing buffer.
   * Used by the "write" routine to send UFT-8 bytes.
   * @param maxBytes Byte array size limit
   * @return Encoded byte array not longer than the limit or NULL if there is nothing to write
   */
  protected byte[] popOutgoingBuffer(int maxBytes) throws Exception {
    byte[] barr;
    if (warray.size() > 0) {

      //Reminder from the last output is still pending
      barr = warray.toByteArray();
      warray.reset();

    } else {

      //Get a string from the outgoing buffer to write into socket
      String str;
      int end = maxBytes;
      synchronized (outgoingBuffer) {
        //Correct sub-string length (if current length is longer than available data size)
        if (end > outgoingBuffer.length()) end = outgoingBuffer.length();
        if (end == 0) return null; //Nothing to write
        str = outgoingBuffer.substring(0, end);
        //Remove this string from the outgoing buffer
        outgoingBuffer.delete(0, end);
      }

      //Convert to byte array, the length of the byte array may be greater than the string length (UTF-8 encoding)
      barr = str.getBytes(utf8 ? CHARSET_UTF8 : CHARSET_ASCII);

    }

    //Will the new array fit into the buffer?
    if (barr.length > maxBytes) {
      warray.write(barr, maxBytes, barr.length-maxBytes); //This will not fit into the socket buffer, save it for the next "write"
      byte[] trg = new byte[maxBytes];
      System.arraycopy(barr,0,trg,0,maxBytes);
      return trg;
    } else {
      return barr;
    }
  }


  protected void write() throws Exception {

    //Read more data from the outgoing buffer into the buffer only if the buffer is empty
    if (!wbuffer.hasRemaining()) {

      //This will get server response from the outgoing buffer encoded with proper charset
      int cap = wbuffer.capacity();
      byte[] barr = popOutgoingBuffer(cap);

      //Nothing to write?
      if (barr == null) {
        Thread.sleep(sleep);
        return;
      }

      //Write out to the buffer
      wbuffer.clear();
      wbuffer.put(barr);
      wbuffer.flip();
    }

    //Forward the data to the user
    int i = sc.write(wbuffer); //Thread blocks here...

    //Client disconnected?
    if (i == -1) throw new BrokenPipeException();

    bytesWrote += i;
    log.debug("Wrote into socket "+i+" bytes (total "+bytesWrote+")");
  }


  public void service() throws Exception {
    /* If connection has been poisoned then we can destroy it only when it writes all data out.
     * We cannot kill it while it has an active data connection.
     * We cannot kill if it did not write a welcome message yet.
     * We cannot kill if it is expecting a reply to be added soon (poisoned byte marker)
     */
    if (poisoned) {
      boolean kill = true;

      if (dataConnection != null && !dataConnection.isDestroyed()) kill = false; //Active data connection

      if (getOutgoingBufferSize() > 0) kill = false; //Data is waiting to be sent

      if (bytesWrote == 0) kill = false; //Welcome message is expected

      Long markerBytesWrote = (Long) session.getAttribute(SessionAttributeName.BYTE_MARKER_POISONED);
      if (markerBytesWrote != null && bytesWrote <= markerBytesWrote) kill = false; //A reply is expected

      if (kill) throw new PoisonedException();
    }
  }


  /** Execute commands waiting in the incoming buffer */
  protected void executeCommands() throws Exception {
    while (true) {
      Command command = getNextCommand();
      if (command == null) break;
      commandProcessor.execute(command);
    }
  }


  /** Reads next user command from the incoming buffer
   * @return Command or NULL if it's not ready yet
   */
  protected Command getNextCommand() throws Exception {
    //Extract the next command from buffer
    String input;
    synchronized (incomingBuffer) {
      int i = incomingBuffer.indexOf("\r\n");
      if (i == -1) return null;                    //Nothing to extraxt yet (the line is not finished)
      input = incomingBuffer.substring(0, i);
      incomingBuffer.delete(0, i+2);               //Also delete \r\n in the end of the command
      if (input.trim().length() == 0) return null; //This is an empty string, skip it
      log.debug("Extracted user input: "+input);
    }

    Command command = commandFactory.create(input);
    command.setConnection(this);

    //If INTERRUPT state is set then ignore all but special FTP commands (same for the poisoned).
    if (interruptState && !command.processInInterruptState()) {
      log.debug("Execution of the command is not allowed while the connection is in INTERRUPT state (dropping command)");
      return null;
    }
    if (poisoned && !command.processInInterruptState()) {
      log.debug("Execution of the command is not allowed while the connection is poisoned (dropping command)");
      return null;
    }

    return command;
  }


  public synchronized void reply(Reply reply) {
    //Prepare reply and write it out
    String prepared = reply.prepare();
    synchronized (outgoingBuffer) {
      outgoingBuffer.append(prepared);
    }
    log.debug("Prepared reply: "+prepared.trim());

    /* Change "interrupt" state: if code starts with "1" then set it, otherwise unset.
     * FTP spec: all codes that start with 1 demand client to wait for another reply.
     */
    if (reply.getCode().startsWith("1")) {
      interruptState = true;
      log.debug("Reply has triggered INTERRUPT state");
    }
    else
    if (interruptState) {
      //Check if reply's command can clear INTERRUPT state, otherwise leave the state as it is
      Command command  = reply.getCommand();
      if (command == null || command.canClearInterruptState()) {
        interruptState = false;
        log.debug("Reply has cleared INTERRUPT state");
      }
    }
  }


  public Session getSession() {
    return session;
  }


  public DataConnection getDataConnection() {
    return dataConnection;
  }


  public void setDataConnection(DataConnection dataConnection) {
    this.dataConnection = dataConnection;
  }


  public synchronized void destroy() {
    //Abort data connection initiator if active
    if (dataConnectionInitiator.isActive()) dataConnectionInitiator.abort();

    //Destory data connection if exists
    if (dataConnection != null) dataConnection.destroy();

    //Test if there is data channel left in the session
    closeSessionDataChannel();

    super.destroy();
  }


  /** Close a data channel if exists in the session */
  protected void closeSessionDataChannel() {
    Channel odc = (Channel) session.getAttribute(SessionAttributeName.DATA_CONNECTION_CHANNEL);
    if (odc != null) {
      log.debug("Attempting to close data channel in session");
      session.removeAttribute(SessionAttributeName.DATA_CONNECTION_CHANNEL);
      try {
        odc.close();
      } catch (Throwable e) {
        log.error("Error closing data channel (ignoring)", e);
      }
    }
  }


  public DataConnectionInitiator getDataConnectionInitiator() {
    return dataConnectionInitiator;
  }


  public int getOutgoingBufferSize() {
    synchronized (outgoingBuffer) {
      return outgoingBuffer.length();
    }
  }


  public int getIncomingBufferSize() {
    synchronized (incomingBuffer) {
      return incomingBuffer.length();
    }
  }


  public boolean isUtf8() {
    return utf8;
  }


  public void setUtf8(boolean utf8) {
    this.utf8 = utf8;
  }
}
