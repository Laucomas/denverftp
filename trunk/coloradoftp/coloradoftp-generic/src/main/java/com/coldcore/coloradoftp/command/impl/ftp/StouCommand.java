/**
 * Command STOU.
 * See FTP spec for details on the command.
 */
package com.coldcore.coloradoftp.command.impl.ftp;

import com.coldcore.coloradoftp.connection.DataConnectionMode;
import com.coldcore.coloradoftp.factory.ObjectFactory;
import com.coldcore.coloradoftp.factory.ObjectName;
import com.coldcore.coloradoftp.filesystem.FileSystem;
import com.coldcore.coloradoftp.filesystem.ListingFile;
import com.coldcore.coloradoftp.session.Session;
import org.apache.log4j.Logger;

public class StouCommand extends StorCommand {

  private static Logger log = Logger.getLogger(StouCommand.class);


  protected String getFilename() {
    Session session = controlConnection.getSession();
    FileSystem fileSystem = (FileSystem) ObjectFactory.getObject(ObjectName.FILESYSTEM);

    //Construct filename (must have the same name as the current folder user is in)
    String curDir = fileSystem.getCurrentDirectory(session);
    String parent = fileSystem.getParent(curDir, session);
    ListingFile lf = fileSystem.getPath(curDir, session);
    String filename = lf.getAbsolutePath().equals(parent) ? "" : lf.getName();

    log.debug("Constructed filename: "+filename);
    return filename;
  }


  protected DataConnectionMode getDataConnectionMode() {
    return DataConnectionMode.STOU;
  }
}
