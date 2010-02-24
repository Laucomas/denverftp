package com.coldcore.coloradoftp.filesystem;

import java.util.Date;

/**
 * Listing file.
 *
 * This class is used by a file system to produce directory listings
 * (in response to LIST and NLST commands for example).
 *
 *
 * ColoradoFTP - The Open Source FTP Server (http://cftp.coldcore.com)
 */
public interface ListingFile {

  /** Get owner name
   * @return Name of the file owner
   */
  public String getOwner();


  /** Set owner name
   * @param owner Name of the file owner
   */
  public void setOwner(String owner);


  /** Test if this file is a directory
   * @return TRIE if it is a directory, FALSE otherwise
   */
  public boolean isDirectory();


  /** Set wether this is directory
   * @param b TRUE for directories, FALSE for files
   */
  public void setDirectory(boolean b);


  /** Get 9-letter permissions string
   * @return Permissions string
   */
  public String getPermissions();


  /** Set 9-letter permissions string
   * @param permissions Permissions string
   */
  public void setPermissions(String permissions);


  /** Get facts (MLSx command)
   * @return Facts string
   */
  public String getMlsxFacts();


  /** Set facts (MLSx command)
   * @param facts Permissions string
   */
  public void setMlsxFacts(String facts);


  /** Get file size
   * @return Size of the file in bytes
   */
  public long getSize();


  /** Set file size
   * @param size Size of the file in bytes
   */
  public void setSize(long size);


  /** Get name of the file
   * @return Non-absolute file name
   */
  public String getName();


  /** Set name of the file
   * @param name Non-absolute file name
   */
  public void setName(String name);


  /** Get abfolute path of the file
   * @return Absolute path name
   */
  public String getAbsolutePath();


  /** Set abfolute path of the file
   * @param path Absolute path name
   */
  public void setAbsolutePath(String path);


  /** Get last modified date
   * @return Last modified date
   */
  public Date getLastModified();


  /** Set last modified date
   * @param date Last modified date
   */
  public void setLastModified(Date date);
}
