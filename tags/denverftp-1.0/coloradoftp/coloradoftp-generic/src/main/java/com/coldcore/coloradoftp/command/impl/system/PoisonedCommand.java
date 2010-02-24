package com.coldcore.coloradoftp.command.impl.system;

import com.coldcore.coloradoftp.command.Reply;
import com.coldcore.coloradoftp.command.impl.AbstractCommand;
import com.coldcore.coloradoftp.factory.ObjectFactory;
import com.coldcore.coloradoftp.factory.ObjectName;

/**
 * "Service not available" command.
 * System submits this command into a command processor when server is going down soon.
 */
public class PoisonedCommand extends AbstractCommand {

  public Reply execute() {
    Reply reply = (Reply) ObjectFactory.getObject(ObjectName.REPLY);
    reply.setCode("421");
    reply.setText("Service not available, closing control connection.");
    return reply;
  }


  public String getName() {
    return "SYSTEM (POISONED)";
  }
}
