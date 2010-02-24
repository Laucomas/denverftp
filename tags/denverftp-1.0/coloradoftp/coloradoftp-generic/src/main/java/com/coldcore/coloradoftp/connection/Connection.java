package com.coldcore.coloradoftp.connection;

import java.nio.channels.SocketChannel;

/**
 * Data or control connection.
 * Connections read and write data from and to users.
 *
 * Life cycle of a connection is very simple. When it is accepted and configured it is then
 * added to a connection pool which calls on it service method. When connection dies, the
 * connection pool removes it from its internal list and the connection will be garbage
 * collected.
 *
 * When poisoned, connection must die as soon as it has nothing to read/write to/from user.
 * This is mainly for control connections as data connections do not use it. But since
 * connection pool's specification requires it to poison all connections as soon as server's
 * core becomes poisoned (before shutdown to let everyone finish and leave), the methods are
 * located in this class.
 *
 *
 * ColoradoFTP - The Open Source FTP Server (http://cftp.coldcore.com)
 */
public interface Connection {

  /** Kill connection (free resources) */
  public void destroy();


  /** Get connection's channel
   * @return Channel
   */
  public SocketChannel getSocketChannel();


  /** Initialize connection (acquire resources)
   * @param channel Channel
   */
  public void initialize(SocketChannel channel);


  /** Test if connection is destroyed
   * @return TRUE if destroyed, FALSE otherwise
   */
  public boolean isDestroyed();


  /** Poison connection (when there is nothing more to read/write it must die,
   * data connections output/input data and die, control connections are not allowed to
   * read user input and die when all output is done)
   */
  public void poison();


  /** Test if connection is poisoned (will be destroyed shortly)
   * @return TRUE if poisoned, FALSE otherwise
   */
  public boolean isPoisoned();


  /** Get bytes user sent so far
   * @return Bytes count
   */
  public long getBytesRead();


  /** Get bytes server sent so far
   * @return Bytes count
   */
  public long getBytesWrote();


  /** Self-service routine */
  public void service() throws Exception;
}
