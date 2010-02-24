package com.coldcore.coloradoftp.plugin.gateway.dao.xml;

import com.coldcore.coloradoftp.plugin.gateway.User;
import com.coldcore.coloradoftp.plugin.gateway.dao.BaseUserDAO;
import com.coldcore.misc5.Xml;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * User data access object (based on plain XML).
 */
public class XmlUserDAO extends BaseUserDAO {

  private static Logger log = Logger.getLogger(XmlUserDAO.class);
  protected Document xml;


  /** Constructor
   * @param filename Path to XML file
   */
  public XmlUserDAO(String filename) throws Exception {
    File file = new File(filename);
    if (file.exists() && file.isFile()) {
      Document xml = Xml.loadXml(file);
      initialize(xml);
      return;
    }

    ResourceLoader loader = new DefaultResourceLoader(getClass().getClassLoader());
    Resource r = loader.getResource(filename);
    if (r != null) {
      Document xml = Xml.loadXml(r.getInputStream());
      initialize(xml);
      return;
    }

    throw new FileNotFoundException("Configuration file cannot be found: "+filename);
  }


  /** Initalization
   * @param xml XML document
   */
  public void initialize(Document xml) {
    this.xml = xml;
    if (xml == null) throw new IllegalArgumentException("Document must not be null");

  }


  public User getUser(String username) throws Exception {
    //Is Xerces DOM implementation thread-safe? No!
    synchronized (xml) {
      Element[] userEs = Xml.findElements("users/user", xml);
      for (Element userE : userEs) {
        String name = userE.getAttribute("name");
        String pass = userE.getAttribute("pass");
        String role = userE.getAttribute("role");

        if (name.equalsIgnoreCase(username)) {
          User user = constructUser(name, pass, role);
          log.debug("User '"+name+"' found with role(s) '"+role+"'");
          return user;
        }
      }
    }

    log.debug("User '"+username+"' not found");
    return null;
  }
}
