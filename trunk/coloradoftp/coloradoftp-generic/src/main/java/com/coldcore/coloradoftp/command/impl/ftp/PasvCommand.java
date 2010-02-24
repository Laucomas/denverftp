/**
 * Command PASV.
 * See FTP spec for details on the command.
 */
package com.coldcore.coloradoftp.command.impl.ftp;

import com.coldcore.coloradoftp.command.Reply;
import com.coldcore.coloradoftp.command.impl.AbstractCommand;
import com.coldcore.coloradoftp.connection.DataPortListener;
import com.coldcore.coloradoftp.connection.DataPortListenerSet;
import com.coldcore.coloradoftp.factory.ObjectFactory;
import com.coldcore.coloradoftp.factory.ObjectName;
import com.coldcore.coloradoftp.session.DataOpenerType;
import com.coldcore.coloradoftp.session.Session;
import com.coldcore.coloradoftp.session.SessionAttributeName;
import org.apache.log4j.Logger;

import java.util.Set;

public class PasvCommand extends AbstractCommand {

  private static Logger log = Logger.getLogger(PasvCommand.class);
  protected String ip;


  /** Get server IP
   * @return Server IP address
   */
  public String getIp() {
    return ip;
  }


  /** Set server IP
   * @param ip Server IP address
   */
  public void setIp(String ip) {
    this.ip = ip;
  }


  public Reply execute() {
    Reply reply = getReply();
    if (!testLogin()) return reply;

    Session session = getConnection().getSession();
    session.removeAttribute(SessionAttributeName.DATA_OPENER_TYPE);

    DataPortListenerSet listeners = (DataPortListenerSet) ObjectFactory.getObject(ObjectName.DATA_PORT_LISTENER_SET);
    if (listeners.boundNumber() == 0 || ip == null || ip.length() == 0) {
      log.warn("No IP address, cannot accept data connections");
      reply.setCode("425");
      reply.setText("Can't open data connection.");
      return reply;
    }

    int port = 0;
    Set<DataPortListener> set = listeners.list();
    for (DataPortListener listener : set) {
      if (listener.addConnection(controlConnection)) {
        port = listener.getPort();
        break;
      }
    }

    if (port == 0) {
      log.warn("No free data port listeners left");
      reply.setCode("425");
      reply.setText("Failed to enter passive mode, try again.");
      return reply;
    }

    log.debug("Data connection will be accepted on "+ip+":"+port);
    int i = port/256;
    String pstr = i+","+(port-i*256);
    String ipcs = ip.replaceAll("\\.", ",");

    //So others will know how to prepare for a new data connection
    session.setAttribute(SessionAttributeName.DATA_OPENER_TYPE, DataOpenerType.PASV);

    reply.setCode("227");
    reply.setText("Entering Passive Mode ("+ipcs+","+pstr+").");
    return reply;
  }
}
