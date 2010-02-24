package com.coldcore.coloradoftp.connection;

import com.coldcore.coloradoftp.command.Reply;
import com.coldcore.coloradoftp.session.Session;

/**
 * Control connection.
 *
 * When user connects to a server a control connection is created and assigned to him/her.
 * One user may have only one control connection which lives until user disconnects.
 * Control connection accepts user commands, executes them and sends replies.
 *
 * Control connection acts as a main point for all users' operations, every user is
 * identified by a control connection. As a result, control connection has references
 * to all user-related objects and vice versa.
 *
 * Note that a user may also have a single data connection. Which can be created and destroyed
 * as many times as required while the control connection lives on.
 * Control connection has a reference to a data connection initiator which is activated
 * when there is a need to establish a new data connection.
 *
 * Control connection may be poisoned at any time. When poisoned, a connection will commit
 * suicide as soon as all the data is written out to the user and the data connection finishes.
 * This feature is required to drop connections when server is full: when message is written
 * out to a user the connection will be dropped right away. Or when a user sends QUIT command,
 * the control connection will wait until the data connection dies and then it will kill itself.
 * So poisoned status must be set when user submits QUIT command.
 *
 * Some FTP commands put control connection into the INTERRUPT state which is then
 * cleared when the connection sends a reply. Such reply must refer to a command which
 * is allowed to clear the state or does not have a reference to a command.
 * Usually INTERRUPT state is trigerred by data transfer commands and cleared right
 * after data transfer ends.
 *
 *
 * ColoradoFTP - The Open Source FTP Server (http://cftp.coldcore.com)
 */
public interface ControlConnection extends Connection {

  /** Reply to user
   * @param reply Reply
   */
  public void reply(Reply reply);


  /** Get user session
   * @return Session
   */
  public Session getSession();


  /** Get data connection of this control connection
   * @return Data connection or NULL if does not have one
   */
  public DataConnection getDataConnection();


  /** Set data connection of this control connection
   * @param dataConnection Data connection
   */
  public void setDataConnection(DataConnection dataConnection);


  /** Get data connection initiator
   * @return Initiator
   */
  public DataConnectionInitiator getDataConnectionInitiator();


  /** Get text length that awaits in the outgoing to user buffer
   * @return Test length
   */
  public int getOutgoingBufferSize();


  /** Get text length that awaits in the incoming from user buffer
   * @return Test length
   */
  public int getIncomingBufferSize();
}
