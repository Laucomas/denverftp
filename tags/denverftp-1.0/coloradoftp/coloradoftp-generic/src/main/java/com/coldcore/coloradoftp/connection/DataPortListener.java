package com.coldcore.coloradoftp.connection;

import java.io.IOException;

/**
 * Listens for incoing data connections.
 *
 * This class is constantly listening on a predefined port. When user is using PASV command, his/her
 * control connection is added to the list in this class. User then initiates a data connection to
 * the port this class listens to. When connection is accepted, this class makes sure that an incoming
 * data connection corresponds to any of the control connections in the list (by IP address). If such
 * control connection is found in the list the new data connection is assigned to it and the control
 * connection is removed from the list.
 *
 * In order to work correctly the internal list must not contain control connections from the same host.
 * FTP server must open several ports (about 100) to allow PASV to work correctly, so
 * that control connections from the same host should not wait for one another.
 *
 * The less ports are open the more insecure FTP server gets. Malitious users can try to bing to all
 * available data ports and if one of them has a connection with the same IP that is waiting, the file
 * will be handed to the user (this is the case when all users are using the same gateway, every
 * connection will then have gateway's IP address).
 *
 * If user tries to open a data connecion which fails for some reason then this class has to send a failed
 * reply to the user (this will also clear the INTERRUPT state of the control connection).
 *
 * This class has to remove destroyed connections from the awaiting list itself. It is not allowed to
 * operate on destroyed connections.
 *
 *
 * ColoradoFTP - The Open Source FTP Server (http://cftp.coldcore.com)
 */
public interface DataPortListener {

  /** Set binding port
   * @param port Port number
   */
  public void setPort(int port);


  /** Get binding port
   * @return Port number
   */
  public int getPort();


  /** Bing to the port */
  public void bind() throws IOException;


  /** Unbind from the port */
  public void unbind() throws IOException;


  /** Test if connector is bound to the port.
   * @return TRUE if it is bound, FALSE otherwise
   */
  public boolean isBound();


  /** Add a connection to the list of connections which await incoming data connection from users
   * @param connection Connection
   * @return TRUE if connection was added to the list, FALSE if connection cannot be added (perhaps the same IP is already there)
   */
  public boolean addConnection(ControlConnection connection);


  /** Remove connection from the list if it exists in it and send a failed reply to the user
   * @param connection Connection
   * @return TRUE if connection was removed from the list, FALSE if connection was not found
   */
  public boolean removeConnection(ControlConnection connection);
}
