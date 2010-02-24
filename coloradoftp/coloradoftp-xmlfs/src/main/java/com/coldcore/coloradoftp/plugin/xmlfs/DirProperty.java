package com.coldcore.coloradoftp.plugin.xmlfs;

import com.coldcore.misc5.Syntax;
import com.coldcore.coloradoftp.plugin.xmlfs.adapter.FileAdapter;
import org.apache.log4j.Logger;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Directory properties.
 *
 * Based on supplied configuration and regular expressions this class can tell
 * wether certain actions are allowed to be performed in a particular directory and
 * in its sub directories.
 *
 * Properties are considered in the order as they were added. The closer the property
 * to the begining of the list, the more superior it is.
 *
 * This class works with real paths, not virtual.
 *
 * This class is thread safe if all its maps are initialized once with all regular expressions
 * and not modified later.
 */
public class DirProperty {

  private static Logger log = Logger.getLogger(DirProperty.class);
  protected String directory;
  protected boolean spread;
  protected Map<String,Boolean> accessFolderRegexp;
  protected Map<String,Boolean> accessFileRegexp;
  protected Map<String,Boolean> listFolderRegexp;
  protected Map<String,Boolean> listFileRegexp;
  protected Map<String,Boolean> createFolderRegexp;
  protected Map<String,Boolean> createFileRegexp;
  protected Map<String,Boolean> renameFolderRegexp;
  protected Map<String,Boolean> renameFileRegexp;
  protected Map<String,Boolean> deleteFolderRegexp;
  protected Map<String,Boolean> deleteFileRegexp;
  protected Map<String,Boolean> appendFileRegexp;
  protected Map<String,Boolean> overwriteFileRegexp;
  protected FileAdapter fileAdapter;


  public DirProperty(FileAdapter fileAdapter) {
    if (fileAdapter == null) throw new IllegalArgumentException("Invalid file adapter");
    this.fileAdapter = fileAdapter;
    accessFolderRegexp = new LinkedHashMap<String,Boolean>();
    accessFileRegexp = new LinkedHashMap<String,Boolean>();
    listFolderRegexp = new LinkedHashMap<String,Boolean>();
    listFileRegexp = new LinkedHashMap<String,Boolean>();
    createFolderRegexp = new LinkedHashMap<String,Boolean>();
    createFileRegexp = new LinkedHashMap<String,Boolean>();
    renameFolderRegexp = new LinkedHashMap<String,Boolean>();
    renameFileRegexp = new LinkedHashMap<String,Boolean>();
    deleteFolderRegexp = new LinkedHashMap<String,Boolean>();
    deleteFileRegexp = new LinkedHashMap<String,Boolean>();
    appendFileRegexp = new LinkedHashMap<String,Boolean>();
    overwriteFileRegexp = new LinkedHashMap<String,Boolean>();
  }


  /** Add expressions to folder access (directory content)
   * @param map Ordered expressions
   */
  public void addAccessFolderRegexp(Map<String,Boolean> map) {
    accessFolderRegexp.putAll(map);
  }


  /** Add expressions to file access (directory content)
   * @param map Ordered expressions
   */
  public void addAccessFileRegexp(Map<String,Boolean> map) {
    accessFileRegexp.putAll(map);
  }


  /** Add expressions to folder listing (directory content)
   * @param map Ordered expressions
   */
  public void addListFolderRegexp(Map<String,Boolean> map) {
    listFolderRegexp.putAll(map);
  }


  /** Add expressions to file listing (directory content)
   * @param map Ordered expressions
   */
  public void addListFileRegexp(Map<String,Boolean> map) {
    listFileRegexp.putAll(map);
  }


  /** Add expressions to folder creations (directory content)
   * @param map Ordered expressions
   */
  public void addCreateFolderRegexp(Map<String,Boolean> map) {
    createFolderRegexp.putAll(map);
  }


  /** Add expressions to file creations (directory content)
   * @param map Ordered expressions
   */
  public void addCreateFileRegexp(Map<String,Boolean> map) {
    createFileRegexp.putAll(map);
  }


  /** Add expressions to folder rename (directory content)
   * @param map Ordered expressions
   */
  public void addRenameFolderRegexp(Map<String,Boolean> map) {
    renameFolderRegexp.putAll(map);
  }


  /** Add expressions to file rename (directory content)
   * @param map Ordered expressions
   */
  public void addRenameFileRegexp(Map<String,Boolean> map) {
    renameFileRegexp.putAll(map);
  }


  /** Add expressions to folder delete (directory content)
   * @param map Ordered expressions
   */
  public void addDeleteFolderRegexp(Map<String,Boolean> map) {
    deleteFolderRegexp.putAll(map);
  }


  /** Add expressions to file delete (directory content)
   * @param map Ordered expressions
   */
  public void addDeleteFileRegexp(Map<String,Boolean> map) {
    deleteFileRegexp.putAll(map);
  }


  /** Add expressions to file append (directory content)
   * @param map Ordered expressions
   */
  public void addAppendFileRegexp(Map<String,Boolean> map) {
    appendFileRegexp.putAll(map);
  }


  /** Add expressions to file overwrite (directory content)
   * @param map Ordered expressions
   */
  public void addOverwriteFileRegexp(Map<String,Boolean> map) {
    overwriteFileRegexp.putAll(map);
  }


  /** Test if directory access is allowed (directory content)
   * @param absDirname Absolute directory name
   * @return Is action allowed or forbidden
   */
  public RegexpActionResult isDirectoryAccessAllowed(String absDirname) {
    return isAllowed(absDirname, accessFolderRegexp);
  }


  /** Test if file access is allowed (directory content)
   * @param absFilename Absolute file name
   * @return Is action allowed or forbidden
   */
  public RegexpActionResult isFileAccessAllowed(String absFilename) {
    return isAllowed(absFilename, accessFileRegexp);
  }


  /** Test if directory listing is allowed (directory content)
   * @param absDirname Absolute directory name
   * @return Is action allowed or forbidden
   */
  public RegexpActionResult isDirectoryListingAllowed(String absDirname) {
    return isAllowed(absDirname, listFolderRegexp);
  }


  /** Test if file listing is allowed (directory content)
   * @param absFilename Absolute file name
   * @return Is action allowed or forbidden
   */
  public RegexpActionResult isFileListingAllowed(String absFilename) {
    return isAllowed(absFilename, listFileRegexp);
  }


  /** Test if directory creation is allowed (directory content)
   * @param absDirname Absolute directory name
   * @return Is action allowed or forbidden
   */
  public RegexpActionResult isDirectoryCreationAllowed(String absDirname) {
    return isAllowed(absDirname, createFolderRegexp);
  }


  /** Test if file creation is allowed (directory content)
   * @param absFilename Absolute file name
   * @return Is action allowed or forbidden
   */
  public RegexpActionResult isFileCreationAllowed(String absFilename) {
    return isAllowed(absFilename, createFileRegexp);
  }


  /** Test if directory rename is allowed (directory content)
   * @param absDirname Absolute directory name
   * @return Is action allowed or forbidden
   */
  public RegexpActionResult isDirectoryRenameAllowed(String absDirname) {
    return isAllowed(absDirname, renameFolderRegexp);
  }


  /** Test if file rename is allowed (directory content)
   * @param absFilename Absolute file name
   * @return Is action allowed or forbidden
   */
  public RegexpActionResult isFileRenameAllowed(String absFilename) {
    return isAllowed(absFilename, renameFileRegexp);
  }


  /** Test if directory delete is allowed (directory content)
   * @param absDirname Absolute directory name
   * @return Is action allowed or forbidden
   */
  public RegexpActionResult isDirectoryDeleteAllowed(String absDirname) {
    return isAllowed(absDirname, deleteFolderRegexp);
  }


  /** Test if file delete is allowed (directory content)
   * @param absFilename Absolute file name
   * @return Is action allowed or forbidden
   */
  public RegexpActionResult isFileDeleteAllowed(String absFilename) {
    return isAllowed(absFilename, deleteFileRegexp);
  }


  /** Test if file append is allowed (directory content)
   * @param absFilename Absolute file name
   * @return Is action allowed or forbidden
   */
  public RegexpActionResult isFileAppendAllowed(String absFilename) {
    return isAllowed(absFilename, appendFileRegexp);
  }


  /** Test if file overwrite is allowed (directory content)
   * @param absFilename Absolute file name
   * @return Is action allowed or forbidden
   */
  public RegexpActionResult isFileOverwriteAllowed(String absFilename) {
    return isAllowed(absFilename, overwriteFileRegexp);
  }


  /** Get directory name
   * @return Absolute directory
   */
  public String getDirectory() {
    return directory;
  }


  /** Set directory name (converts to proper format)
   * @param absDirname Absolute directory name
   */
  public void setDirectory(String absDirname) {
    absDirname = fileAdapter.normalizePath(absDirname);
    directory = absDirname;
  }


  /** Test if properties of this directory are applied to its sub folders and further
   * @return TRUE if applies or FALSE if not
   */
  public boolean isSpread() {
    return spread;
  }


  /** Set if properties of this directory must be applied to its sub folders and further
   * @param spread TRUE to apply or FALSE not to apply
   */
  public void setSpread(boolean spread) {
    this.spread = spread;
  }


  /** Get relative name of directory or file (relative to this directory)
   * @param absName Absolute name
   * @return Relative name (does not starts nor ends with file separator) or null if not a part of this directory
   */
  protected String getRelativeName(String absName) {
    if (!absName.startsWith(directory) && !absName.equals(directory)) return null;
    String relative = absName.substring(directory.length());
    return stripFileSeparators(relative);
  }


  /** Strip file separetors from the begining and the end of a string
   * @param str String to process
   * @return Stripped string
   */
  protected String stripFileSeparators(String str) {
    while (str.startsWith(fileAdapter.getSeparator())) str = str.substring(1);
    while (str.endsWith(fileAdapter.getSeparator())) str = str.substring(0, str.length()-1);
    return str;
  }


  /** Get whatever there is after the last file separator (if there are any)
   * @param str String to process
   * @return Last file or folder name
   */
  protected String getLastName(String str) {
    str = stripFileSeparators(str);
    if (str.indexOf(fileAdapter.getSeparator()) == -1) return str;
    int ind = str.lastIndexOf(fileAdapter.getSeparator());
    return str.substring(ind+1);
  }


  /** Test if folder/file name is allowed (in directory content).
   * This method checks a file name ot the name of a last folder.
   * @param absName Directory or file name (absolute form, real path on a hard drive, proper format)
   * @param regexps Map with regular expressions and YES/NO values to check against
   * @return "ALLOW" result if allowed (or cannot be determined), "FORBID" result otherwise
   */
  public RegexpActionResult isAllowed(String absName, Map<String,Boolean> regexps) {
    //Relative name to this directory
    String relName = getRelativeName(absName);

    //If outside of this directory or this directory itself
    if (relName == null) return RegexpActionResult.ALLOW_NO_MATCH;
    if (relName.length() == 0) return RegexpActionResult.ALLOW_NO_MATCH;

    //Folder or file name to check
    String name = relName;

    //If folder or file is deeper inside then return TRUE if spread is false, otherwise check
    if (relName.indexOf(fileAdapter.getSeparator()) != -1) {
      if (spread) name = getLastName(relName);
      else return RegexpActionResult.ALLOW_NO_MATCH;
    }

    name = name.toLowerCase();

    //Check the name (till the first match of regexp with folder or file name)
    for (String regexp : regexps.keySet()) {
      boolean allow = regexps.get(regexp);
      if (Syntax.check(name, regexp)) {
        log.debug("Regexp ["+regexp+"] matches file/folder ["+name+"], allows ["+allow+"]");
        return allow ? RegexpActionResult.ALLOW_MATCH : RegexpActionResult.FORBID_MATCH;
      }
    }

    //No match
    return RegexpActionResult.ALLOW_NO_MATCH;
  }
}