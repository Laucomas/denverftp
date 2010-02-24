/**
 * Command RNTO.
 * See FTP spec for details on the command.
 */
package com.coldcore.coloradoftp.command.impl.ftp;

import com.coldcore.coloradoftp.command.Reply;
import com.coldcore.coloradoftp.command.impl.AbstractCommand;
import com.coldcore.coloradoftp.factory.ObjectFactory;
import com.coldcore.coloradoftp.factory.ObjectName;
import com.coldcore.coloradoftp.filesystem.FileSystem;
import com.coldcore.coloradoftp.session.Session;
import org.apache.log4j.Logger;

public class RntoCommand extends AbstractCommand {

  private static Logger log = Logger.getLogger(RntoCommand.class);


  public Reply execute() {
    Reply reply = getReply();
    if (!testLogin()) return reply;

    Session session = controlConnection.getSession();
    session.removeAttribute("rnto.path");

    String rnto = getParameter();
    if (rnto.equals("")) {
      reply.setCode("501");
      reply.setText("Send path name.");
      return reply;
    }

    String rnfr = (String) session.getAttribute("rnfr.path");
    session.removeAttribute("rnfr.path");

    if (rnfr == null) {
      reply.setCode("503");
      reply.setText("Send RNFR first.");
      return reply;
    }

    FileSystem fileSystem = (FileSystem) ObjectFactory.getObject(ObjectName.FILESYSTEM);
    fileSystem.renamePath(rnfr, rnto, session);

    reply.setCode("250");
    reply.setText("Path renamed.");
    return reply;
  }
}
