package com.coldcore.coloradoftp.plugin.impl3659.command;

import com.coldcore.coloradoftp.command.Command;
import com.coldcore.coloradoftp.command.Reply;
import com.coldcore.coloradoftp.factory.ObjectFactory;
import com.coldcore.coloradoftp.factory.ObjectName;
import com.coldcore.coloradoftp.filesystem.FileSystem;
import com.coldcore.coloradoftp.filesystem.ListingFile;
import com.coldcore.coloradoftp.session.Session;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Command MLST.
 * See FTP spec for details on the command.
 */
public class MlstCommand extends BaseMlsxCommand {

  private static Logger log = Logger.getLogger(MlstCommand.class);


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

    String path = getParameter();
    String cdir = fileSystem.getCurrentDirectory(session);
    if (path.length() == 0) path = cdir;

    ListingFile lf = fileSystem.getPath(path, session);
    Map<String,String> pathFacts = getPathFacts(lf, cdir);
    String pfacts = preparePathFacts(pathFacts);

    StringBuffer sb = new StringBuffer();
    sb.append("Listing ").append(path).append("\r\n");
    sb.append(pfacts).append(" ").append(lf.getAbsolutePath()).append("\r\n");
    sb.append("End");

    reply.setCode("250");
    reply.setText(sb.toString());
    return reply;
  }


  public Reply executeOnParent(Command parent) {
    if (parent.getName().equals("OPTS")) {
      return executeOPTS();
    }

    if (parent.getName().equals("FEAT")) {
      return executeFEAT();
    }

    return null;
  }


  /** Execute as a reply to the OPTS command
   * @return Reply
   */
  protected Reply executeOPTS() {
    Reply reply = getReply();

    String param = getParameter();
    if (param.length() > 0 && (param.indexOf(" ") != -1 || param.indexOf(";") == -1)) {
      reply.setCode("501");
      reply.setText("Invalid MLST options");
      return reply;
    }

    //Clear the facts set and then re-fill it again
    Set<String> selectedFacts = getSelectedFacts();
    selectedFacts.clear();

    if (param.length() > 0) {
      StringTokenizer st = new StringTokenizer(param, ";");
      while (st.hasMoreTokens()) {
        String s = st.nextToken().trim().toLowerCase();
        if (facts.contains(s)) selectedFacts.add(s);
      }
    }

    StringBuffer sb = new StringBuffer("MLST OPTS");
    if (!selectedFacts.isEmpty()) sb.append(" ");
    for (String fact : selectedFacts)
      sb.append(fact).append(";");

    reply.setCode("200");
    reply.setText(sb.toString());
    return reply;
  }


  /** Execute as a reply to the FEAT command
   * @return Reply
   */
  protected Reply executeFEAT() {
    Reply reply = getReply();

    Set<String> selectedFacts = getSelectedFacts();

    StringBuffer sb = new StringBuffer("Features supported\r\n");
    sb.append("MLST");
    if (!facts.isEmpty()) sb.append(" ");
    for (String fact : facts) {
      sb.append(fact);
      if (selectedFacts.contains(fact)) sb.append("*");
      sb.append(";");
    }
    sb.append("\r\nend");

    reply.setCode("211");
    reply.setText(sb.toString());
    return reply;
  }
}
