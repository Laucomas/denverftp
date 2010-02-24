package com.coldcore.coloradoftp.plugin.xmlfs.permissionsmanager;

import com.coldcore.coloradoftp.plugin.xmlfs.UserHome;
import com.coldcore.coloradoftp.plugin.xmlfs.adapter.FileAdapter;

/**
 * This manager deals with directories permissions (directory properties).
 */
public interface PermissionsManager {

  public void setFileAdapter(FileAdapter fileAdapter);

  /** Test if directory access is allowed.
   * Note that this method does not work with paths ending with file separator.
   * @param dirname Directory name (absolute form, real path, proper format)
   * @param home User home
   * @return TRUE if access is allowed, FALSE otherwise
   */
  public boolean canAccessDirectory(String dirname, UserHome home);


  /** Test if file access is allowed.
   * Note that this method does not work with paths ending with file separator.
   * @param filename File name (absolute form, real path, proper format)
   * @param home User home
   * @return TRUE if access is allowed, FALSE otherwise
   */
  public boolean canAccessFile(String filename, UserHome home);


  /** Test if directory listing is allowed.
   * Note that this method does not work with paths ending with file separator.
   * @param dirname Directory name (absolute form, real path, proper format)
   * @param home User home
   * @return TRUE if access is allowed, FALSE otherwise
   */
  public boolean canListDirectory(String dirname, UserHome home);


  /** Test if file listing is allowed.
   * Note that this method does not work with paths ending with file separator.
   * @param filename File name (absolute form, real path, proper format)
   * @param home User home
   * @return TRUE if access is allowed, FALSE otherwise
   */
  public boolean canListFile(String filename, UserHome home);


  /** Test if directory delete is allowed.
   * Note that this method may not work with paths ending with file separator.
   * @param dirname Directory name (absolute form, real path, proper format)
   * @param home User home
   * @return TRUE if access is allowed, FALSE otherwise
   */
  public boolean canDeleteDirectory(String dirname, UserHome home);


  /** Test if file delete is allowed.
   * Note that this method may not work with paths ending with file separator.
   * @param filename File name (absolute form, real path, proper format)
   * @param home User home
   * @return TRUE if access is allowed, FALSE otherwise
   */
  public boolean canDeleteFile(String filename, UserHome home);


  /** Test if directory rename is allowed.
   * Note that this method may not work with paths ending with file separator.
   * @param dirname Directory name (absolute form, real path, proper format)
   * @param home User home
   * @return TRUE if access is allowed, FALSE otherwise
   */
  public boolean canRenameDirectory(String dirname, UserHome home);


  /** Test if file rename is allowed.
   * Note that this method may not work with paths ending with file separator.
   * @param filename File name (absolute form, real path, proper format)
   * @param home User home
   * @return TRUE if access is allowed, FALSE otherwise
   */
  public boolean canRenameFile(String filename, UserHome home);


  /** Test if directory create is allowed.
   * Note that this method may not work with paths ending with file separator.
   * @param dirname Directory name (absolute form, real path, proper format)
   * @param home User home
   * @return TRUE if access is allowed, FALSE otherwise
   */
  public boolean canCreateDirectory(String dirname, UserHome home);


  /** Test if file create is allowed.
   * Note that this method may not work with paths ending with file separator.
   * @param filename File name (absolute form, real path, proper format)
   * @param home User home
   * @return TRUE if access is allowed, FALSE otherwise
   */
  public boolean canCreateFile(String filename, UserHome home);


  /** Test if file append is allowed.
   * Note that this method may not work with paths ending with file separator.
   * @param filename File name (absolute form, real path, proper format)
   * @param home User home
   * @return TRUE if access is allowed, FALSE otherwise
   */
  public boolean canAppendFile(String filename, UserHome home);


  /** Test if file overwrite is allowed.
   * Note that this method may not work with paths ending with file separator.
   * @param filename File name (absolute form, real path, proper format)
   * @param home User home
   * @return TRUE if access is allowed, FALSE otherwise
   */
  public boolean canOverwriteFile(String filename, UserHome home);
}