package com.coldcore.coloradoftp.plugin.xmlfs.adapter;

/**
 * File Adapter containing several file related operations.
 * This is required to abstract from a hard drive. All the input and output
 * parameters relate to an actual underlying file system (eg drive).
 */
public interface FileAdapter {

  /** Similar to:
   *    File.separator = /
   *    File.separator = \
   * @return File separator as used by an underlying real file system (eg drive)
   */
  public String getSeparator();


  /** Similar to:
   *    File("/foo/bar").getAbsolutePath() = /foo/bar
   *    File("/foo/bar/").getAbsolutePath() = /foo/bar
   * @param path Absolute path, may not have proper file separators or may have extra spaces as this value
   *             may be read from a configuration file and supplied as is.
   * @return Absolute path with proper file separators (matching getSeparator), without a 
   *         file separator at the end etc
   */
  public String normalizePath(String path);


  /** Similar to:
   *    File("/foo/bar").getParentFile().getAbsolutePath() = /foo
   *    File("/foo").getParentFile().getAbsolutePath() = /
   * @param path Absolute path, proper format, with proper file separators (output of normalizePath)
   * @return Absolute path of a parent folder or a root if no parent found
   */
  public String getParentPath(String path);

}