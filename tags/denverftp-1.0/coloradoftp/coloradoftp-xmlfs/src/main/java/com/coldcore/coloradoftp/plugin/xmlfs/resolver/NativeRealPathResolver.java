package com.coldcore.coloradoftp.plugin.xmlfs.resolver;

import com.coldcore.coloradoftp.plugin.xmlfs.UserHome;
import com.coldcore.coloradoftp.plugin.xmlfs.VirtualFolder;
import org.apache.log4j.Logger;

import java.io.File;

/**
 * Uses java.io.File to perform virtual to real path mappings.
 */
public class NativeRealPathResolver implements RealPathResolver {

  private static Logger log = Logger.getLogger(NativeRealPathResolver.class);


  public String virtualPathToReal(String path, UserHome home) {
    if (path == null || path.length() == 0 || !path.startsWith("/")) {
      log.debug("Virtual path ["+path+"] cannot be converted to real");
      return null;
    }

    /* First we must determine where input points to. It can point to a root folder,
     * virtual folder, subfolder in user's home path or to a file in user's home path.
     * Then we must check if the path input points to exists on HDD.
     */

    //Points to a file in user home path
    File file =  new File(home.getPath()+"/"+path.substring(1));
    if (file.exists() && file.isFile()) {
      log.debug("Virtual path ["+path+"] converted to real ["+file.getAbsolutePath()+"]");
      return file.getAbsolutePath();
    }

    //Points to a root folder
    if (path.equals("/")) {
      file = new File(home.getPath());
      if (file.exists() && file.isDirectory()) {
        log.debug("Virtual path ["+path+"] converted to real ["+file.getAbsolutePath()+"]");
        return file.getAbsolutePath();
      }
      log.warn("User home does not exist: "+file.getAbsolutePath());
      return null;
    }

    //First folder is a virtual folder or sub folder?
    String firtFolderPath = null;
    String firtFolder = path.substring(1);
    if (firtFolder.indexOf("/") != -1) firtFolder = firtFolder.substring(0, firtFolder.indexOf("/"));

    VirtualFolder folder = home.getVirtualFolder(firtFolder);
    if (folder != null) {
      //First folder is a virtual folder
      firtFolderPath = folder.getPath();
    } else {
      //First folder is a sub folder or file in user's home path
      file = new File(home.getPath());
      if (file.exists() && file.isDirectory()) {
        File[] files = file.listFiles();
        for (File f : files)
          if (f.getName().equals(firtFolder) && f.isDirectory()) firtFolderPath = f.getAbsolutePath();
      }
    }
    if (firtFolderPath == null) {
      log.debug("Virtual path ["+path+"], first folder not found");
      return null;
    }

    //Test if path exists on a hard drive
    String relative = path.substring(1+firtFolder.length());
    String hddPath = firtFolderPath+"/"+relative;
    file =  new File(hddPath);
    if (file.exists()) {
      log.debug("Virtual path ["+path+"] converted to real ["+file.getAbsolutePath()+"]");
      return file.getAbsolutePath();
    }

    log.debug("Virtual path ["+path+"], real path does not exist: "+file.getAbsolutePath());
    return null;
  }

}