/**
 * Command CWD.
 * See FTP spec for details on the command.
 */
package com.coldcore.coloradoftp.command.impl.ftp;

import com.coldcore.coloradoftp.command.Reply;
import com.coldcore.coloradoftp.command.impl.AbstractCommand;
import com.coldcore.coloradoftp.factory.ObjectFactory;
import com.coldcore.coloradoftp.factory.ObjectName;
import com.coldcore.coloradoftp.filesystem.FileSystem;
import org.apache.log4j.Logger;

public class CwdCommand extends AbstractCommand {

  private static Logger log = Logger.getLogger(CwdCommand.class);


  public Reply execute() {
    Reply reply = getReply();
    if (!testLogin()) return reply;

    String dir = getParameter();
    if (dir.length() == 0) {
      reply.setCode("250");
      reply.setText("CWD command successful.");
      return reply;
    }

    FileSystem fileSystem = (FileSystem) ObjectFactory.getObject(ObjectName.FILESYSTEM);
    fileSystem.changeDirectory(dir, controlConnection.getSession());

    reply.setCode("250");
    reply.setText("Directory changed.");
    return reply;
  }
}
