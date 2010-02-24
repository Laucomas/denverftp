package com.coldcore.coloradoftp.plugin.gateway.dao.acegi;

import org.acegisecurity.AuthenticationManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Set;

/**
 * User data access object (based on Acegi).
 */
public class ExternalAcegiUserDAO extends AcegiUserDAO {

  private static Logger log = Logger.getLogger(ExternalAcegiUserDAO.class);

  public static final String ALLOWED_USER_ROLES = "allowedUserRoles";
  public static final String AUTHENTICATION_MANAGER = "authenticationManager";


  /** Constructor
   * @param filename Path to XML file
   */
  public ExternalAcegiUserDAO(String filename) throws Exception {
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
    BeanFactory beanFactory = new XmlBeanFactory(resource);

    Set<String> roles = (Set<String>) beanFactory.getBean(ALLOWED_USER_ROLES);
    setAllowedUserRoles(roles);

    AuthenticationManager manager = (AuthenticationManager) beanFactory.getBean(AUTHENTICATION_MANAGER);
    setAuthenticationManager(manager);
  }
}
