/**
 * Command RETR.
 * See FTP spec for details on the command.
 */
package com.coldcore.coloradoftp.command.impl.ftp;

import com.coldcore.coloradoftp.command.Reply;
import com.coldcore.coloradoftp.command.impl.AbstractCommand;
import com.coldcore.coloradoftp.connection.DataConnectionMode;
import com.coldcore.coloradoftp.factory.ObjectFactory;
import com.coldcore.coloradoftp.factory.ObjectName;
import com.coldcore.coloradoftp.filesystem.FileSystem;
import com.coldcore.coloradoftp.filter.DataFilterApplicator;
import com.coldcore.coloradoftp.session.Session;
import com.coldcore.coloradoftp.session.SessionAttributeName;
import org.apache.log4j.Logger;

import java.nio.channels.ReadableByteChannel;
import java.nio.channels.Channel;

public class RetrCommand extends AbstractCommand {

  private static Logger log = Logger.getLogger(RetrCommand.class);


  public Reply execute() {
    Reply reply = getReply();
    if (!testLogin()) return reply;

    String filename = getParameter();
    if (filename.length() == 0) {
      reply.setCode("501");
      reply.setText("Send file name.");
      return reply;
    }

    //Test if there is data channel left in the session
    closeSessionDataChannel();

    Session session = controlConnection.getSession();
    Long marker = (Long) session.getAttribute(SessionAttributeName.DATA_MARKER);
    session.removeAttribute(SessionAttributeName.DATA_MARKER);
    if (marker == null) marker = 0L;

    FileSystem fileSystem = (FileSystem) ObjectFactory.getObject(ObjectName.FILESYSTEM);
    ReadableByteChannel rbc = fileSystem.readFile(filename, marker, session);

    DataFilterApplicator applicator = (DataFilterApplicator) ObjectFactory.getObject(ObjectName.DATA_FILTER_APPLICATOR);
    rbc = applicator.applyFilters(rbc, session);

    String type = (String) session.getAttribute(SessionAttributeName.DATA_TYPE);
    if (type == null) type = "A";

    if (!prepareForDataConnection()) {
      try {
        rbc.close();
      } catch (Throwable e) {
        log.error("Error closing data channel (ignoring)", e);
      }
      return reply;
    }

    session.setAttribute(SessionAttributeName.DATA_CONNECTION_FILENAME, filename);
    session.setAttribute(SessionAttributeName.DATA_CONNECTION_MODE, DataConnectionMode.RETR);
    session.setAttribute(SessionAttributeName.DATA_CONNECTION_CHANNEL, rbc);

    reply.setCode("150");
    reply.setText("Opening "+type+" mode data connection for "+filename+".");
    return reply;
  }


  /** Close a data channel if exists in the session */
  protected void closeSessionDataChannel() {
    Session session = controlConnection.getSession();
    Channel odc = (Channel) session.getAttribute(SessionAttributeName.DATA_CONNECTION_CHANNEL);
    if (odc != null) {
      log.debug("Attempting to close data channel in session");
      session.removeAttribute(SessionAttributeName.DATA_CONNECTION_CHANNEL);
      try {
        odc.close();
      } catch (Throwable e) {
        log.error("Error closing data channel (ignoring)", e);
      }
    }
  }
}
