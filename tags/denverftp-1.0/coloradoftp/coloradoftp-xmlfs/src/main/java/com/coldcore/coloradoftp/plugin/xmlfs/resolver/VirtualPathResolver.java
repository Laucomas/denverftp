package com.coldcore.coloradoftp.plugin.xmlfs.resolver;

import com.coldcore.coloradoftp.plugin.xmlfs.UserHome;
import com.coldcore.coloradoftp.session.Session;

/**
 * Converts virtual path to an absolute virtual path and performs some operations on it.
 */
public interface VirtualPathResolver {

  /** Convert virtual path name (user input as is) to absolute form.
   * When user submits a directory or file name via FTP client this method must ensure that
   * it is in an absolute form.
   * @param path Path to convert (as FTP client sumbits)
   * @param userSession User session
   * @return Absolute file/folder name (which does not end with file separator) or NULL if cannot be converted
   */
  public String virtualPathToAbsolute(String path, Session userSession);


  /** Test if virtual path (user input as is) is a virtual folder mounted to users home.
   * Note that this method may not work with paths ending with file separator.
   * @param path Absolute path (as FTP client sumbits, in absolute form)
   * @param home User home
   * @return TRUE if path points to a virtual folder (and not further), FALSE otherwise
   */
  public boolean isVirtualFolder(String path, UserHome home);


  /** Get virtual parent path.
   * Note that this method may not work with paths ending with file separator.
   * If a path already is a root folder then the root folder will be returned.
   * @param path Absolute path (as FTP client sumbits, in absolute form)
   * @return Parent path (absolute form)
   */
  public String getVirtualParent(String path);


  /** Get user current virtual directory.
   * @param userSession User session
   * @return Current directory (absolute form)
   */
  public String getCurrentVirtualDirectory(Session userSession);


  /** Return virtual file separator.
   * @return File separator (may have more than 1 character, however, this is not recommended)
   */
  public String getVirtualFileSeparator();
}