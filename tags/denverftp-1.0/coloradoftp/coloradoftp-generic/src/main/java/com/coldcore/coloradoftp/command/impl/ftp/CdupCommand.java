/**
 * Command CDUP.
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

public class CdupCommand extends AbstractCommand {

  private static Logger log = Logger.getLogger(CdupCommand.class);


  public Reply execute() {
    Reply reply = getReply();
    if (!testLogin()) return reply;

    Session session = controlConnection.getSession();
    FileSystem fileSystem = (FileSystem) ObjectFactory.getObject(ObjectName.FILESYSTEM);
    String curDir = fileSystem.getCurrentDirectory(session);
    String cdup = fileSystem.getParent(curDir, session);
    fileSystem.changeDirectory(cdup, session);

    reply.setCode("250");
    reply.setText("Directory changed.");
    return reply;
  }
}
