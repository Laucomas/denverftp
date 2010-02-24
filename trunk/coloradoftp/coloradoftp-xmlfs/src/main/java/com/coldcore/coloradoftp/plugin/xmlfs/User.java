package com.coldcore.coloradoftp.plugin.xmlfs;

/**
 * User.
 */
public class User {

  protected String username;
  protected UserHome home;
  protected boolean def;


  /** Get user name
   * @return User name
   */
  public String getUsername() {
    return username;
  }


  /** Set user name
   * @param username User name
   */
  public void setUsername(String username) {
    this.username = username;
  }


  /** Get user home
   * @return User home
   */
  public UserHome getHome() {
    return home;
  }


  /** Set user home
   * @param home User home
   */
  public void setHome(UserHome home) {
    this.home = home;
  }


  /** Set default flag
   * @return Default flag
   */
  public boolean isDefault() {
    return def;
  }


  /** Get default flag
   * @param def Default flag
   */
  public void setDefault(boolean def) {
    this.def = def;
  }
}