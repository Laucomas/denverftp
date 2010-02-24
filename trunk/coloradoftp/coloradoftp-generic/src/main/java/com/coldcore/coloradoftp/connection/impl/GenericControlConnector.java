package com.coldcore.coloradoftp.connection.impl;

import com.coldcore.coloradoftp.command.Command;
import com.coldcore.coloradoftp.command.CommandProcessor;
import com.coldcore.coloradoftp.connection.ConnectionPool;
import com.coldcore.coloradoftp.connection.ControlConnection;
import com.coldcore.coloradoftp.connection.ControlConnector;
import com.coldcore.coloradoftp.core.Core;
import com.coldcore.coloradoftp.core.CoreStatus;
import com.coldcore.coloradoftp.factory.ObjectFactory;
import com.coldcore.coloradoftp.factory.ObjectName;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;

/**
 * @see com.coldcore.coloradoftp.connection.ControlConnector
 */
public class GenericControlConnector implements ControlConnector, Runnable {

  private static Logger log = Logger.getLogger(GenericControlConnector.class);
  protected ServerSocketChannel ssc;
  protected int port;
  protected boolean bound;
  protected Core core;
  protected CommandProcessor commandProcessor;
  protected ConnectionPool controlConnectionPool;
  protected Thread thr;
  protected long sleep;


  public GenericControlConnector() {
    port = 21;
    sleep = 100L;
  }


  /** Configure connection before adding it to a pool
   * @param connection Connection
   */
  public void configure(ControlConnection connection) {
    if (core.getStatus() == CoreStatus.POISONED) {
      //Server is shutting down, reply and poison the connection
      Command command = (Command) ObjectFactory.getObject(ObjectName.COMMAND_POISONED);
      command.setConnection(connection);
      commandProcessor.execute(command);
      connection.poison();
    } else {
      //Server is ready, reply so
      Command command = (Command) ObjectFactory.getObject(ObjectName.COMMAND_WELCOME);
      command.setConnection(connection);
      commandProcessor.execute(command);
    }
  }


  public synchronized void bind() throws IOException {
    if (bound) {
      log.warn("Connector on port "+port+" was bound when bind routine was submitted");
      throw new IllegalStateException("Unbind the connector on port "+port+" first");
    }

    //Get required objects
    core = (Core) ObjectFactory.getObject(ObjectName.CORE);
    commandProcessor = (CommandProcessor) ObjectFactory.getObject(ObjectName.COMMAND_PROCESSOR);
    controlConnectionPool = (ConnectionPool) ObjectFactory.getObject(ObjectName.CONTROL_CONNECTION_POOL);

    //Bind to the port
    ssc = ServerSocketChannel.open();
    ssc.socket().bind(new InetSocketAddress(port));
    bound = true;

    //Start this class
    thr = new Thread(this);
    thr.setDaemon(true);
    thr.start();

    log.info("Connector is bound to port "+port);
  }


  public synchronized void unbind() throws IOException {
    if (!bound) {
      log.warn("Connector on port "+port+" was not bound when unbind routine was submitted");
      throw new IllegalStateException("Cannot unbind the connector on port "+port+", it is not bound");
    }

    //Set this now to prevent run() method from flooding errors after the socket is closed
    bound = false;
    
    //Unbind from the port
    if (ssc.isOpen()) ssc.close();

    //Wait for this class to stop (just in case)
    try {
      thr.join(30000);
    } catch (Throwable e) {}

    log.info("Connector on port "+port+" is unbound");
  }


  public boolean isBound() {
    return bound;
  }


  public void run() {
    while (bound) {

      ControlConnection connection = null;
      SocketChannel sc = null;
      try {
        sc = ssc.accept(); //Thread blocks here...
        String ip = sc.socket().getInetAddress().getHostAddress();
        log.debug("New control connection accepted (IP "+ip+")");

        //Create new connection instance and initialize it
        connection = (ControlConnection) ObjectFactory.getObject(ObjectName.CONTROL_CONNECTION);
        connection.initialize(sc);

        //Configure the control connection and add to pool
        configure(connection);
        controlConnectionPool.add(connection);
        log.debug("New control connection is ready");

        Thread.sleep(sleep);

      } catch (Throwable e) {
        if (bound) log.warn("Failed to accept a connection (ignoring)", e);
        try {
          connection.destroy();
        } catch (Throwable ex) {}
        try {
          sc.close();
        } catch (Throwable ex) {}
      }

    }
    log.debug("Control connector thread finished");
  }


  public void setPort(int port) {
    if (port < 1) throw new IllegalArgumentException("Invalid port");
    this.port = port;
  }


  public int getPort() {
    return port;
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
}
