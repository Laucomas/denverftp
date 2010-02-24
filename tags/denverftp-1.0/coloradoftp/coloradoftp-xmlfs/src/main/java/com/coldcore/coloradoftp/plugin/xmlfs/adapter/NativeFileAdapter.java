package com.coldcore.coloradoftp.plugin.xmlfs.adapter;

import java.io.File;

/**
 * Adapter reusing java.io.File
 */
public class NativeFileAdapter implements FileAdapter {

  public String getSeparator() {
    return File.separator;
  }


  public String normalizePath(String path) {
    return new File(path).getAbsolutePath();
  }


  public String getParentPath(String path) {
    return new File(path).getParentFile().getAbsolutePath();
  }
}
