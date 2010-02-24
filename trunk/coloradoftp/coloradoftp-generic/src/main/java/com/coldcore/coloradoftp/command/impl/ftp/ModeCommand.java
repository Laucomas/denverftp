/**
 * Command MODE.
 * See FTP spec for details on the command.
 *
 * This implementation supports only stream mode "S".
 */
package com.coldcore.coloradoftp.command.impl.ftp;

import com.coldcore.coloradoftp.command.Reply;
import com.coldcore.coloradoftp.command.impl.AbstractCommand;
import com.coldcore.coloradoftp.factory.ObjectFactory;
import com.coldcore.coloradoftp.factory.ObjectName;
import com.coldcore.coloradoftp.filter.DataFilterFactory;
import com.coldcore.coloradoftp.session.Session;
import com.coldcore.coloradoftp.session.SessionAttributeName;

import java.util.Iterator;

public class ModeCommand extends AbstractCommand {

  public Reply execute() {
    Reply reply = getReply();
    if (!testLogin()) return reply;

    String mode = getParameter();
    mode = mode.toUpperCase();

    if (mode.length() == 0) {
      reply.setCode("501");
      reply.setText("Syntax error in parameters or arguments.");
      return reply;
    }

    DataFilterFactory factory = (DataFilterFactory) ObjectFactory.getObject(ObjectName.MODE_FILTER_FACTORY);
    if (factory.listNames().contains(mode)) {
      Session session = getConnection().getSession();
      session.setAttribute(SessionAttributeName.DATA_MODE, mode);
      reply.setCode("200");
      reply.setText("Command okay.");
    } else {
      StringBuffer sb = new StringBuffer();
      Iterator<String> it = factory.listNames().iterator();
      while (it.hasNext()) {
        sb.append("MODE ").append(it.next());
        if (it.hasNext()) sb.append(", ");
      }
      reply.setCode("504");
      reply.setText("Supported only "+sb.toString()+".");
    }
    return reply;
  }
}
