package com.coldcore.coloradoftp.plugin.gateway.dao.acegi;

import com.coldcore.coloradoftp.plugin.gateway.User;
import com.coldcore.coloradoftp.plugin.gateway.dao.BaseUserDAO;
import org.acegisecurity.Authentication;
import org.acegisecurity.AuthenticationManager;
import org.acegisecurity.GrantedAuthority;
import org.acegisecurity.AuthenticationException;
import org.acegisecurity.providers.UsernamePasswordAuthenticationToken;
import org.apache.log4j.Logger;

import java.util.Set;

/**
 * User data access object (based on Acegi).
 */
public class AcegiUserDAO extends BaseUserDAO {

  private static Logger log = Logger.getLogger(AcegiUserDAO.class);
  protected AuthenticationManager authenticationManager;
  protected Set<String> allowedUserRoles;


  public AuthenticationManager getAuthenticationManager() {
    return authenticationManager;
  }


  public void setAuthenticationManager(AuthenticationManager authenticationManager) {
    this.authenticationManager = authenticationManager;
  }


  public Set<String> getAllowedUserRoles() {
    return allowedUserRoles;
  }


  public void setAllowedUserRoles(Set<String> allowedUserRoles) {
    this.allowedUserRoles = allowedUserRoles;
  }


  public User getUser(String username) throws Exception {
    return null;
  }


  public User getUser(String username, String password) throws Exception {
    if (authenticationManager == null)
      throw new IllegalStateException("Authentication Manager is not set");

    if (username == null) username = "";

    Authentication auth = new UsernamePasswordAuthenticationToken(username, password);
    try {
      auth = authenticationManager.authenticate(auth);
    } catch (AuthenticationException e) {}

    if (!auth.isAuthenticated()) {
      log.debug("User '"+username+"' not authenticated");
      return null;
    } else {
      log.debug("User '"+username+"' authenticated");
    }

    String role = "";
    GrantedAuthority[] authorities = auth.getAuthorities();
    if (authorities != null)
      for (GrantedAuthority ga : authorities) {
        if (role.length() > 0) role += ", ";
        role += ga.getAuthority();
      }

    User user = constructUser(username, password, role);

    if (allowedUserRoles == null || allowedUserRoles.isEmpty()) {
      log.debug("No allowed roles defined, user '"+username+"' is allowed");
      return user;
    }

    if (authorities != null)
      for (GrantedAuthority ga : authorities) {
        if (allowedUserRoles.contains(ga.getAuthority())) {
          log.debug("User '"+username+"' matched allowed role '"+ga.getAuthority()+"'");
          return user;
        }
      }

    log.debug("User '"+username+"' is not allowed");
    return null;
  }
}
