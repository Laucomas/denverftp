package com.coldcore.coloradoftp.command.impl.ftp;

import com.coldcore.coloradoftp.command.impl.AbstractCommand;
import com.coldcore.coloradoftp.command.Reply;
import com.coldcore.coloradoftp.command.CommandFactory;
import com.coldcore.coloradoftp.command.Command;
import com.coldcore.coloradoftp.factory.ObjectFactory;
import com.coldcore.coloradoftp.factory.ObjectName;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Command OPTS.
 * See FTP spec for details on the command.
 *
 * This implementation can also be used as FEAT command.
 */
public class OptsCommand extends AbstractCommand {

  private static Logger log = Logger.getLogger(OptsCommand.class);


  public Reply execute() {
    Reply reply = getReply();

    String pcmd = getParameter().toUpperCase();
    if (pcmd.equals("")) {
      reply.setCode("501");
      reply.setText("Send another command as parameter.");
      return reply;
    }

    //Get all available commands
    CommandFactory commandFactory = (CommandFactory) ObjectFactory.getObject(ObjectName.COMMAND_FACTORY);
    List<String> commandNames = new ArrayList<String>(commandFactory.listNames());

    String tpcmd = pcmd;
    int ind = tpcmd.indexOf(" ");
    if (ind != -1) tpcmd = tpcmd.substring(0, ind);

    if (commandNames.contains(tpcmd)) {
      //Command is supported
      log.debug("Command "+tpcmd+" is supported");
      replyOnExistingCommand(pcmd);
      return reply;
    } else {
      //Command is not supported
      log.debug("Command "+tpcmd+" is not supported");
      replyOnUnexistingCommand(pcmd);
      return reply;
    }

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
      //Child dit not provided a reply - use the default
      reply.setCode("504");
      reply.setText("Command not implemented for that parameter.");
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

}
