package com.coldcore.coloradoftp.core;

/**
 * The core itself.
 *
 * Core is responsible for starting/stopping FTP server and all its parts.
 * There is nothing else the core can do because starting from version 1.2
 * all components run in separate threads and therefore execute on their own.
 *
 * Core has a special status names POISONED. This status means that the server is going to shut
 * down and is waiting for all control and data connections to finish and disconnect. All connection
 * pools and control connection acceptors must take core status into consideration.
 *
 * When poisoned and there are no connections left to serve, the server will not neccessarily stop.
 * This is up to the implementation of the core.
 *
 *
 * ColoradoFTP - The Open Source FTP Server (http://cftp.coldcore.com)
 */
public interface Core {

  /** Start server */
  public void start();


  /** Stop server */
  public void stop();


  /** Poison server (no more connections allowed and existing will be killed when all data is transferred) */
  public void poison();


  /** Get server status
   * @return Server status
   */
  public CoreStatus getStatus();
  
  /**
   * Gets the port on which the server is listening for control connections.
   * @return the control port.
   */
  public int getControlPort();
}
