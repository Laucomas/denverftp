package com.coldcore.coloradoftp.plugin.cottafilesystem;

import com.coldcore.coloradoftp.filesystem.FailedActionException;
import com.coldcore.coloradoftp.filesystem.FailedActionReason;
import com.coldcore.coloradoftp.filesystem.FileSystem;
import com.coldcore.coloradoftp.filesystem.ListingFile;
import com.coldcore.coloradoftp.filesystem.impl.ListingFileBean;
import com.coldcore.coloradoftp.plugin.xmlfs.User;
import com.coldcore.coloradoftp.plugin.xmlfs.VirtualFolder;
import com.coldcore.coloradoftp.plugin.xmlfs.XmlFS;
import com.coldcore.coloradoftp.plugin.xmlfs.parser.ParsingException;
import com.coldcore.coloradoftp.plugin.cottafilesystem.xmlfs.CottaRealPathResolver;
import com.coldcore.coloradoftp.plugin.cottafilesystem.xmlfs.CottaFileAdapter;
import com.coldcore.coloradoftp.session.Session;
import com.coldcore.coloradoftp.session.SessionAttributeName;
import org.apache.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.channels.Channels;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import net.sf.cotta.TFileFactory;
import net.sf.cotta.TDirectory;
import net.sf.cotta.TFile;
import net.sf.cotta.TIoException;
import net.sf.cotta.io.OutputMode;

/**
 * @see com.coldcore.coloradoftp.filesystem.FileSystem
 *
 * This implementation is mounted to the COTTA back end and using the XmlFS for permissions and mappings.
 *
 * This class uses unix style: "/" is a root directory and "/" is a file separator.
 * This is for FTP clients - the way they display directories and files.
 *
 * XmlFS dictates:
 * Every user starts in his own "/" root directory. This appears as a root directory of the server but
 * it can be just anywhere in the actual filesystem. User cannot go outside of his/her home directory,
 * but virtual folders can be mounted to user's home allowing user to travel anywhere withing the actual
 * filesystem.
 */
public class CottaFileSystem implements FileSystem {

  private static Logger log = Logger.getLogger(CottaFileSystem.class);
  protected XmlFS xmlFS;
  protected String filesOwner;
  protected String filesPermissions;
  protected String mlsxFileFacts;
  protected String mlsxDirFacts;
  private TFileFactory fileFactory;


  public CottaFileSystem(String filename, TFileFactory fileFactory) throws FileNotFoundException, ParsingException {
    this.fileFactory = fileFactory;
    if (fileFactory == null) throw new IllegalArgumentException("Invalid file factory");

    CottaRealPathResolver realPathResolver = new CottaRealPathResolver(fileFactory);
    CottaFileAdapter fileAdapter = new CottaFileAdapter();

    xmlFS = new XmlFS();
    xmlFS.setRealPathResolver(realPathResolver);
    xmlFS.setFileAdapter(fileAdapter);
    xmlFS.initialize(filename);

    filesOwner = "ftp";
    filesPermissions = "rwxrwxrwx";
    mlsxDirFacts = "cdeflp";
    mlsxFileFacts = "adfrw";
  }


  /** Default files owner when listing directory content
   * @param owner Owner name
   */
  public void setFilesOwner(String owner) {
    if (owner == null) throw new IllegalArgumentException("Invalid owner");
    filesOwner = owner;
  }


  /** Default files owner when listing directory content
   * @return Owner name
   */
  public String getFilesOwner() {
    return filesOwner;
  }


  /** Default files permissions when listing directory content
   * @param permissions Permissions
   */
  public void setFilesPermissions(String permissions) {
    if (permissions == null) throw new IllegalArgumentException("Invalid permissions");
    filesPermissions = permissions;
  }


  /** Default files permissions when listing directory content
   * @return Permissions
   */
  public String getFilesPermissions() {
    return filesPermissions;
  }


  public String getCurrentDirectory(Session userSession) {
    return xmlFS.getVirtualPathResolver().getCurrentVirtualDirectory(userSession);
  }


  public String toAbsolute(String path, Session userSession) throws FailedActionException {
    String apath = xmlFS.getVirtualPathResolver().virtualPathToAbsolute(path, userSession);
    if (apath == null)
      throw new FailedActionException(FailedActionReason.SYSTEM_ERROR, "Cannot convert path.");
    return apath;
  }


  public String getParent(String path, Session userSession) throws FailedActionException {
    String apath = toAbsolute(path, userSession);
    return xmlFS.getVirtualPathResolver().getVirtualParent(apath);
  }


  public Set<ListingFile> listDirectory(String dir, Session userSession) {
    User user = xmlFS.findUser(userSession);
    if (user == null)
      throw new FailedActionException(FailedActionReason.OTHER, "No file system entry.");

    String apath = xmlFS.getVirtualPathResolver().virtualPathToAbsolute(dir, userSession);
    String rpath = xmlFS.getRealPathResolver().virtualPathToReal(apath, user.getHome());
    if (rpath == null)
      throw new FailedActionException(FailedActionReason.PATH_ERROR, "Path not found.");

    //Permissions
    if (!xmlFS.getPermissionsManager().canAccessDirectory(rpath, user.getHome()))
      throw new FailedActionException(FailedActionReason.NO_PERMISSIONS, "Access denied.");

    TDirectory tdir = fileFactory.dir(rpath);
    if (!tdir.exists())
      throw new FailedActionException(FailedActionReason.PATH_ERROR, "Path is a file.");

    Set<ListingFile> set = new HashSet<ListingFile>();
    TDirectory[] dlist;
    TFile[] flist;
    try {
      dlist = tdir.listDirs();
      flist = tdir.listFiles();
    } catch (TIoException e) {
      log.error("Cannot list path", e);
      throw new FailedActionException(FailedActionReason.SYSTEM_ERROR);
    }
    for (TDirectory d : dlist) {
      //Test if listing is allowed
      if (!xmlFS.getPermissionsManager().canListDirectory(d.toPath().toPathString(), user.getHome())) continue;
      String fapath = xmlFS.getVirtualPathResolver().virtualPathToAbsolute(apath+"/"+d.name(), userSession);
      String frpath = d.toPath().toPathString();
      ListingFile lf = createListingFile(fapath, frpath);
      if (lf != null) set.add(lf);
    }
    for (TFile f : flist) {
      //Test if listing is allowed
      if (!xmlFS.getPermissionsManager().canListFile(f.toPath().toPathString(), user.getHome())) continue;
      String fapath = xmlFS.getVirtualPathResolver().virtualPathToAbsolute(apath+"/"+f.name(), userSession);
      String frpath = f.toPath().toPathString();
      ListingFile lf = createListingFile(fapath, frpath);
      if (lf != null) set.add(lf);
    }

    //Add virtual folders
    if (apath.equals("/")) {
      Set<VirtualFolder> vfolders = user.getHome().getVirtualFolders();
      for (VirtualFolder vf : vfolders) {
        ListingFile lf = createListingFile(apath, vf.getPath());
        if (lf != null) {
          lf.setName(vf.getName());
          lf.setAbsolutePath("/"+vf.getName());
          set.add(lf);
        }
      }
    }

    log.debug("Directory listed: "+rpath);

    return set;
  }


  public ListingFile getPath(String path, Session userSession) throws FailedActionException {
    User user = xmlFS.findUser(userSession);
    if (user == null)
      throw new FailedActionException(FailedActionReason.OTHER, "No file system entry.");

    String apath = xmlFS.getVirtualPathResolver().virtualPathToAbsolute(path, userSession);
    String rpath = xmlFS.getRealPathResolver().virtualPathToReal(apath, user.getHome());
    if (rpath == null)
      throw new FailedActionException(FailedActionReason.PATH_ERROR, "Path not found.");

    return createListingFile(apath, rpath);
  }


  /** Create listing file object
   * @param apath Absolute virtual path
   * @param rpath Absolute real path
   * @return Listing file or NULL if does not exist or cannot be converted
   */
  protected ListingFile createListingFile(String apath, String rpath) {
    TDirectory tdir = fileFactory.dir(rpath);
    TFile tfile = fileFactory.file(rpath);
    if (!tdir.exists() && !tfile.exists()) return null;
    boolean isFile = tfile.exists();

    ListingFile lf = new ListingFileBean();
    lf.setName(apath.equals("/")?"/":isFile?tfile.name():tdir.name());
    lf.setAbsolutePath(apath);
    lf.setOwner(filesOwner);
    lf.setDirectory(!isFile);
    lf.setPermissions(filesPermissions);
    if (isFile) lf.setSize(tfile.length());
    lf.setLastModified(new Date()); //No modified date? Damn you COTTA!
    lf.setMlsxFacts(!isFile?mlsxDirFacts:mlsxFileFacts);

    return lf;
  }


  public String changeDirectory(String dir, Session userSession) {
    User user = xmlFS.findUser(userSession);
    if (user == null)
      throw new FailedActionException(FailedActionReason.OTHER, "No file system entry.");

    String apath = xmlFS.getVirtualPathResolver().virtualPathToAbsolute(dir, userSession);
    String rpath = xmlFS.getRealPathResolver().virtualPathToReal(apath, user.getHome());
    if (rpath == null)
      throw new FailedActionException(FailedActionReason.PATH_ERROR, "Path not found.");

    //Permissions
    if (!xmlFS.getPermissionsManager().canAccessDirectory(rpath, user.getHome()))
      throw new FailedActionException(FailedActionReason.NO_PERMISSIONS, "Access denied.");

    TDirectory tdir = fileFactory.dir(rpath);
    if (!tdir.exists())
      throw new FailedActionException(FailedActionReason.PATH_ERROR, "Path is a file.");

    userSession.setAttribute(SessionAttributeName.CURRENT_DIRECTORY, apath);
    log.debug("User switched to directory: "+rpath);

    return apath;
  }


  public void deletePath(String path, Session userSession) {
    User user = xmlFS.findUser(userSession);
    if (user == null)
      throw new FailedActionException(FailedActionReason.OTHER, "No file system entry.");

    if (path.endsWith("/"))
      throw new FailedActionException(FailedActionReason.INVALID_INPUT, "Invalid path.");

    String apath = xmlFS.getVirtualPathResolver().virtualPathToAbsolute(path, userSession);
    String rpath = xmlFS.getRealPathResolver().virtualPathToReal(apath, user.getHome());
    if (rpath == null)
      throw new FailedActionException(FailedActionReason.PATH_ERROR, "Path not found.");

    TDirectory tdir = fileFactory.dir(rpath);
    TFile tfile = fileFactory.file(rpath);
    boolean isFile = tfile.exists();

    //Permissions
    boolean allowed;
    allowed = !isFile ?
            xmlFS.getPermissionsManager().canDeleteDirectory(rpath, user.getHome()) :
            xmlFS.getPermissionsManager().canDeleteFile(rpath, user.getHome());
    if (!allowed)
      throw new FailedActionException(FailedActionReason.NO_PERMISSIONS, "Access denied.");

    if (xmlFS.getVirtualPathResolver().isVirtualFolder(apath, user.getHome()))
      throw new FailedActionException(FailedActionReason.NO_PERMISSIONS, "Path is a virtual folder.");

    //todo file locks check

    try {
      if (isFile) tfile.delete();
      else tdir.deleteAll();
    } catch (TIoException e) {
      log.error("Cannot delete path", e);
      throw new FailedActionException(FailedActionReason.SYSTEM_ERROR);
    }

    log.debug("Path deleted: "+rpath);
  }


  public String createDirectory(String dir, Session userSession) {
    User user = xmlFS.findUser(userSession);
    if (user == null)
      throw new FailedActionException(FailedActionReason.OTHER, "No file system entry.");

    if (dir.endsWith("/"))
      throw new FailedActionException(FailedActionReason.INVALID_INPUT, "Invalid directory name.");

    String apath = xmlFS.getVirtualPathResolver().virtualPathToAbsolute(dir, userSession);
    String rpath = xmlFS.getRealPathResolver().virtualPathToReal(apath, user.getHome());
    if (rpath != null)
      throw new FailedActionException(FailedActionReason.PATH_ERROR, "Path already exists.");

    if (xmlFS.getVirtualPathResolver().isVirtualFolder(apath, user.getHome()))
      throw new FailedActionException(FailedActionReason.PATH_ERROR, "Path already exists.");

    String parent = getParent(apath, userSession);
    rpath = xmlFS.getRealPathResolver().virtualPathToReal(parent, user.getHome());
    if (rpath == null)
      throw new FailedActionException(FailedActionReason.PATH_ERROR, "Directory not found.");

    rpath = rpath+"/"+apath.substring(apath.lastIndexOf("/")+1);
    TDirectory tdir = fileFactory.dir(rpath);
    rpath = tdir.toPath().toPathString();

    //Permissions
    if (!xmlFS.getPermissionsManager().canCreateDirectory(rpath, user.getHome()))
      throw new FailedActionException(FailedActionReason.NO_PERMISSIONS, "Access denied.");

    try {
      tdir.ensureExists();
    } catch (TIoException e) {
      log.error("Cannot create directory", e);
      throw new FailedActionException(FailedActionReason.SYSTEM_ERROR);
    }

    log.debug("Directory created: "+rpath);

    return apath;
  }


  public String renamePath(String from, String to, Session userSession) {
    User user = xmlFS.findUser(userSession);
    if (user == null)
      throw new FailedActionException(FailedActionReason.OTHER, "No file system entry.");

    if (from.endsWith("/"))
      throw new FailedActionException(FailedActionReason.INVALID_INPUT, "Invalid path name.");

    if (to.endsWith("/"))
      throw new FailedActionException(FailedActionReason.INVALID_INPUT, "Invalid path name.");

    String apathF = xmlFS.getVirtualPathResolver().virtualPathToAbsolute(from, userSession);
    String rpathF = xmlFS.getRealPathResolver().virtualPathToReal(apathF, user.getHome());
    if (rpathF == null)
      throw new FailedActionException(FailedActionReason.PATH_ERROR, "Path not found.");

    String apathT = xmlFS.getVirtualPathResolver().virtualPathToAbsolute(to, userSession);
    String rpathT = xmlFS.getRealPathResolver().virtualPathToReal(apathT, user.getHome());
    if (rpathT != null)
      throw new FailedActionException(FailedActionReason.PATH_ERROR, "Path already exists.");

    if (xmlFS.getVirtualPathResolver().isVirtualFolder(apathF, user.getHome()))
      throw new FailedActionException(FailedActionReason.NO_PERMISSIONS, "Path is a virtual folder.");

    if (xmlFS.getVirtualPathResolver().isVirtualFolder(apathT, user.getHome()))
      throw new FailedActionException(FailedActionReason.PATH_ERROR, "Path already exists.");

    TDirectory tdirF = fileFactory.dir(rpathF);
    TFile tfileF = fileFactory.file(rpathF);
    boolean isFileF = tfileF.exists();

    String parent = getParent(apathT, userSession);
    rpathT = xmlFS.getRealPathResolver().virtualPathToReal(parent, user.getHome());
    if (rpathT == null)
      throw new FailedActionException(FailedActionReason.PATH_ERROR, "Directory not found.");

    rpathT = rpathT+"/"+apathT.substring(apathT.lastIndexOf("/")+1);
    TDirectory tdirT = fileFactory.dir(rpathT);
    TFile tfileT = fileFactory.file(rpathT);
    rpathT = tfileT.toPath().toPathString(); //Both TFile and TDir have the same path, so we just use TFile

    //Permissions
    boolean allowed;
    allowed = !isFileF ?
            xmlFS.getPermissionsManager().canRenameDirectory(rpathF, user.getHome()) && xmlFS.getPermissionsManager().canCreateDirectory(rpathT, user.getHome()) :
            xmlFS.getPermissionsManager().canRenameFile(rpathF, user.getHome()) && xmlFS.getPermissionsManager().canCreateFile(rpathT, user.getHome());
    if (!allowed)
      throw new FailedActionException(FailedActionReason.NO_PERMISSIONS, "Access denied.");

    try {
      if (isFileF) tfileF.moveTo(tfileT);
      else tdirF.moveTo(tdirT);
    } catch (TIoException e) {
      log.error("Cannot move path", e);
      throw new FailedActionException(FailedActionReason.SYSTEM_ERROR);
    }

    log.debug("Path ("+rpathF+") renamed: "+rpathT);

    return apathT;
  }


  public ReadableByteChannel readFile(String filename, long position, Session userSession) {
    User user = xmlFS.findUser(userSession);
    if (user == null)
      throw new FailedActionException(FailedActionReason.OTHER, "No file system entry.");

    if (filename.endsWith("/"))
      throw new FailedActionException(FailedActionReason.INVALID_INPUT, "Invalid file name.");

    String apath = xmlFS.getVirtualPathResolver().virtualPathToAbsolute(filename, userSession);
    String rpath = xmlFS.getRealPathResolver().virtualPathToReal(apath, user.getHome());
    if (rpath == null)
      throw new FailedActionException(FailedActionReason.PATH_ERROR, "File not found.");

    TFile tfile = fileFactory.file(rpath);
    if (!tfile.exists())
      throw new FailedActionException(FailedActionReason.PATH_ERROR, "Path is a directory.");

    //Permissions
    if (!xmlFS.getPermissionsManager().canAccessFile(rpath, user.getHome()))
      throw new FailedActionException(FailedActionReason.NO_PERMISSIONS, "Access denied.");

    //todo file locks

    ReadableByteChannel fc;
    try {
      InputStream in = tfile.io().inputStream();
      while (position-- > 0)
        in.read();
      fc = Channels.newChannel(in);
    } catch (Throwable e) {
      log.error("Cannot create (r) file channel", e);
      throw new FailedActionException(FailedActionReason.SYSTEM_ERROR);
    }

    //Just in case, to be used by external components
    userSession.setAttribute("transferred.file", tfile);

    log.debug("File channel (r) mounted to: "+rpath);
    return fc;
  }


  public WritableByteChannel saveFile(String filename, boolean append, Session userSession) {
    User user = xmlFS.findUser(userSession);
    if (user == null)
      throw new FailedActionException(FailedActionReason.OTHER, "No file system entry.");

    if (filename.endsWith("/"))
      throw new FailedActionException(FailedActionReason.INVALID_INPUT, "Invalid file name.");

    String apath = xmlFS.getVirtualPathResolver().virtualPathToAbsolute(filename, userSession);
    String parent = getParent(apath, userSession);
    String rpath = xmlFS.getRealPathResolver().virtualPathToReal(parent, user.getHome());
    if (rpath == null)
      throw new FailedActionException(FailedActionReason.PATH_ERROR, "Directory not found.");

    rpath = rpath+"/"+apath.substring(apath.lastIndexOf("/")+1);
    TDirectory tdir = fileFactory.dir(rpath);
    TFile tfile = fileFactory.file(rpath);
    rpath = tfile.toPath().toPathString();

    if (tdir.exists())
      throw new FailedActionException(FailedActionReason.PATH_ERROR, "Path already exists.");

    //Permissions
    boolean allowed;
    allowed = (tfile.exists() && append && xmlFS.getPermissionsManager().canAppendFile(rpath, user.getHome())) ||
              (tfile.exists() && !append && xmlFS.getPermissionsManager().canOverwriteFile(rpath, user.getHome())) ||
              (!tfile.exists() && xmlFS.getPermissionsManager().canCreateFile(rpath, user.getHome()));
    if (!allowed)
      throw new FailedActionException(FailedActionReason.NO_PERMISSIONS, "Access denied.");

    //todo file locks

    WritableByteChannel fc;
    try {
      OutputStream out = tfile.io().outputStream(append ? OutputMode.APPEND : OutputMode.OVERWRITE);
      fc = Channels.newChannel(out);
    } catch (Throwable e) {
      log.error("Cannot create (w) file channel", e);
      throw new FailedActionException(FailedActionReason.SYSTEM_ERROR);
    }

    //Just in case, to be used by external components
    userSession.setAttribute("transferred.file", tfile);

    log.debug("File channel (w) mounted to: "+rpath);
    return fc;
  }


  public String getFileSeparator() {
    return xmlFS.getVirtualPathResolver().getVirtualFileSeparator();
  }
}