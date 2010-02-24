package com.coldcore.coloradoftp.plugin.impl3659.command;

import com.coldcore.coloradoftp.command.Reply;
import com.coldcore.coloradoftp.factory.ObjectFactory;
import com.coldcore.coloradoftp.factory.ObjectName;
import com.coldcore.coloradoftp.filesystem.FileSystem;
import com.coldcore.coloradoftp.filesystem.ListingFile;
import com.coldcore.coloradoftp.session.Session;
import org.apache.log4j.Logger;

/**
 * Command MDTM.
 * See FTP spec for details on the command.
 */
public class MdtmCommand extends BaseCommand {

  private static Logger log = Logger.getLogger(MdtmCommand.class);


  /** Set date format
   * @param format Date format
   */
  public void setDateFormat(String format) {
    if (format == null || !format.matches(DATE_FORMAT_REGEXP)) throw new IllegalArgumentException("Invalid format");
    setupDateFormatter(format);
  }


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

    ListingFile file = fileSystem.getPath(filename, session);
    if (file.isDirectory()) {
      reply.setCode("550");
      reply.setText("File unavailable.");
      return reply;
    }

    String tstamp = dateFormatter.format(file.getLastModified());

    reply.setCode("213");
    reply.setText(tstamp);
    return reply;
  }

}
