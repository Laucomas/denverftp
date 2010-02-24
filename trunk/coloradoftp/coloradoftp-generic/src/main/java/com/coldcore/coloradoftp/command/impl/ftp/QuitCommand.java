/**
 * Command QUIT.
 * See FTP spec for details on the command.
 */
package com.coldcore.coloradoftp.command.impl.ftp;

import com.coldcore.coloradoftp.command.Reply;
import com.coldcore.coloradoftp.command.impl.AbstractCommand;
import com.coldcore.coloradoftp.connection.DataConnection;
import com.coldcore.coloradoftp.connection.DataPortListener;
import com.coldcore.coloradoftp.connection.DataPortListenerSet;
import com.coldcore.coloradoftp.factory.ObjectFactory;
import com.coldcore.coloradoftp.factory.ObjectName;
import com.coldcore.coloradoftp.session.Session;
import com.coldcore.coloradoftp.session.SessionAttributeName;

public class QuitCommand extends AbstractCommand {

  public Reply execute() {
    Reply reply = getReply();
    if (!testLogin()) return reply;

    //Abort data connection initiator
    controlConnection.getDataConnectionInitiator().abort();

    //Abort data connection listeners
    DataPortListenerSet listeners = (DataPortListenerSet) ObjectFactory.getObject(ObjectName.DATA_PORT_LISTENER_SET);
    for (DataPortListener listener : listeners.list())
      listener.removeConnection(controlConnection);

    //Logout the user
    logout();

    /* We must not clear login state of the user, instead we will poison her connection
     * to make sure she cannot send any more commands in (except for special commands).
     * And set the byte marker on this poisoned connection so it will wait for a reply to be
     * added to its outgoing buffer before attempting to kill itself in the "service" method.
     */
    Session session = getConnection().getSession();
    session.setAttribute(SessionAttributeName.BYTE_MARKER_POISONED, controlConnection.getBytesWrote());
    controlConnection.poison();

    DataConnection dataConnection = controlConnection.getDataConnection();
    if (dataConnection != null) {
      reply.setCode("221");
      reply.setText("Logged out, closing control connection as soon as data transferred.");
    } else {
      reply.setCode("221");
      reply.setText("Logged out, closing control connection.");
    }

    return reply;
  }


  public boolean processInInterruptState() {
    return true;
  }


  /** Log out user.
   * This implementation does nothing.
   */
  protected void logout() {
  }
}
