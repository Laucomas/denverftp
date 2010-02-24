package com.coldcore.coloradoftp.command.impl.system;

import com.coldcore.coloradoftp.command.Reply;
import com.coldcore.coloradoftp.command.impl.AbstractCommand;
import com.coldcore.coloradoftp.factory.ObjectFactory;
import com.coldcore.coloradoftp.factory.ObjectName;

/**
 * "Syntax error" command.
 * System submits this command into a command processor when user submits input which cannot be parsed.
 */
public class SyntaxErrorCommand extends AbstractCommand {

  public Reply execute() {
    Reply reply = (Reply) ObjectFactory.getObject(ObjectName.REPLY);
    reply.setCode("500");
    reply.setText("Syntax error, command unrecognized.");
    return reply;
  }


  public String getName() {
    return "SYSTEM (SYNTAX ERROR)";
  }
}
