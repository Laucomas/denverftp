/**
 * Command TYPE.
 * See FTP spec for details on the command.
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

public class TypeCommand extends AbstractCommand {

  public Reply execute() {
    Reply reply = getReply();
    if (!testLogin()) return reply;

    String type = getParameter();
    type = type.toUpperCase();

    if (type.length() == 0) {
      reply.setCode("501");
      reply.setText("Syntax error in parameters or arguments.");
      return reply;
    }

    DataFilterFactory factory = (DataFilterFactory) ObjectFactory.getObject(ObjectName.TYPE_FILTER_FACTORY);
    if (factory.listNames().contains(type)) {
      Session session = getConnection().getSession();
      session.setAttribute(SessionAttributeName.DATA_TYPE, type);
      reply.setCode("200");
      reply.setText("Type set to "+type);
    } else {
      StringBuffer sb = new StringBuffer();
      Iterator<String> it = factory.listNames().iterator();
      while (it.hasNext()) {
        sb.append("TYPE ").append(it.next());
        if (it.hasNext()) sb.append(", ");
      }
      reply.setCode("504");
      reply.setText("Supported only "+sb.toString()+".");
    }
    return reply;
  }
}
