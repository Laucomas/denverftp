/**
 * Command MKD.
 * See FTP spec for details on the command.
 */
package com.coldcore.coloradoftp.command.impl.ftp;

import com.coldcore.coloradoftp.command.Reply;
import com.coldcore.coloradoftp.command.impl.AbstractCommand;
import com.coldcore.coloradoftp.factory.ObjectFactory;
import com.coldcore.coloradoftp.factory.ObjectName;
import com.coldcore.coloradoftp.filesystem.FileSystem;
import org.apache.log4j.Logger;

public class MkdCommand extends AbstractCommand {

  private static Logger log = Logger.getLogger(MkdCommand.class);


  public Reply execute() {
    Reply reply = getReply();
    if (!testLogin()) return reply;

    String dir = getParameter();
    if (dir.length() == 0) {
      reply.setCode("501");
      reply.setText("Send directory name.");
      return reply;
    }

    FileSystem fileSystem = (FileSystem) ObjectFactory.getObject(ObjectName.FILESYSTEM);
    dir = fileSystem.createDirectory(dir, controlConnection.getSession());

    dir = dir.replaceAll("\"", "\"\""); //Encode double-quated

    reply.setCode("257");
    reply.setText("\""+dir+"\" directory created.");
    return reply;
  }
}
