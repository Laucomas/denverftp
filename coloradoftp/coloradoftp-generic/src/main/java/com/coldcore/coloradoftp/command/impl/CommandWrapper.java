package com.coldcore.coloradoftp.command.impl;

import com.coldcore.coloradoftp.command.Command;
import com.coldcore.coloradoftp.command.Reply;
import com.coldcore.coloradoftp.connection.ControlConnection;

/**
 * Command wrapper.
 * This class is for extensions wishing to operate on existing command
 * and stay independent of future interface changes.
 */
public class CommandWrapper implements Command {

  protected Command command;


  public CommandWrapper(Command command) {
    super();
    this.command = command;
    if (command == null) throw new IllegalArgumentException("Invalid command");
  }


  public boolean processInInterruptState() {
    return command.processInInterruptState();
  }


  public boolean canClearInterruptState() {
    return command.canClearInterruptState();
  }


  public String getName() {
    return command.getName();
  }


  public void setName(String name) {
    command.setName(name);
  }


  public String getParameter() {
    return command.getParameter();
  }


  public void setParameter(String parameter) {
    command.setParameter(parameter);
  }


  public Reply execute() {
    return command.execute();
  }


  public Reply executeOnParent(Command parent) {
    return command.executeOnParent(parent);
  }


  public void setConnection(ControlConnection connection) {
    command.setConnection(connection);
  }


  public ControlConnection getConnection() {
    return command.getConnection();
  }
}
