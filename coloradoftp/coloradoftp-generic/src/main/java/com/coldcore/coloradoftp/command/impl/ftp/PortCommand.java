/**
 * Command PORT.
 * See FTP spec for details on the command.
 */
package com.coldcore.coloradoftp.command.impl.ftp;

import com.coldcore.coloradoftp.command.Reply;
import com.coldcore.coloradoftp.command.impl.AbstractCommand;
import com.coldcore.coloradoftp.connection.DataConnectionInitiator;
import com.coldcore.coloradoftp.session.DataOpenerType;
import com.coldcore.coloradoftp.session.Session;
import com.coldcore.coloradoftp.session.SessionAttributeName;
import org.apache.log4j.Logger;

import java.util.StringTokenizer;

public class PortCommand extends AbstractCommand {

  private static Logger log = Logger.getLogger(PortCommand.class);


  public Reply execute() {
    Reply reply = getReply();
    if (!testLogin()) return reply;

    Session session = getConnection().getSession();
    session.removeAttribute(SessionAttributeName.DATA_OPENER_TYPE);

    //Get host address and port numer client listens to
    try {
      StringTokenizer st = new StringTokenizer(getParameter(), ",");
      String ip = Integer.parseInt(st.nextToken())+"."
                + Integer.parseInt(st.nextToken())+"."
                + Integer.parseInt(st.nextToken())+"."
                + Integer.parseInt(st.nextToken());
      int port = Integer.parseInt(st.nextToken())*256+Integer.parseInt(st.nextToken());

      DataConnectionInitiator initiator = controlConnection.getDataConnectionInitiator();
      initiator.setIp(ip);
      initiator.setPort(port);
      log.debug("Data connection will connect to "+ip+":"+port);

    } catch (Throwable e) {
      reply.setCode("501");
      reply.setText("Send correct IP and port number.");
      return reply;
    }

    //So others will know how to prepare for a new data connection
    session.setAttribute(SessionAttributeName.DATA_OPENER_TYPE, DataOpenerType.PORT);

    reply.setCode("200");
    reply.setText("PORT command successful.");
    return reply;
  }
}
