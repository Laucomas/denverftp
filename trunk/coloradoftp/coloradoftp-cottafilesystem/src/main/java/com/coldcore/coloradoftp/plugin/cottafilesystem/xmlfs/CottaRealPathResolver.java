package com.coldcore.coloradoftp.plugin.cottafilesystem.xmlfs;

import com.coldcore.coloradoftp.plugin.xmlfs.resolver.RealPathResolver;
import com.coldcore.coloradoftp.plugin.xmlfs.UserHome;
import com.coldcore.coloradoftp.plugin.xmlfs.VirtualFolder;
import org.apache.log4j.Logger;
import net.sf.cotta.*;

/**
 * Uses COTTA to perform virtual to real path mappings.
 */
public class CottaRealPathResolver implements RealPathResolver {

    private static Logger log = Logger.getLogger(RealPathResolver.class);
    private TFileFactory fileFactory;


    public CottaRealPathResolver(TFileFactory fileFactory) {
        this.fileFactory = fileFactory;
        if (fileFactory == null) throw new IllegalArgumentException("Invalid file factory");
    }


    /**
     * This method copies the logic, flow, structure, comments of the NativeRealPathresolver, but
     * it uses COTTA objects instead.
     */
    public String virtualPathToReal(String path, UserHome home) {
      if (path == null || path.length() == 0 || !path.startsWith("/")) {
        log.debug("Virtual path ["+path+"] cannot be converted to real");
        return null;
      }

      /* First we must determine where input points to. It can point to a root folder,
       * virtual folder, subfolder in user's home path or to a file in user's home path.
       * Then we must check if the path input points to exists on a back end file system.
       */

      //Points to a file in user home path
      TDirectory tdir;
      TFile tfile = fileFactory.file(home.getPath()+"/"+path.substring(1));
      if (tfile.exists()) { //Can only be file (as in the NativeRealPathresolver)
        log.debug("Virtual path ["+path+"] converted to real ["+tfile.toPath().toPathString()+"]");
        return tfile.toPath().toPathString();
      }

      //Points to a root folder
      if (path.equals("/")) {
        tdir = fileFactory.dir(home.getPath());
        if (tdir.exists()) { //Can only be directory (as in the NativeRealPathresolver)
          log.debug("Virtual path ["+path+"] converted to real ["+tdir.toPath().toPathString()+"]");
          return tdir.toPath().toPathString();
        }
        log.warn("User home does not exist: "+tdir.toPath().toPathString());
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
        tdir = fileFactory.dir(home.getPath());
        if (tdir.exists())  try { //Can only be directory (as in the NativeRealPathresolver)
          TDirectory[] dirs = tdir.listDirs();
          for (TDirectory d : dirs)
            if (d.name().equals(firtFolder)) firtFolderPath = d.toPath().toPathString();
        } catch (TIoException e) {throw new RuntimeException(e);} 
      }
      if (firtFolderPath == null) {
        log.debug("Virtual path ["+path+"], first folder not found");
        return null;
      }

      //Test if path exists on a back end file system (file or directory as in the NativeRealPathresolver)
      String relative = path.substring(1+firtFolder.length());
      tfile = fileFactory.file(firtFolderPath+"/"+relative);
      tdir = fileFactory.dir(firtFolderPath+"/"+relative);
      if (tfile.exists() || tdir.exists()) { //Both TFile and TDir have the same path, so we just use TFile
        log.debug("Virtual path ["+path+"] converted to real ["+tfile.toPath().toPathString()+"]");
        return tfile.toPath().toPathString();
      }

      log.debug("Virtual path ["+path+"], real path does not exist: "+tfile.toPath().toPathString());
      return null;
    }
}