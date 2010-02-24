package com.coldcore.coloradoftp.connection;

import java.io.IOException;

/**
 * Accepts incoming control connections.
 *
 * When connection is accepted on a predefined port, this class configures it,
 * writes a welcome/reject message into it and adds it to a connection pool which will
 * serve it till the connection dies.
 *
 * Server should not just drop a connection leaving user with no response message. If a
 * connection must be dropped, it should be poisoned instead, this way the connection
 * writes out the message to its user and then dies.
 *
 * This class must be aware of core's POISONED status. When core is poisoned it is the
 * responsibility of a connector to not to allow any more incoming connections, so server
 * can shut down normally.
 *
 *
 * ColoradoFTP - The Open Source FTP Server (http://cftp.coldcore.com)
 */
public interface ControlConnector {

  /** Bing to the port and start listening for incoming connections */
  public void bind() throws IOException;


  /** Unbind from the port and stop listening for incoming connections */
  public void unbind() throws IOException;


  /** Test if connector is bound to the port.
   * @return TRUE if it is bound, FALSE otherwise
   */
  public boolean isBound();


  /** Set binding port
   * @param port Port number
   */
  public void setPort(int port);


  /** Get binding port
   * @return Port number
   */
  public int getPort();
}
