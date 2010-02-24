/**
 * Command RNFR.
 * See FTP spec for details on the command.
 */
package com.coldcore.coloradoftp.command.impl.ftp;

import com.coldcore.coloradoftp.command.Reply;
import com.coldcore.coloradoftp.command.impl.AbstractCommand;
import com.coldcore.coloradoftp.session.Session;
import org.apache.log4j.Logger;

public class RnfrCommand extends AbstractCommand {

  private static Logger log = Logger.getLogger(RnfrCommand.class);


  public Reply execute() {
    Reply reply = getReply();
    if (!testLogin()) return reply;

    Session session = controlConnection.getSession();
    session.removeAttribute("rnfr.path");

    String path = getParameter();
    if (path.equals("")) {
      reply.setCode("501");
      reply.setText("Send path name.");
      return reply;
    }

    session.setAttribute("rnfr.path", path);

    reply.setCode("350");
    reply.setText("Send RNTO to complete rename.");
    return reply;
  }
}
