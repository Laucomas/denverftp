package com.coldcore.coloradoftp.command.impl.ftp;

import com.coldcore.coloradoftp.command.CommandFactory;
import com.coldcore.coloradoftp.command.Reply;
import com.coldcore.coloradoftp.command.Command;
import com.coldcore.coloradoftp.command.impl.AbstractCommand;
import com.coldcore.coloradoftp.factory.ObjectFactory;
import com.coldcore.coloradoftp.factory.ObjectName;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Command HELP.
 * See FTP spec for details on the command.
 *
 * This implementation can also be used as FEAT command.
 */
public class HelpCommand extends AbstractCommand {

  private static Logger log = Logger.getLogger(HelpCommand.class);


  public Reply execute() {
    Reply reply = getReply();

    //Get all available commands
    CommandFactory commandFactory = (CommandFactory) ObjectFactory.getObject(ObjectName.COMMAND_FACTORY);
    List<String> commandNames = new ArrayList<String>(commandFactory.listNames());

    String pcmd = getParameter().toUpperCase();
    if (pcmd.length() > 0) {
      if (commandNames.contains(pcmd)) {
        //Command is supported
        log.debug("Command "+pcmd+" is supported");
        replyOnExistingCommand(pcmd);
        return reply;
      } else {
        //Command is not supported
        log.debug("Command "+pcmd+" is not supported");
        replyOnUnexistingCommand(pcmd);
        return reply;
      }
    }

    //No parameter
    replyOnNoCommand();

    return reply;
  }


  /** Called when existing/supported command provided as an argument to form an appropriare reply
   * @param command Command name
   */
  protected void replyOnExistingCommand(String command) {
    //Create a child command
    CommandFactory commandFactory = (CommandFactory) ObjectFactory.getObject(ObjectName.COMMAND_FACTORY);
    Command cmd = commandFactory.create(command+"\r\n");
    cmd.setConnection(getConnection());

    Reply reply = getReply();

    //Execute the child
    Reply rpl = cmd.executeOnParent(this);
    if (rpl != null) {
      //Child provided a reply - use it
      reply.setCode(rpl.getCode());
      reply.setText(rpl.getText());
    } else {
      //Child dit not provided a reply - use the default (as FEAT requires)
      StringBuffer sb = new StringBuffer("Supported command:\r\n");
      sb.append(command).append("\r\nend"); //Uppercase command on a new line!
      reply.setCode("211");
      reply.setText(sb.toString());
    }
  }


  /** Called when unexisting/unsupported command provided as an argument to form an appropriare reply
   * @param command Command name
   */
  protected void replyOnUnexistingCommand(String command) {
    Reply reply = getReply();
    reply.setCode("504");
    reply.setText("Command not implemented for that parameter.");
  }


  /** Called when no command provided as an argument to form an appropriare reply */
  protected void replyOnNoCommand() {
    //Get all available commands
    CommandFactory commandFactory = (CommandFactory) ObjectFactory.getObject(ObjectName.COMMAND_FACTORY);
    List<String> commandNames = new ArrayList<String>(commandFactory.listNames());

    //Sort the list of commands
    Collections.sort(commandNames, new Comparator<String>() {
      public int compare(String o1, String o2) {
        return o1.compareToIgnoreCase(o2);
      }
    });

    //Form a reply (up to 6 commands in a row)
    StringBuffer sb = new StringBuffer("Supported commands:\r\n");
    int i = 0;
    for (String commandName : commandNames) {
      sb.append(commandName);
      if (++i % 6 != 0) sb.append("\t");
      else sb.append("\r\n");
    }

    Reply reply = getReply();
    reply.setCode("214");
    reply.setText(sb.toString().trim()+"\r\nOther commands unimplemented.");
  }

}
