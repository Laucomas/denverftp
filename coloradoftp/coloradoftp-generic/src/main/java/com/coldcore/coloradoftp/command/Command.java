package com.coldcore.coloradoftp.command;

import com.coldcore.coloradoftp.connection.ControlConnection;

/**
 * User command.
 *
 * This class is executed by a command processor and returns a reply which
 * if then send back to user.
 *
 * In general there should be a separate implementation for every FTP command,
 * so every implementation knows how to handle one (and only one) FTP command.
 * This way commands can be easily replaced later.
 *
 * Control connection can enter an INTERRUPT state. In INTERRUPT state user must
 * wait for a server reply and is not allowed to send anything in but the INTERRUPT
 * commands. As a result, there are two types of FTP commands: one will be processed
 * during the INTERRUPT state and the other will be dropped when a control connection
 * is in that state.
 *
 * Some FTP commands put control connection into the INTERRUPT state which is then
 * cleared when the connection sends a reply. Such reply must refer to a command which
 * is allowed to clear the state or does not have a reference to a command.
 *
 * There is a certain set of commands that must be processed during the INTERRUPT state:
 * ABOR, QUIT and STAT.
 * And usually only one command is allowed to clear that state: ABOR.
 * The rest of FTP commands should not bother with INTERRUPT state.
 *
 *
 * ColoradoFTP - The Open Source FTP Server (http://cftp.coldcore.com)
 */
public interface Command {

  /** Test if this is command must be processed while a connection is in the INTERRUPT state
   * @return TRUE is it can be, FALSE otherwise
   */
  public boolean processInInterruptState();


  /** Test if reply to this command must clear INTERRUPT state of a connection
   * @return TRUE is it can, FALSE otherwise
   */
  public boolean canClearInterruptState();


  /** Get name of the command
   * @return Command name
   */
  public String getName();


  /** Set name of the command
   * @param name Command name
   */
  public void setName(String name);


  /** Get parameter of the command
   * @return Command parameter
   */
  public String getParameter();


  /** Set parameter of the command
   * @param parameter Command parameter
   */
  public void setParameter(String parameter);


  /** Execute the command
   * @return Reply to the command
   */
  public Reply execute();


  /** Execute the command as part of the parent command (e.g. FEAT/HELP/OPTS)
   * @param parent Parent command
   * @return Reply to the command or NULL to allow the parent to provide the default reply
   */
  public Reply executeOnParent(Command parent);


  /** Set control connection that submitted this command
   * @param connection Connection
   */
  public void setConnection(ControlConnection connection);


  /** Get control connection that submitted this command
   * @return Connection
   */
  public ControlConnection getConnection();
}
