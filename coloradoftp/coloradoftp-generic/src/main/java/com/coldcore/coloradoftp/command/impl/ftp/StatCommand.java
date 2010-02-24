/**
 * Command STAT.
 * See FTP spec for details on the command.
 */
package com.coldcore.coloradoftp.command.impl.ftp;

import com.coldcore.coloradoftp.command.Reply;
import com.coldcore.coloradoftp.connection.DataConnection;
import com.coldcore.coloradoftp.factory.ObjectFactory;
import com.coldcore.coloradoftp.factory.ObjectName;
import com.coldcore.coloradoftp.filesystem.FileSystem;
import com.coldcore.coloradoftp.filesystem.ListingFile;
import com.coldcore.coloradoftp.session.Session;
import com.coldcore.coloradoftp.session.SessionAttributeName;

import java.util.Set;

public class StatCommand extends ListCommand {

  public Reply execute() {
    Reply reply = getReply();
    if (!testLogin()) return reply;

    DataConnection dataConnection = controlConnection.getDataConnection();
    if (dataConnection != null) {
      reply.setCode("221");
      reply.setText("Waiting for data transfer to finish.");
      return reply;
    }

    //If there is no parameter then send general info
    if (getParameter().length() == 0) {
      Session session = controlConnection.getSession();
      String mode = (String) session.getAttribute(SessionAttributeName.DATA_MODE);
      String type = (String) session.getAttribute(SessionAttributeName.DATA_TYPE);
      String stru = (String) session.getAttribute(SessionAttributeName.DATA_STRUCTURE);
      if (mode == null) mode = "S";
      if (type == null) type = "A";
      if (stru == null) stru = "F";
      reply.setCode("211");
      reply.setText("Control connection OK, TYPE "+type+", MODE "+mode+", STRU "+stru+".");
      return reply;
    }

    //The parameter must be path, so list it
    String listDir = getParameter();
    Session session = controlConnection.getSession();
    FileSystem fileSystem = (FileSystem) ObjectFactory.getObject(ObjectName.FILESYSTEM);
    Set<ListingFile> list = fileSystem.listDirectory(listDir, session);

    String prepared = prepareList(list)+"end";


    reply.setCode("212");
    reply.setText("List results:\r\n"+prepared.trim());
    return reply;
  }

  
  public boolean processInInterruptState() {
    return true;
  }

}
