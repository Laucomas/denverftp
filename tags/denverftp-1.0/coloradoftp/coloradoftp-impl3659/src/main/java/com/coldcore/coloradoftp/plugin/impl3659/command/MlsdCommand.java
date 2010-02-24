package com.coldcore.coloradoftp.plugin.impl3659.command;

import com.coldcore.coloradoftp.command.Reply;
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
import java.util.Map;
import java.util.Set;

/**
 * Command MLST.
 * See FTP spec for details on the command.
 */
public class MlsdCommand extends BaseMlsxCommand {

  private static Logger log = Logger.getLogger(MlsdCommand.class);


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

    Session session = controlConnection.getSession();
    FileSystem fileSystem = (FileSystem) ObjectFactory.getObject(ObjectName.FILESYSTEM);

    String listDir = getParameter();
    String cdir = fileSystem.getCurrentDirectory(session);
    if (listDir.length() == 0) listDir = cdir;
    String pdir = fileSystem.getParent(listDir, session);

    Set<ListingFile> list = fileSystem.listDirectory(listDir, session);
    String alistDir = fileSystem.toAbsolute(listDir, session);
    String prepared = prepareList(list, alistDir, pdir);

    ByteArrayInputStream bin = new ByteArrayInputStream(prepared.getBytes());
    ReadableByteChannel rbc = Channels.newChannel(bin);

    session.setAttribute(SessionAttributeName.DATA_CONNECTION_MODE, DataConnectionMode.LIST);
    session.setAttribute(SessionAttributeName.DATA_CONNECTION_CHANNEL, rbc);

    if (!prepareForDataConnection()) return reply;

    reply.setCode("150");
    reply.setText("Opening A mode data connection for MLSD "+listDir+".");
    return reply;
  }


  /** Convert files list into MLSD format
   * @param list List of files and directories inside the listed directory
   * @param cdir Absolute current/listed directory (to set "type=cdir" fact)
   * @param pdir Absolute parent directory (to set "type=pdir" fact)
   * @return Prepared string
   */
  protected String prepareList(Set<ListingFile> list, String cdir, String pdir) {
    Session session = controlConnection.getSession();
    FileSystem fileSystem = (FileSystem) ObjectFactory.getObject(ObjectName.FILESYSTEM);

    StringBuffer sb = new StringBuffer();

    //Parent directory if present
    if (!pdir.equals(cdir)) {
      ListingFile lf = fileSystem.getPath(pdir, session);
      Map<String,String> pathFacts = getPathFacts(lf, pdir);
      pathFacts.put("type", "pdir");
      String pfacts = preparePathFacts(pathFacts);
      sb.append(pfacts).append(" ").append(pdir).append("\r\n");
    }

    //Current directory
    ListingFile clf = fileSystem.getPath(cdir, session);
    Map<String,String> pathFacts = getPathFacts(clf, cdir);
    String pfacts = preparePathFacts(pathFacts);
    sb.append(pfacts).append(" ").append(cdir).append("\r\n");

    //Listing
    for (ListingFile lf : list) {
      pathFacts = getPathFacts(lf, cdir);
      pfacts = preparePathFacts(pathFacts);
      sb.append(pfacts).append(" ").append(lf.getAbsolutePath()).append("\r\n");
    }

    log.debug("Directory listing:\n"+sb);
    return sb.toString();
  }
}
