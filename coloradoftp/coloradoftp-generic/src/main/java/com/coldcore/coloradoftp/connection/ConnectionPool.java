package com.coldcore.coloradoftp.connection;

import java.util.Set;

/**
 * Pool of active connections.
 *
 * All connections should be added to connection pools. Connection pools process connections'
 * self-service routines. If a connection fails to be processed (e.g. throws an exception),
 * the pool must call connection's destroy method and then removes it from its list.
 *
 * Connection pools are not allowed to process destroyed connections but may add or return them
 * in appropriate methods.
 *
 * This class must be aware of core's POISONED status. When core is poisoned it is the
 * responsibility of a connection pool to poison all connections in it, so they will die
 * soon and server will shut down.
 *
 *
 * ColoradoFTP - The Open Source FTP Server (http://cftp.coldcore.com)
 */
public interface ConnectionPool {

  /** Add a connection to the pool
   * @param connection Connection
   */
  public void add(Connection connection);


  /** Remove a connection from the pool
   * @param connection Connection
   */
  public void remove(Connection connection);


  /** Pool size
   * @return Number of connection in the pool
   */
  public int size();


  /** Destroy the pool and kill all connections */
  public void destroy();


  /** Initialize the pool */
  public void initialize() throws Exception;


  /** List all connections (must return a copy and not the original set)
   * @return Copy of the original set of connections
   */
  public Set<Connection> list();
}
