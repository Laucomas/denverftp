package com.coldcore.coloradoftp.command.impl.system;

import com.coldcore.coloradoftp.command.Reply;
import com.coldcore.coloradoftp.command.impl.AbstractCommand;
import com.coldcore.coloradoftp.factory.ObjectFactory;
import com.coldcore.coloradoftp.factory.ObjectName;

/**
 * "Welcome" command.
 * System submits this command into a command processor when user's connection is accepted by a connector.
 */
public class WelcomeCommand extends AbstractCommand {

  public Reply execute() {
    Reply reply = (Reply) ObjectFactory.getObject(ObjectName.REPLY);
    reply.setCode("220");
    reply.setText("Welcome to ColoradoFTP - the open source FTP server (www.coldcore.com)");
    return reply;
  }


  public String getName() {
    return "SYSTEM (WELCOME)";
  }
}
