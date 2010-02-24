/**
 * Command PWD.
 * See FTP spec for details on the command.
 */
package com.coldcore.coloradoftp.command.impl.ftp;

import com.coldcore.coloradoftp.command.Reply;
import com.coldcore.coloradoftp.command.impl.AbstractCommand;
import com.coldcore.coloradoftp.factory.ObjectFactory;
import com.coldcore.coloradoftp.factory.ObjectName;
import com.coldcore.coloradoftp.filesystem.FileSystem;

public class PwdCommand extends AbstractCommand {

  public Reply execute() {
    Reply reply = getReply();
    if (!testLogin()) return reply;

    FileSystem fileSystem = (FileSystem) ObjectFactory.getObject(ObjectName.FILESYSTEM);
    String dir = fileSystem.getCurrentDirectory(controlConnection.getSession());

    dir = dir.replaceAll("\"", "\"\""); //Encode double-quated

    reply.setCode("257");
    reply.setText("\""+dir+"\" is current directory.");
    return reply;
  }
}
