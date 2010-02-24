package com.coldcore.coloradoftp.connection.impl;

import com.coldcore.coloradoftp.connection.Connection;
import com.coldcore.coloradoftp.connection.TerminatedException;
import org.apache.log4j.Logger;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * @see com.coldcore.coloradoftp.connection.Connection
 *
 * Base class for all connections.
 */
abstract public class GenericConnection implements Connection {

  /** Target to run */
  protected interface Target {
    /** Get target name */
    public String getName();
    /** Do target action and rest */
    public void doTask() throws Exception;
  }


  /** Target runner */
  protected class Runner implements Runnable {

    protected Target target;

    public Runner(Target target) {
      this.target = target;
    }

    public void run() {
      try {

        while (!destroyed) {
          //Run the target
          target.doTask();
          Thread.yield();
        }

      } catch (TerminatedException e) {

        //Normal termination (exception has the message)
        log.debug(e.toString());
        try {
          destroy();
        } catch (Throwable ex) {}

      } catch (Throwable e) {

        //If something shut down the connection then this is no error
        if (!goingDown) {
          //Error termination
          log.error("Error in "+target.getName()+" thread", e);
          try {
            destroy();
          } catch (Throwable ex) {}
        }

      }
      log.debug(target.getName()+" thread finished");
    }
  }


  /** Data reader thread */
  protected class Reader implements Target {
    public String getName() {
      return "Reader";
    }
    public void doTask() throws Exception {
      read();
    }
  }

  /** Data writer thread */
  protected class Writer implements Target {
    public String getName() {
      return "Writer";
    }
    public void doTask() throws Exception {
      write();
    }
  }


  private static Logger log = Logger.getLogger(GenericConnection.class);
  protected SocketChannel sc;
  protected boolean poisoned;
  protected boolean destroyed;
  protected long bytesWrote;
  protected long bytesRead;
  protected ByteBuffer rbuffer;
  protected ByteBuffer wbuffer;
  protected long sleep;
  protected boolean goingDown;


  public GenericConnection() {
    sleep = 100L;
  }


  public SocketChannel getSocketChannel() {
    return sc;
  }


  public synchronized void initialize(SocketChannel channel) {
    if (sc != null) {
      log.warn("Attempt to re-initialize an initialized connection");
      throw new IllegalStateException("Already initialized");
    }

    sc = channel;
  }


  /** Start reader thread */
  protected void startReaderThread() {
    Thread runnerThread = new Thread(new Runner(new Reader()));
    runnerThread.setDaemon(true);
    runnerThread.start();
  }


  /** Start writer thread */
  protected void startWriterThread() {
    Thread writerThread = new Thread(new Runner(new Writer()));
    writerThread.setDaemon(true);
    writerThread.start();
  }


  public synchronized void destroy() {
    goingDown = true;
    if (destroyed) return;

    //Close the channel
    try {
      sc.close();
    } catch (Throwable e) {
      log.error("Cannot close the channel (ignoring)", e);
    }

    destroyed = true;
    log.debug("Connection destroyed");
  }


  public void poison() {
    poisoned = true;
    log.debug("Connection poisoned");
  }


  public boolean isDestroyed() {
    return destroyed;
  }


  public boolean isPoisoned() {
    return poisoned;
  }


  public long getBytesWrote() {
    return bytesWrote;
  }


  public long getBytesRead() {
    return bytesRead;
  }


  /** Read data from user routine */
  abstract protected void read() throws Exception;


  /** Write data to user routine */
  abstract protected void write() throws Exception;


  abstract public void service() throws Exception;


  /** Get thread sleep time
   * @return Time in mills
   */
  public long getSleep() {
    return sleep;
  }


  /** Set thread sleep time
   * @param sleep Time in mills
   */
  public void setSleep(long sleep) {
    this.sleep = sleep;
  }
}
