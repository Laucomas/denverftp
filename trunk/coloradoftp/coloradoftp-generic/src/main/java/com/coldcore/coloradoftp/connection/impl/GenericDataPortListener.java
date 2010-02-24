package com.coldcore.coloradoftp.connection.impl;

import com.coldcore.coloradoftp.command.Reply;
import com.coldcore.coloradoftp.connection.ConnectionPool;
import com.coldcore.coloradoftp.connection.ControlConnection;
import com.coldcore.coloradoftp.connection.DataConnection;
import com.coldcore.coloradoftp.connection.DataPortListener;
import com.coldcore.coloradoftp.factory.ObjectFactory;
import com.coldcore.coloradoftp.factory.ObjectName;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;

/**
 * @see com.coldcore.coloradoftp.connection.DataPortListener
 */
public class GenericDataPortListener implements DataPortListener, Runnable {

  private static Logger log = Logger.getLogger(GenericDataPortListener.class);
  protected int port;
  protected boolean bound;
  protected ServerSocketChannel ssc;
  protected Map<String, ControlConnection> awaiting;
  protected ConnectionPool dataConnectionPool;
  protected Reply errorReply;
  protected Thread thr;
  protected long sleep;


  public GenericDataPortListener() {
    port = -1;
    sleep = 100L;
    awaiting = new HashMap<String,ControlConnection>();
  }


  protected Reply getErrorReply() {
    if (errorReply == null) {
      errorReply = (Reply) ObjectFactory.getObject(ObjectName.REPLY);
      errorReply.setCode("425");
      errorReply.setText("Can't open data connection.");
    }
    return errorReply;
  }


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


  public void setPort(int port) {
    this.port = port;
  }


  public int getPort() {
    return port;
  }


  public synchronized void bind() throws IOException {
    if (port < 1) throw new IllegalArgumentException("Set correct port first");

    if (bound) {
      log.warn("Listener on port "+port+" was bound when bind routine was submitted");
      throw new IllegalStateException("Unbind the listener on port "+port+" first");
    }

    //Get required objects
    dataConnectionPool = (ConnectionPool) ObjectFactory.getObject(ObjectName.DATA_CONNECTION_POOL);

    //Bind to the port
    ssc = ServerSocketChannel.open();
    ssc.socket().bind(new InetSocketAddress(port));

    //Start this class
    thr = new Thread(this);
    thr.setDaemon(true);
    thr.start();

    bound = true;
    log.debug("Listener is bound to port "+port);
  }


  public synchronized void unbind() throws IOException {
    if (!bound) {
      log.warn("Listener on port "+port+" was not bound when unbind routine was submitted");
      throw new IllegalStateException("Cannot unbind the listener on port "+port+", it is not bound");
    }

    //Set this now to prevent run() method from flooding errors after the socket is closed
    bound = false;

    //Remove all awaiting connections
    synchronized (awaiting) {
      for (ControlConnection connection : awaiting.values())
        if (!connection.isDestroyed()) removeConnection(connection);
    }

    //Unbind from the port
    if (ssc.isOpen()) ssc.close();

    log.debug("Listener on port "+port+" is unbound");
  }


  public boolean isBound() {
    return bound;
  }


  public boolean addConnection(ControlConnection connection) {
    if (!bound) return false;

    //Clean up the map from unnecessary control connections
    cleanup();

    //Add a new one
    String ip = connection.getSocketChannel().socket().getInetAddress().getHostAddress();
    synchronized (awaiting) {
      ControlConnection con = awaiting.get(ip);
      if (con != null && con != connection) return false;
      awaiting.put(ip, connection);
      return true;
    }
  }


  public boolean removeConnection(ControlConnection connection) {
    String ip = connection.getSocketChannel().socket().getInetAddress().getHostAddress();
    synchronized (awaiting) {
      ControlConnection c = awaiting.get(ip);
      if (c == connection) {
        awaiting.remove(ip);
        c.reply(getErrorReply());
        return true;
      }
      return false;
    }
  }


  public void run() {
    while (bound) {

      ControlConnection controlConnection = null;
      DataConnection dataConnection = null;
      SocketChannel sc = null;
      try {
        sc = ssc.accept(); //Thread blocks here...
        String ip = sc.socket().getInetAddress().getHostAddress();
        log.debug("New incoming data connection (from "+ip+" on port "+port+")");

        //Create new connection instance
        dataConnection = (DataConnection) ObjectFactory.getObject(ObjectName.DATA_CONNECTION);
        dataConnection.initialize(sc);

        //Locate a control connection waiting for this data connection
        controlConnection = popControlConnection(dataConnection);
        if (controlConnection == null) {
          log.warn("No control connection found for an incoming data connection (from "+ip+" on port "+port+")");
          dataConnection.destroyNoReply();
        } else {

          //If there is a data connection already then kill it
          DataConnection existing = controlConnection.getDataConnection();
          if (existing != null && !existing.isDestroyed()) {
            log.warn("BUG: Replacing existing data connection with a new one!");
            existing.destroyNoReply();
          }

          //Configure the data connection and wire it with the control connection and add to the pool
          controlConnection.setDataConnection(dataConnection);
          dataConnection.setControlConnection(controlConnection);
          configure(dataConnection);
          dataConnectionPool.add(dataConnection);
          log.debug("New data connection is ready");
        }

        Thread.sleep(sleep);

      } catch (Throwable e) {
        if (bound) log.warn("Failed to accept a connection (ignoring)", e);
        try {
          dataConnection.destroyNoReply();
        } catch (Throwable ex) {}
        try {
          sc.close();
        } catch (Throwable ex) {
          if (bound) log.error("Cannot close the channel (ignoring)", e);
        }

        //Send error reply
        if (controlConnection != null) controlConnection.reply(getErrorReply());
      }

    }
    log.debug("Data port listener thread finished");
  }


  /** Cleas up the map from connections which should not be in it */
  protected void cleanup() {
    synchronized (awaiting) {
      for (String ip : awaiting.keySet()) {
        ControlConnection connection = awaiting.get(ip);
        if (connection.isDestroyed()) awaiting.remove(ip);
      }
    }
  }


  /** Locate a control connection which awaits for a data connection and remove it
   * @param dataConnection Incoming data connection
   * @return Control connection or NULL if a control connection cannot be located and the data connection should be dropped
   */
  protected ControlConnection popControlConnection(DataConnection dataConnection) {
    String dip = dataConnection.getSocketChannel().socket().getInetAddress().getHostAddress();
    synchronized (awaiting) {
      for (String ip : awaiting.keySet()) {
        if (ip.equals(dip)) {
          ControlConnection controlConnection = awaiting.remove(ip);
          return controlConnection.isDestroyed() ? null : controlConnection;
        }
      }
    }
    return null;
  }


  /** Configure connection before adding it to a pool
   * @param connection Connection
   */
  public void configure(DataConnection connection) {
  }
}
