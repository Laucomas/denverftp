package com.coldcore.coloradoftp.command.impl;

import com.coldcore.coloradoftp.command.Command;
import com.coldcore.coloradoftp.command.CommandFactory;
import com.coldcore.coloradoftp.factory.ObjectFactory;
import com.coldcore.coloradoftp.factory.ObjectName;
import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @see com.coldcore.coloradoftp.command.CommandFactory
 */
public class GenericCommandFactory implements CommandFactory {

  private static Logger log = Logger.getLogger(GenericCommandFactory.class);
  protected Map<String,String> map;


  public GenericCommandFactory(Map<String,String> commandsRef) {
    map = new HashMap<String,String>(commandsRef);
  }


  public Set<String> listNames() {
    return new HashSet<String>(map.keySet());
  }


  public Command create(String input) {
    String name;
    String parameter;
    try {
      //First comes name of the command and then a single parameter (optional).
      input = input.trim();
      int i = input.indexOf(" ");
      if (i == -1) i = input.length();
      name = input.substring(0,i).trim().toUpperCase();
      parameter = input.substring(i).trim();
    } catch (Throwable e) {
      log.warn("Failed to process input: "+input);
      return (Command) ObjectFactory.getObject(ObjectName.COMMAND_SYNTAX_ERROR);
    }

    Command command;
    String ref = map.get(name);
    if (ref != null) {
      command = (Command) ObjectFactory.getObject(ref);
      command.setName(name);
      command.setParameter(parameter);
    } else {
      log.warn("Command "+name+" not implemented");
      command = (Command) ObjectFactory.getObject(ObjectName.COMMAND_NOT_IMPLEMENTED);
    }
    return command;
  }
}
