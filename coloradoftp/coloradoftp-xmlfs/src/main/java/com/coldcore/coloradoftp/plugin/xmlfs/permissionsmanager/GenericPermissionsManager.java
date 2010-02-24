package com.coldcore.coloradoftp.plugin.xmlfs.permissionsmanager;

import com.coldcore.coloradoftp.plugin.xmlfs.*;
import com.coldcore.coloradoftp.plugin.xmlfs.adapter.FileAdapter;
import org.apache.log4j.Logger;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Generic permissions manager which deals with directories permissions (directory properties).
 *
 * Properties priority:
 *   ACCESS
 *     CREATE
 *       APPEND - files only
 *         OVERWRITE - files only
 *           RENAME
 *             DELETE
 *
 * All tests against regular expressions are sort of case-insensitive. All names (folder or file) are
 * compared after being lower-cased.
 */
public class GenericPermissionsManager implements PermissionsManager {

  private static Logger log = Logger.getLogger(GenericPermissionsManager.class);
  protected FileAdapter fileAdapter;


  public void setFileAdapter(FileAdapter fileAdapter) {
    this.fileAdapter = fileAdapter;
  }


    /** Find closest directory properties match for the given path.
   * Every user has a user home and a set of virtual folders and those have directory
   * properties. This method locates an object that contains the given path but is not
   * that path itself (except if the path is user home).
   * Note that this method does not work with paths ending with file separator.
   * @param path Path on a hard drive (absolute form, proper format)
   * @param home User home
   * @return Directory properties (read only!) or NULL if not configured
   */
  protected Set<DirProperty> findClosestProperties(String path, UserHome home) {
    if (path == null || path.length() == 0 || path.endsWith(fileAdapter.getSeparator())) {
      log.debug("Cannot find properties for path ["+path+"]");
      return null;
    }

    /* Find the closest configured parent directory and get its properties (we will just find
     * the longest path). We will use the parent of the path because parent's properties
     * contain rules for the path.
     */
    Set<DirProperty> properties = new LinkedHashSet<DirProperty>();
    int length = 0;
    String parent = fileAdapter.getParentPath(path);

    //Check if the path is within user home
    Set<DirProperty> set = home.getProperties();
    for (DirProperty dp : set) {
      if (parent.startsWith(dp.getDirectory())) {
        if (length > dp.getDirectory().length()) continue;
        if (length < dp.getDirectory().length()) properties.clear();
        properties.add(dp);
        length = dp.getDirectory().length();
      }
    }

    //Check if the path is within virtual folders
    for (VirtualFolder folder : home.getVirtualFolders()) {
      set = folder.getProperties();
      for (DirProperty dp : set) {
        if (parent.startsWith(dp.getDirectory())) {
          if (length > dp.getDirectory().length()) continue;
          if (length < dp.getDirectory().length()) properties.clear();
          properties.add(dp);
          length = dp.getDirectory().length();
        }
      }
    }

    //This path is not configured for the user
    if (properties.size() == 0) {
      log.debug("Path ["+path+"] not configured, no properties");
      return null;
    }

    log.debug("Path ["+path+"], "+properties.size()+" closest configured ["+properties.iterator().next().getDirectory()+"]");
    return properties;
  }


  public boolean canAccessDirectory(String dirname, UserHome home) {
    if (dirname == null || dirname.length() == 0 || dirname.endsWith(fileAdapter.getSeparator())) {
      log.debug("Can access directory ["+dirname+"]? cannot");
      return false;
    }

    /* By default everything is allowed.
     * We must check every directory property to see if any forbids this action.
     */
    Set<DirProperty> properties = findClosestProperties(dirname, home);
    if (properties != null)
      for (DirProperty dp : properties) {
        RegexpActionResult r = dp.isDirectoryAccessAllowed(dirname);
        if (r == RegexpActionResult.ALLOW_MATCH) break;
        if (r == RegexpActionResult.FORBID_MATCH) {
          log.debug("Can access directory ["+dirname+"]? no");
          return false;
        }
      }

    log.debug("Can access directory ["+dirname+"]? yes");
    return true;
  }


  public boolean canAccessFile(String filename, UserHome home) {
    if (filename == null || filename.length() == 0 || filename.endsWith(fileAdapter.getSeparator())) {
      log.debug("Can access file ["+filename+"]? cannot");
      return false;
    }

    /* By default everything is allowed.
     * We must check every directory property to see if any forbids this action.
     */
    Set<DirProperty> properties = findClosestProperties(filename, home);
    if (properties != null)
      for (DirProperty dp : properties) {
        RegexpActionResult r = dp.isFileAccessAllowed(filename);
        if (r == RegexpActionResult.ALLOW_MATCH) break;
        if (r == RegexpActionResult.FORBID_MATCH) {
          log.debug("Can access file ["+filename+"]? no");
          return false;
        }
      }

    log.debug("Can access file ["+filename+"]? yes");
    return true;
  }


  public boolean canListDirectory(String dirname, UserHome home) {
    if (dirname == null || dirname.length() == 0 || dirname.endsWith(fileAdapter.getSeparator())) {
      log.debug("Can list directory ["+dirname+"]? cannot");
      return false;
    }

    /* By default everything is allowed.
     * We must check every directory property to see if any forbids this action.
     */
    Set<DirProperty> properties = findClosestProperties(dirname, home);
    if (properties != null)
      for (DirProperty dp : properties) {
        RegexpActionResult r = dp.isDirectoryListingAllowed(dirname);
        if (r == RegexpActionResult.ALLOW_MATCH) break;
        if (r == RegexpActionResult.FORBID_MATCH) {
          log.debug("Can list directory ["+dirname+"]? no");
          return false;
        }
      }

    log.debug("Can list directory ["+dirname+"]? yes");
    return true;
  }


  public boolean canListFile(String filename, UserHome home) {
    if (filename == null || filename.length() == 0 || filename.endsWith(fileAdapter.getSeparator())) {
      log.debug("Can list file ["+filename+"]? cannot");
      return false;
    }

    /* By default everything is allowed.
     * We must check every directory property to see if any forbids this action.
     */
    Set<DirProperty> properties = findClosestProperties(filename, home);
    if (properties != null)
      for (DirProperty dp : properties) {
        RegexpActionResult r = dp.isFileListingAllowed(filename);
        if (r == RegexpActionResult.ALLOW_MATCH) break;
        if (r == RegexpActionResult.FORBID_MATCH) {
          log.debug("Can list file ["+filename+"]? no");
          return false;
        }
      }

    log.debug("Can list file ["+filename+"]? yes");
    return true;
  }


  public boolean canDeleteDirectory(String dirname, UserHome home) {
    if (dirname == null || dirname.length() == 0 || dirname.endsWith(fileAdapter.getSeparator())) {
      log.debug("Can delete directory ["+dirname+"]? cannot");
      return false;
    }

    /* By default everything is allowed.
     * We must check every directory property to see if any forbids this action.
     */
    Set<DirProperty> properties = findClosestProperties(dirname, home);
    if (properties != null)
      for (DirProperty dp : properties) {
        RegexpActionResult r = dp.isDirectoryDeleteAllowed(dirname);
        if (r == RegexpActionResult.ALLOW_MATCH) break;
        if (r == RegexpActionResult.FORBID_MATCH) {
          log.debug("Can delete directory ["+dirname+"]? no");
          return false;
        }
      }

    if (!canRenameDirectory(dirname, home)) return false;

    log.debug("Can delete directory ["+dirname+"]? yes");
    return true;
  }


  public boolean canDeleteFile(String filename, UserHome home) {
    if (filename == null || filename.length() == 0 || filename.endsWith(fileAdapter.getSeparator())) {
      log.debug("Can delete file ["+filename+"]? cannot");
      return false;
    }

    /* By default everything is allowed.
     * We must check every directory property to see if any forbids this action.
     */
    Set<DirProperty> properties = findClosestProperties(filename, home);
    if (properties != null)
      for (DirProperty dp : properties) {
        RegexpActionResult r = dp.isFileDeleteAllowed(filename);
        if (r == RegexpActionResult.ALLOW_MATCH) break;
        if (r == RegexpActionResult.FORBID_MATCH) {
          log.debug("Can delete file ["+filename+"]? no");
          return false;
        }
      }

    if (!canRenameFile(filename, home)) return false;

    log.debug("Can delete file ["+filename+"]? yes");
    return true;
  }


  public boolean canRenameDirectory(String dirname, UserHome home) {
    if (dirname == null || dirname.length() == 0 || dirname.endsWith(fileAdapter.getSeparator())) {
      log.debug("Can rename directory ["+dirname+"]? cannot");
      return false;
    }

    /* By default everything is allowed.
     * We must check every directory property to see if any forbids this action.
     */
    Set<DirProperty> properties = findClosestProperties(dirname, home);
    if (properties != null) {
      for (DirProperty dp : properties) {
        RegexpActionResult r = dp.isDirectoryRenameAllowed(dirname);
        if (r == RegexpActionResult.ALLOW_MATCH) break;
        if (r == RegexpActionResult.FORBID_MATCH) {
          log.debug("Can rename directory ["+dirname+"]? no");
          return false;
        }
      }
    }

    if (!canCreateDirectory(dirname, home)) return false;

    log.debug("Can rename directory ["+dirname+"]? yes");
    return true;
  }


  public boolean canRenameFile(String filename, UserHome home) {
    if (filename == null || filename.length() == 0 || filename.endsWith(fileAdapter.getSeparator())) {
      log.debug("Can rename file ["+filename+"]? cannot");
      return false;
    }

    /* By default everything is allowed.
     * We must check every directory property to see if any forbids this action.
     */
    Set<DirProperty> properties = findClosestProperties(filename, home);
    if (properties != null)
      for (DirProperty dp : properties) {
        RegexpActionResult r = dp.isFileRenameAllowed(filename);
        if (r == RegexpActionResult.ALLOW_MATCH) break;
        if (r == RegexpActionResult.FORBID_MATCH) {
          log.debug("Can rename file ["+filename+"]? no");
          return false;
        }
      }

    if (!canOverwriteFile(filename, home)) return false;

    log.debug("Can rename file ["+filename+"]? yes");
    return true;
  }


  public boolean canCreateDirectory(String dirname, UserHome home) {
    if (dirname == null || dirname.length() == 0 || dirname.endsWith(fileAdapter.getSeparator())) {
      log.debug("Can create directory ["+dirname+"]? cannot");
      return false;
    }

    /* By default everything is allowed.
     * We must check every directory property to see if any forbids this action.
     */
    Set<DirProperty> properties = findClosestProperties(dirname, home);
    if (properties != null)
      for (DirProperty dp : properties) {
        RegexpActionResult r = dp.isDirectoryCreationAllowed(dirname);
        if (r == RegexpActionResult.ALLOW_MATCH) break;
        if (r == RegexpActionResult.FORBID_MATCH) {
          log.debug("Can create directory ["+dirname+"]? no");
          return false;
        }
      }

    if (!canAccessDirectory(dirname, home)) return false;

    log.debug("Can create directory ["+dirname+"]? yes");
    return true;
  }


  public boolean canCreateFile(String filename, UserHome home) {
    if (filename == null || filename.length() == 0 || filename.endsWith(fileAdapter.getSeparator())) {
      log.debug("Can create file ["+filename+"]? cannot");
      return false;
    }

    /* By default everything is allowed.
     * We must check every directory property to see if any forbids this action.
     */
    Set<DirProperty> properties = findClosestProperties(filename, home);
    if (properties != null)
      for (DirProperty dp : properties) {
        RegexpActionResult r = dp.isFileCreationAllowed(filename);
        if (r == RegexpActionResult.ALLOW_MATCH) break;
        if (r == RegexpActionResult.FORBID_MATCH) {
          log.debug("Can create file ["+filename+"]? no");
          return false;
        }
      }

    if (!canAccessFile(filename, home)) return false;

    log.debug("Can create file ["+filename+"]? yes");
    return true;
  }


  public boolean canAppendFile(String filename, UserHome home) {
    if (filename == null || filename.length() == 0 || filename.endsWith(fileAdapter.getSeparator())) {
      log.debug("Can append file ["+filename+"]? cannot");
      return false;
    }

    /* By default everything is allowed.
     * We must check every directory property to see if any forbids this action.
     */
    Set<DirProperty> properties = findClosestProperties(filename, home);
    if (properties != null)
      for (DirProperty dp : properties) {
        RegexpActionResult r = dp.isFileAppendAllowed(filename);
        if (r == RegexpActionResult.ALLOW_MATCH) break;
        if (r == RegexpActionResult.FORBID_MATCH) {
          log.debug("Can append file ["+filename+"]? no");
          return false;
        }
      }

    if (!canCreateFile(filename, home)) return false;

    log.debug("Can append file ["+filename+"]? yes");
    return true;
  }


  public boolean canOverwriteFile(String filename, UserHome home) {
    if (filename == null || filename.length() == 0 || filename.endsWith(fileAdapter.getSeparator())) {
      log.debug("Can overwrite file ["+filename+"]? cannot");
      return false;
    }

    /* By default everything is allowed.
     * We must check every directory property to see if any forbids this action.
     */
    Set<DirProperty> properties = findClosestProperties(filename, home);
    if (properties != null)
      for (DirProperty dp : properties) {
        RegexpActionResult r = dp.isFileOverwriteAllowed(filename);
        if (r == RegexpActionResult.ALLOW_MATCH) break;
        if (r == RegexpActionResult.FORBID_MATCH) {
          log.debug("Can overwrite file ["+filename+"]? no");
          return false;
        }
      }

    if (!canAppendFile(filename, home)) return false;

    log.debug("Can overwrite file ["+filename+"]? yes");
    return true;
  }
}