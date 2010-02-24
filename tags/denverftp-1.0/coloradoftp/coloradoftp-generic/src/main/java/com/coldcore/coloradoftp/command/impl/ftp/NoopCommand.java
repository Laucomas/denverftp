/**
 * Command NOOP.
 * See FTP spec for details on the command.
 */
package com.coldcore.coloradoftp.command.impl.ftp;

import com.coldcore.coloradoftp.command.Reply;
import com.coldcore.coloradoftp.command.impl.AbstractCommand;
import com.coldcore.coloradoftp.factory.ObjectFactory;
import com.coldcore.coloradoftp.factory.ObjectName;

public class NoopCommand extends AbstractCommand {

  public Reply execute() {
    String param = getParameter();

    Reply reply = getReply();
    reply.setCode("200");
    reply.setText("OK"+(param.length() > 0 ? " "+param : ""));
    return reply;
  }
}
