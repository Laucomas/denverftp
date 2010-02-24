package com.coldcore.coloradoftp.filesystem;

import com.coldcore.coloradoftp.session.Session;

import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.Set;

/**
 * Virtual filesystem.
 *
 * This filesystem can be mounted to a hard drive filesystem, to a database or
 * to any other persistence file-like storage implementation.
 *
 * This class usually exists in single instance. Every user-ralated method takes
 * a user session as an argument, so the filesystem knows which user requested a
 * file operation.
 *
 * Some not critical methods may be left unimplemented (throwing an exception). And it is
 * not a mandatory that all other methods must be implemented for every parameter user
 * may supply.
 *
 * This class communicates back to command processor (to submit reply in case of invalid
 * input for example) by using FailedActionException exceptions. Command processor
 * treats FailedActionException separatly from all other exceptions producing replies
 * that correspond to filesystem errors.
 *
 *
 * ColoradoFTP - The Open Source FTP Server (http://cftp.coldcore.com)
 */
public interface FileSystem {

  /** Get user current directory.
   * @param userSession User session
   * @return Current directory (absolute form)
   */
  public String getCurrentDirectory(Session userSession) throws FailedActionException;


  /** Convert path to an absolute form.
   * This method must convert user input to an absolute form and return it even if
   * the path does not exist or user does not have permissions to it.
   * @param path Get parent path of this path (user input - absolute or relative)
   * @param userSession User session
   * @return Path (absolute form)
   */
  public String toAbsolute(String path, Session userSession) throws FailedActionException;


  /** Get parent path.
   * This method must convert user input to an absolute form and return the parent path even if
   * the path does not exist or user does not have permissions to it. If a path already is a root
   * folder then the root folder must be returned.
   * @param path Get parent path of this path (user input - absolute or relative)
   * @param userSession User session
   * @return Parent path (absolute form)
   */
  public String getParent(String path, Session userSession) throws FailedActionException;


  /** Get the list of all files and folders in directory.
   * @param dir Directory (user input - absolute or relative)
   * @param userSession User session
   * @return Directory listing
   */
  public Set<ListingFile> listDirectory(String dir, Session userSession) throws FailedActionException;


  /** Get path object.
   * @param path Path (user input - absolute or relative)
   * @param userSession User session
   * @return Path object
   */
  public ListingFile getPath(String path, Session userSession) throws FailedActionException;


  /** Change current user directory.
   * @param dir New directory (user input - absolute or relative)
   * @param userSession User session
   * @return Absolute name of a new directory
   */
  public String changeDirectory(String dir, Session userSession) throws FailedActionException;


  /** Delete path.
   * @param path Path to remove (user input - absolute or relative)
   * @param userSession User session
   */
  public void deletePath(String path, Session userSession) throws FailedActionException;


  /** Create a new directory.
   * @param dir Directory to create (user input - absolute or relative)
   * @param userSession
   * @throws FailedActionException
   * @return Absolute name of a new directory
   */
  public String createDirectory(String dir, Session userSession) throws FailedActionException;


  /** Rename path.
   * @param from From name (user input - absolute or relative)
   * @param to To name (user input - absolute or relative)
   * @return Absolute name of a new path
   */
  public String renamePath(String from, String to, Session userSession) throws FailedActionException;


  /** Read file data.
   * @param filename Filename (user input - absolute or relative)
   * @param position Start reading from this position in the file
   * @param userSession User session
   * @return Channel mounted to file the system can read data from (will be closed by the system)
   */
  public ReadableByteChannel readFile(String filename, long position, Session userSession) throws FailedActionException;


  /** Write file data.
   * @param filename Filename (user input - absolute or relative)
   * @param append TRUE if file must be appended, FALSE if overwritten
   * @param userSession User session
   * @return Channel mounted to file the system can write data into (will be closed by the system)
   */
  public WritableByteChannel saveFile(String filename, boolean append, Session userSession) throws FailedActionException;


  /** Return file separator.
   * @return File separator (may have more than 1 character)
   */
  public String getFileSeparator();
}
