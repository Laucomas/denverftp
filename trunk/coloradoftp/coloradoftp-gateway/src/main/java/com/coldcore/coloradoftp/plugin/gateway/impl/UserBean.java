package com.coldcore.coloradoftp.plugin.gateway.impl;

import com.coldcore.coloradoftp.plugin.gateway.Role;
import com.coldcore.coloradoftp.plugin.gateway.User;

import java.util.Set;

/**
 * @see com.coldcore.coloradoftp.plugin.gateway.User
 */
public class UserBean implements User {

  protected String username;
  protected String password;
  protected Set<Role> roles;


  public String getUsername() {
    return username;
  }


  public void setUsername(String username) {
    this.username = username;
  }


  public String getPassword() {
    return password;
  }


  public void setPassword(String password) {
    this.password = password;
  }


  public Set<Role> getRoles() {
    return roles;
  }


  public void setRoles(Set<Role> role) {
    this.roles = role;
  }
}
