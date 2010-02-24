package com.coldcore.coloradoftp.connection;

/**
 * Initiates a data connection with user.
 *
 * This class establishes a new connection to user's machine as a result of a PORT command.
 * This class is a part of a control connection and is created on a per user basis.
 *
 * When a user wants a server to establish a data connection with his/her machine, this class
 * is executed by a control connection untill a data connection is established or failed.
 * In the latter case it is a responsibility of this class to send a failed reply to the user
 * (this will also clear the INTERRUPT state of the control connection).
 * Otherwise, if data connection is established, this class must add it to a connection pool
 * and set control connection's reference to the new data connection.
 *
 * This class can establish connection with user only after user gets "150" reply from the
 * control connection and not before. Data port listener and data connection do not have to
 * check for "150" reply, but this class has to.
 *
 * The easiest way to test this condition is:
 * 1. Before producing "150" relpy, a command saves into a user session amount of bytes
 *    the control connection has sent to the user so far (byte marker)
 * 2. This class must wait until byte marker apperas in the session and the control
 *    connection writes beyond it
 * 3. When control connection clears its outgoing buffer the "150" reply is sent to the user
 * NB! This class may or may not remove byte marker from the session later, so it is the
 *     responsibility of a file command to clear the byte marker before initiating a new
 *     data connection to make sure that this class will wait for a proper "150" reply.
 *
 *
 * ColoradoFTP - The Open Source FTP Server (http://cftp.coldcore.com)
 */
public interface DataConnectionInitiator {

  /** Set IP address to connect to
   * @param ip User's IP address
   */
  public void setIp(String ip);


  /** Get IP address to connect to
   * @return User's IP address
   */
  public String getIp();


  /** Set port number to connect to
   * @param port User's port number
   */
  public void setPort(int port);


  /** Get port number to connect to
   * @return User's port number
   */
  public int getPort();


  /** Test if data connection initiator is active and should be executed by a control connection.
   * @return TRUE if it is active, FALSE otherwise
   */
  public boolean isActive();


  /** Activate (start connecting) */
  public void activate();


  /** Abort data connection initializer (if active then stop connection attempts and send failed reply).
   * This method has nothong to do with ABOR command and reply must not be to ABOR command.
   */
  public void abort();


  /** Get control connection of this data connection initiator
   * @return Control connection
   */
  public ControlConnection getControlConnection();


  /** Set control connection of this control connection initiator
   * @param controlConnection Control connection
   */
  public void setControlConnection(ControlConnection controlConnection);
}
