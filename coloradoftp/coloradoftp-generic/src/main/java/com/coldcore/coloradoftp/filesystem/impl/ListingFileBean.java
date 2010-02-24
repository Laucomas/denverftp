/**
 * @see com.coldcore.coloradoftp.filesystem.ListingFile
 */
package com.coldcore.coloradoftp.filesystem.impl;

import com.coldcore.coloradoftp.filesystem.ListingFile;

import java.util.Date;

public class ListingFileBean implements ListingFile {

  protected String owner;
  protected boolean directory;
  protected String permissions;
  protected String mlsxFacts;
  protected long size;
  protected String name;
  protected String absolutePath;
  protected Date lastModified;


  public String getOwner() {
    return owner;
  }


  public void setOwner(String owner) {
    this.owner = owner;
  }


  public boolean isDirectory() {
    return directory;
  }


  public void setDirectory(boolean directory) {
    this.directory = directory;
  }


  public String getPermissions() {
    return permissions;
  }


  public void setPermissions(String permissions) {
    this.permissions = permissions;
  }


  public String getMlsxFacts() {
    return mlsxFacts;
  }


  public void setMlsxFacts(String mlsxFacts) {
    this.mlsxFacts = mlsxFacts;
  }


  public long getSize() {
    return size;
  }


  public void setSize(long size) {
    this.size = size;
  }


  public String getName() {
    return name;
  }


  public void setName(String name) {
    this.name = name;
  }


  public Date getLastModified() {
    return lastModified;
  }


  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }


  public String getAbsolutePath() {
    return absolutePath;
  }


  public void setAbsolutePath(String absolutePath) {
    this.absolutePath = absolutePath;
  }
}
