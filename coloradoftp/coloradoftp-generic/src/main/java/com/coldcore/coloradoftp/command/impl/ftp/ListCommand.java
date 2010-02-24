/**
 * Command LIST.
 * See FTP spec for details on the command.
 */
package com.coldcore.coloradoftp.command.impl.ftp;

import com.coldcore.coloradoftp.command.Reply;
import com.coldcore.coloradoftp.command.impl.AbstractCommand;
import com.coldcore.coloradoftp.connection.DataConnectionMode;
import com.coldcore.coloradoftp.factory.ObjectFactory;
import com.coldcore.coloradoftp.factory.ObjectName;
import com.coldcore.coloradoftp.filesystem.FileSystem;
import com.coldcore.coloradoftp.filesystem.ListingFile;
import com.coldcore.coloradoftp.session.Session;
import com.coldcore.coloradoftp.session.SessionAttributeName;
import org.apache.log4j.Logger;

import java.io.ByteArrayInputStream;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Set;

public class ListCommand extends AbstractCommand {

  private static Logger log = Logger.getLogger(ListCommand.class);


  public Reply execute() {
    Reply reply = getReply();
    if (!testLogin()) return reply;

    Session session = controlConnection.getSession();
    FileSystem fileSystem = (FileSystem) ObjectFactory.getObject(ObjectName.FILESYSTEM);

    String listDir = fileSystem.getCurrentDirectory(session);
    if (getParameter().length() > 0 &&
        parameter.indexOf("*") == -1 && !parameter.startsWith("-")) listDir = getParameter();

    Set<ListingFile> list = fileSystem.listDirectory(listDir, session);
    String prepared = prepareList(list);

    ByteArrayInputStream bin = new ByteArrayInputStream(prepared.getBytes());
    ReadableByteChannel rbc = Channels.newChannel(bin);

    session.setAttribute(SessionAttributeName.DATA_CONNECTION_MODE, DataConnectionMode.LIST);
    session.setAttribute(SessionAttributeName.DATA_CONNECTION_CHANNEL, rbc);

    if (!prepareForDataConnection()) return reply;

    reply.setCode("150");
    reply.setText("Opening A mode data connection for "+listDir+".");
    return reply;
  }


  /** Convert files list into format FTP client can understand
   * @param list List of files and directories
   * @return Prepared string
   */
  protected String prepareList(Set<ListingFile> list) {
    StringBuffer sb = new StringBuffer();
    SimpleDateFormat sdf = new SimpleDateFormat("MMM dd HH:mm", new Locale("en"));
    for (ListingFile f : list) {
      //-rw------- 1 peter 848 Dec 14 11:22 README.txt\r\n
      sb.append(f.isDirectory() ? "d" : "-").append(f.getPermissions()).append(" ");
      sb.append("1 ").append(f.getOwner()).append(" ");
      sb.append(f.getSize()).append(" ");
      sb.append(sdf.format(f.getLastModified())).append(" ");
      sb.append(f.getName()).append("\r\n");
    }
    log.debug("Directory listing:\n"+sb);
    return sb.toString();
  }
}
