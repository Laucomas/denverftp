/**
 * Command STOR.
 * See FTP spec for details on the command.
 *
 * This class is designed as the superclass for STOU and APPE commands.
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

import java.nio.channels.WritableByteChannel;
import java.nio.channels.Channel;

public class StorCommand extends AbstractCommand {

  private static Logger log = Logger.getLogger(StorCommand.class);


  public Reply execute() {
    Reply reply = getReply();
    if (!testLogin()) return reply;

    String filename = getFilename();
    if (filename.length() == 0) {
      reply.setCode("501");
      reply.setText("Send file name.");
      return reply;
    }

    //Test if there is data channel left in the session
    closeSessionDataChannel();

    Session session = controlConnection.getSession();
    FileSystem fileSystem = (FileSystem) ObjectFactory.getObject(ObjectName.FILESYSTEM);
    WritableByteChannel wbc = fileSystem.saveFile(filename, isAppend(), session);

    DataFilterApplicator applicator = (DataFilterApplicator) ObjectFactory.getObject(ObjectName.DATA_FILTER_APPLICATOR);
    wbc = applicator.applyFilters(wbc, session);

    String type = (String) session.getAttribute(SessionAttributeName.DATA_TYPE);
    if (type == null) type = "A";

    if (!prepareForDataConnection()) {
      try {
        wbc.close();
      } catch (Throwable e) {
        log.error("Error closing data channel (ignoring)", e);
      }
      return reply;
    }

    session.setAttribute(SessionAttributeName.DATA_CONNECTION_FILENAME, filename);
    session.setAttribute(SessionAttributeName.DATA_CONNECTION_MODE, getDataConnectionMode());
    session.setAttribute(SessionAttributeName.DATA_CONNECTION_CHANNEL, wbc);

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
  

  /** Get filename to save
   * @return Filename
   */
  protected String getFilename() {
    return getParameter();
  }


  /** Get data connection mode to use
   * @return Data connection mode
   */
  protected DataConnectionMode getDataConnectionMode() {
    return DataConnectionMode.STOR;
  }


  /** Test if file must be appended or overwritten
   * @return TRUE to append or FALSE to overwrite
   */
  protected boolean isAppend() {
    return false;
  }
}
