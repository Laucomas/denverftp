package com.coldcore.coloradoftp.plugin.cottafilesystem.xmlfs;

import com.coldcore.coloradoftp.plugin.xmlfs.adapter.FileAdapter;
import net.sf.cotta.*;

/**
 * Adapter reusing COTTA.
 */
public class CottaFileAdapter implements FileAdapter {

    public String getSeparator() {
        return "/";
    }


    public String normalizePath(String path) {
      TPath tpath = TPath.parse(path);
      if (tpath.isRelative()) throw new IllegalArgumentException("Path must not be relative");
      return tpath.toPathString();
    }


    public String getParentPath(String path) {
      TPath tpath = TPath.parse(path);
      if (tpath.isRelative()) throw new IllegalArgumentException("Path must not be relative");
      TPath tparent = tpath.parent(); //Null if already a root
      return tparent == null ? path : tparent.toPathString();
    }
}
