package com.coldcore.coloradoftp.command.impl;

import com.coldcore.coloradoftp.command.Command;
import com.coldcore.coloradoftp.command.CommandProcessor;
import com.coldcore.coloradoftp.command.Reply;
import com.coldcore.coloradoftp.factory.ObjectFactory;
import com.coldcore.coloradoftp.factory.ObjectName;
import com.coldcore.coloradoftp.filesystem.FailedActionException;
import org.apache.log4j.Logger;

/**
 * @see com.coldcore.coloradoftp.command.CommandProcessor
 */
public class GenericCommandProcessor implements CommandProcessor {

  private static Logger log = Logger.getLogger(GenericCommandProcessor.class);


  public void execute(Command command) {
    //Execute command
    Reply reply;
    try {
      reply = command.execute();
    } catch (FailedActionException e) {
      //Filesystem error
      reply = getFileSystemReply(e);
    } catch (Throwable e) {
      //Command error
      log.error("Command failed (ignoring)", e);
      reply = ((Command) ObjectFactory.getObject(ObjectName.COMMAND_LOCAL_ERROR)).execute();
    }

    String debug = "[Command] "+command.getName()+" "+(command.getParameter()==null?"":command.getParameter())+"\n";
    debug += reply==null?"[NO REPLY]":"[Reply] "+reply.getCode()+" "+(reply.getText()==null?"":reply.getText());
    log.debug("Execution result:\n"+debug);

    //Submit reply to control connection
    try {
      if (reply != null) command.getConnection().reply(reply);
    } catch (Throwable e) {
      log.error("Command's connection reply failed (ignoring)", e);
    }
  }


  /** Get reply corresponding to a filesystem error
   * @param e Filesystem exception
   * @return Reply
   */
  protected Reply getFileSystemReply(FailedActionException e) {
    Reply reply = (Reply) ObjectFactory.getObject(ObjectName.REPLY);
    switch (e.getReason()) {
      case INVALID_INPUT:
        reply.setCode("553");
        reply.setText(e.getText() == null ? "Cannot parse input." : e.getText());
        return reply;
      case NO_PERMISSIONS:
        reply.setCode("550");
        reply.setText(e.getText() == null ? "No permission." : e.getText());
        return reply;
      case NOT_IMPLEMENTED:
        reply.setCode("504");
        reply.setText(e.getText() == null ? "Not implemented." : e.getText());
        return reply;
      case PATH_ERROR:
        reply.setCode("450");
        reply.setText(e.getText() == null ? "Requested path error." : e.getText());
        return reply;
      case OTHER:
        reply.setCode("450");
        reply.setText(e.getText() == null ? "Unknown reason." : e.getText());
        return reply;
      default:
        log.error("Filesystem failed (ignoring)", e);
        return ((Command) ObjectFactory.getObject(ObjectName.COMMAND_LOCAL_ERROR)).execute();
    }
  }
}
