package com.coldcore.coloradoftp.plugin.xmlfs.resolver;

import com.coldcore.coloradoftp.plugin.xmlfs.UserHome;

/**
 * This class maps a virtual path to a real path on a real file system (eg drive).
 */
public interface RealPathResolver {

  /** Convert folder/file virtual path name (user input as is) to a path on a real file system (eg drive).
   * When user submits a directory or file name via FTP client this method must convert
   * it to a path on a hard drive that corresponds to the virtual path.
   * @param path Absolute path name to convert (as FTP client sumbits, in absolute form)
   * @param home User home
   * @return Absolute path on a hard drive (in proper format and without file separator in the end)
   *         or NULL if cannot be converted
   */
  public String virtualPathToReal(String path, UserHome home);

}