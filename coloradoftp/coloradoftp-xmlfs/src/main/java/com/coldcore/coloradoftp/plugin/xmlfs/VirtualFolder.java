package com.coldcore.coloradoftp.plugin.xmlfs;

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Virtual folder.
 *
 * Virtual folders mount directories from anywhere on hard drive to user's
 * home directory. User sees the name of the virtual folder in his/her FTP list
 * which is the entry point to the mounted directory itself.
 */
public class VirtualFolder {

  protected String name;
  protected String path;
  protected Set<DirProperty> properties;


  public VirtualFolder() {
    properties = new LinkedHashSet<DirProperty>();
  }


  /** Get the name
   * @return Virtual folder name
   */
  public String getName() {
    return name;
  }


  /** Set the name
   * @param name Virtual folder name
   */
  public void setName(String name) {
    this.name = name;
  }


  /** Get mounted path
   * @return Absolute path name
   */
  public String getPath() {
    return path;
  }


  /** Set mounted path
   * @param absPath Absolute path name
   */
  public void setPath(String absPath) {
    path = absPath;
  }


  /** Add properties that apply to this virtual folder and its content
   * @param set Directory properties
   */
  public void addProperties(Set<DirProperty> set) {
    properties.addAll(set);
  }


  /** Get properties that apply to this virtual folder and its content
   * @return Directory properties (read only!)
   */
  public Set<DirProperty> getProperties() {
    return properties;
  }
}