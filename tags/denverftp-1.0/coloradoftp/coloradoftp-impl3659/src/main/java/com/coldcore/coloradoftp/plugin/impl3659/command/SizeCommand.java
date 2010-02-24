package com.coldcore.coloradoftp.plugin.impl3659.command;

import com.coldcore.coloradoftp.command.Reply;
import com.coldcore.coloradoftp.factory.ObjectFactory;
import com.coldcore.coloradoftp.factory.ObjectName;
import com.coldcore.coloradoftp.filesystem.FileSystem;
import com.coldcore.coloradoftp.filesystem.ListingFile;
import com.coldcore.coloradoftp.filter.DataFilter;
import com.coldcore.coloradoftp.filter.DataFilterFactory;
import com.coldcore.coloradoftp.session.Session;
import com.coldcore.coloradoftp.session.SessionAttributeName;
import org.apache.log4j.Logger;

/**
 * Command SIZE.
 * See FTP spec for details on the command.
 */
public class SizeCommand extends BaseCommand {

  private static Logger log = Logger.getLogger(SizeCommand.class);


  public Reply execute() {
    Reply reply = getReply();
    if (!testLogin()) return reply;

    String filename = getParameter();
    if (filename.length() == 0) {
      reply.setCode("501");
      reply.setText("Send file name.");
      return reply;
    }

    Session session = controlConnection.getSession();
    FileSystem fileSystem = (FileSystem) ObjectFactory.getObject(ObjectName.FILESYSTEM);

    DataFilter filter = getTypeFilter(session);
    if (filter != null && filter.mayModifyDataLength()) {
      reply.setCode("550");
      reply.setText("SIZE unavailable for TYPE "+filter.getName()+".");
      return reply;
    }

    ListingFile file = fileSystem.getPath(filename, session);
    if (file.isDirectory()) {
      reply.setCode("550");
      reply.setText("File unavailable.");
      return reply;
    }

    String size = ""+file.getSize();

    reply.setCode("213");
    reply.setText(size);
    return reply;
  }


  /** Load filter for TYPE
   * @param session User session
   * @return Data filter (never NULL)
   */
  protected DataFilter getTypeFilter(Session session) {
    String type = (String) session.getAttribute(SessionAttributeName.DATA_TYPE);
    if (type == null) type = "A";
    DataFilterFactory factory = (DataFilterFactory) ObjectFactory.getObject(ObjectName.TYPE_FILTER_FACTORY);
    return factory.create(type);
  }
}
