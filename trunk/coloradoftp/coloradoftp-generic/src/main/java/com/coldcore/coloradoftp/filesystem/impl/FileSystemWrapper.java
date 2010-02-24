package com.coldcore.coloradoftp.filesystem.impl;

import com.coldcore.coloradoftp.filesystem.FileSystem;
import com.coldcore.coloradoftp.filesystem.FailedActionException;
import com.coldcore.coloradoftp.filesystem.ListingFile;
import com.coldcore.coloradoftp.session.Session;

import java.util.Set;
import java.nio.channels.WritableByteChannel;
import java.nio.channels.ReadableByteChannel;

/**
 * File system wrapper.
 * This class is for extensions wishing to operate on existing filesystem
 * and stay independent of future interface changes.
 */
public class FileSystemWrapper implements FileSystem {

  protected FileSystem fileSystem;


  public FileSystemWrapper(FileSystem fileSystem) {
    super();
    this.fileSystem = fileSystem;
    if (fileSystem == null) throw new IllegalArgumentException("Invalid file system");
  }


  public String getCurrentDirectory(Session userSession) throws FailedActionException {
    return fileSystem.getCurrentDirectory(userSession);
  }


  public String toAbsolute(String path, Session userSession) throws FailedActionException {
    return fileSystem.toAbsolute(path, userSession);
  }


  public String getParent(String path, Session userSession) throws FailedActionException {
    return fileSystem.getParent(path, userSession);
  }


  public Set<ListingFile> listDirectory(String dir, Session userSession) throws FailedActionException {
    return fileSystem.listDirectory(dir, userSession);
  }


  public ListingFile getPath(String path, Session userSession) throws FailedActionException {
    return fileSystem.getPath(path, userSession);
  }


  public String changeDirectory(String dir, Session userSession) throws FailedActionException {
    return fileSystem.changeDirectory(dir, userSession);
  }


  public void deletePath(String path, Session userSession) throws FailedActionException {
    fileSystem.deletePath(path, userSession);
  }


  public String createDirectory(String dir, Session userSession) throws FailedActionException {
    return fileSystem.createDirectory(dir, userSession);
  }


  public String renamePath(String from, String to, Session userSession) throws FailedActionException {
    return fileSystem.renamePath(from, to, userSession);
  }


  public ReadableByteChannel readFile(String filename, long position, Session userSession) throws FailedActionException {
    return fileSystem.readFile(filename, position, userSession);
  }


  public WritableByteChannel saveFile(String filename, boolean append, Session userSession) throws FailedActionException {
    return fileSystem.saveFile(filename, append, userSession);
  }


  public String getFileSeparator() {
    return fileSystem.getFileSeparator();
  }
}
