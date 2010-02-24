package com.coldcore.coloradoftp.plugin.gateway.impl;

import com.coldcore.coloradoftp.plugin.gateway.Role;

/**
 * @see com.coldcore.coloradoftp.plugin.gateway.Role
 */
public class RoleBean implements Role {

  protected String name;


  public String getName() {
    return name;
  }


  public void setName(String name) {
    this.name = name;
  }
}
