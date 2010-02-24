package com.coldcore.coloradoftp.plugin.xmlfs;

import com.coldcore.coloradoftp.plugin.xmlfs.adapter.FileAdapter;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * User home.
 *
 * It is a list of directories and virtual folders user's filesystem contains.
 */
public class UserHome {

  protected Set<DirProperty> properties;
  protected Set<VirtualFolder> folders;
  protected String path;
  protected FileAdapter fileAdapter;


  public UserHome(FileAdapter fileAdapter) {
    if (fileAdapter == null) throw new IllegalArgumentException("Invalid file adapter");
    this.fileAdapter = fileAdapter;
    properties = new LinkedHashSet<DirProperty>();
    folders = new HashSet<VirtualFolder>();
  }


  /** Add directory properties
   * @param set Directory properties
   */
  public void addProperties(Set<DirProperty> set) {
    properties.addAll(set);
  }


  /** Get directory properties
   * @return Directory properties (read only!)
   */
  public Set<DirProperty> getProperties() {
    return properties;
  }


  /** Add virtual folders
   * @param set Virtual folders
   */
  public void addFolders(Set<VirtualFolder> set) {
    folders.addAll(set);
  }


  /** Get path to user home folder
   * @return Absolute path
   */
  public String getPath() {
    return path;
  }


  /** Set path to user home folder (converts to proper format)
   * @param absPath Absolute path
   */
  public void setPath(String absPath) {
    absPath = fileAdapter.normalizePath(absPath);
    path = absPath;
  }


  /** Get virtual folder by name
   * @param name Virtual folder name
   * @return Virtual folder or NULL
   */
  public VirtualFolder getVirtualFolder(String name) {
    for (VirtualFolder folder : folders)
      if (folder.getName().equals(name)) return folder;
    return null;
  }


  /** Get virtual folders
   * @return Virtual folders (read only!)
   */
  public Set<VirtualFolder> getVirtualFolders() {
    return folders;
  }
}