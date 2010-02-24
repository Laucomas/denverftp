package com.coldcore.coloradoftp.plugin.gateway.dao.spring;

import com.coldcore.coloradoftp.plugin.gateway.User;
import com.coldcore.coloradoftp.plugin.gateway.dao.BaseUserDAO;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Set;

/**
 * @deprecated Due to the poor design.
 * User data access object (based on Spring).
 */
public class SpringUserDAO extends BaseUserDAO {

  private static Logger log = Logger.getLogger(SpringUserDAO.class);
  protected BeanFactory beanFactory;


  /** Constructor
   * @param filename Path to XML file
   */
  public SpringUserDAO(String filename) throws Exception {
    File file = new File(filename);
    if (file.exists() && file.isFile()) {
      Resource resource = new FileSystemResource(file);
      initialize(resource);
      return;
    }

    ResourceLoader loader = new DefaultResourceLoader(getClass().getClassLoader());
    Resource r = loader.getResource(filename);
    if (r != null) {
      Resource resource = new InputStreamResource(r.getInputStream());
      initialize(resource);
      return;
    }
      
    throw new FileNotFoundException("Configuration file cannot be found: "+filename);
  }


  /** Initalization
   * @param resource XML resorce
   */
  public void initialize(Resource resource) {
    if (resource == null) throw new IllegalArgumentException("Resource must not be null");
    beanFactory = new XmlBeanFactory(resource);
  }


  public User getUser(String username) throws Exception {
    if (username == null) username = "";

    Set<User> set = (Set<User>) beanFactory.getBean("users");
    for (User user : set)
      if (username.equalsIgnoreCase(user.getUsername())) {
        log.debug("User '"+username+"' found");
        return user;
      }

    log.debug("User '"+username+"' not found");
    return null;
  }
}
