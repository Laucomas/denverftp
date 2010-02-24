package com.coldcore.coloradoftp.plugin.xmlfs.resolver;

import com.coldcore.coloradoftp.plugin.xmlfs.UserHome;
import com.coldcore.coloradoftp.session.Session;
import com.coldcore.coloradoftp.session.SessionAttributeName;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Generic resolver.
 * This class uses unix style: "/" is a root directory and "/" is a file separator.
 */
public class GenericVirtualPathResolver implements VirtualPathResolver {

  private static Logger log = Logger.getLogger(GenericVirtualPathResolver.class);


  public String virtualPathToAbsolute(String path, Session userSession) {
    if (path == null || path.length() == 0) {
      log.debug("Virtual path ["+path+"] cannot be converted to absolute");
      return null;
    }

    String apath = path;
    if (apath.startsWith("/")) {
      while (apath.endsWith("/")) apath = apath.substring(0, apath.length()-1);
      if (apath.length() == 0) apath = "/";
      apath = fixVirtualParentRefs(apath);
      log.debug("Virtual path ["+path+"] converted to absolute ["+apath+"]");
      return apath;
    }

    while (apath.endsWith("/")) apath = apath.substring(0, apath.length()-1);

    String curDir = (String) userSession.getAttribute(SessionAttributeName.CURRENT_DIRECTORY);
    if (curDir == null || curDir.equals("/")) {
      apath = "/"+apath;
      apath = fixVirtualParentRefs(apath);
      log.debug("Virtual path ["+path+"] converted to absolute ["+apath+"]");
      return apath;
    }

    apath = curDir+"/"+apath;
    apath = fixVirtualParentRefs(apath);
    log.debug("Virtual path ["+path+"] converted to absolute ["+apath+"]");
    return apath;
  }


  /** Normalizes path by removing references to parent dirs from a virtual path and removing multiple file separators.
   * WARNING! The path may contain ".." (references to the parent dir). This is a serious security issue because
   * user may go outside of his root dir. This method correctly converts the path to the same path but without "..".
   * @param path Absolute virtual file/folder name
   * @return Absolute virtual file/folder name (does not end with file separator) or NULL if cannot be converted
   */
  protected String fixVirtualParentRefs(String path) {
    if (path == null || !path.startsWith("/")) return path;

    //Add all items (names separated by "/") to the list
    List<String> items = new ArrayList<String>();
    StringTokenizer st = new StringTokenizer(path, "/");
    while (st.hasMoreTokens())
      items.add(st.nextToken());

    //Remove all ".." items and items (folders) that stand in front of them
    for (int z = 0; z < items.size(); z++) {
      String item = items.get(z);
      if (item.trim().equals("..")) {
        //Remove this ".." and also remove the previous folder (if it exists)
        items.remove(z);
        if (z > 0) items.remove(--z);
        z--;
      }
    }

    //Assemble the path again from the items (now there are no "..")
    path = "";
    for (String item : items)
      path += "/"+item;
    if (path.length() == 0) path = "/";

    return path;
  }


  public boolean isVirtualFolder(String path, UserHome home) {
    if (path == null || path.length() == 0 || !path.startsWith("/") || path.endsWith("/")) return false;
    String name = path.substring(1);
    return home.getVirtualFolder(name) != null;
  }

  
  public String getVirtualParent(String path) {
    if (path == null || path.length() == 0 || !path.startsWith("/") || path.endsWith("/")) return path;
    String apath = path.substring(0, path.lastIndexOf("/"));
    if (apath.length() == 0) apath = "/";
    log.debug("Parent of ["+path+"] is ["+apath+"]");
    return apath;
  }


  public String getCurrentVirtualDirectory(Session userSession) {
    String curDir = (String) userSession.getAttribute(SessionAttributeName.CURRENT_DIRECTORY);
    if (curDir == null) {
      curDir = "/";
      userSession.setAttribute(SessionAttributeName.CURRENT_DIRECTORY, curDir);
    }
    log.debug("User current directory: "+curDir);
    return curDir;
  }


  public String getVirtualFileSeparator() {
    return "/";
  }
}