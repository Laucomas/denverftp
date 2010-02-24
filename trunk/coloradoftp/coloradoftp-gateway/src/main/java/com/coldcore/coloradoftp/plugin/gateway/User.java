package com.coldcore.coloradoftp.plugin.gateway;

import java.util.Set;

/**
 * User.
 */
public interface User {

  /** Get user name
   * @return User name
   */
  public String getUsername();


  /** Set user name
   * @param username User name
   */
  public void setUsername(String username);


  /** Get password
   * @return Password
   */
  public String getPassword();


  /** Set password
   * @param password Password
   */
  public void setPassword(String password);


  /** Get role
   * @return Role
   */
  public Set<Role> getRoles();


  /** Set role
   * @param role Role
   */
  public void setRoles(Set<Role> role);
}
