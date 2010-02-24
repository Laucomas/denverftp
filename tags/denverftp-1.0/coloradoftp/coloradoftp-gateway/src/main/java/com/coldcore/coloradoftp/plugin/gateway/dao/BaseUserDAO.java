package com.coldcore.coloradoftp.plugin.gateway.dao;

import com.coldcore.coloradoftp.plugin.gateway.Role;
import com.coldcore.coloradoftp.plugin.gateway.User;
import com.coldcore.coloradoftp.plugin.gateway.impl.RoleBean;
import com.coldcore.coloradoftp.plugin.gateway.impl.UserBean;
import org.apache.log4j.Logger;

import java.util.StringTokenizer;
import java.util.Set;
import java.util.HashSet;

/**
 * Base user DAO class.
 */
abstract public class BaseUserDAO implements UserDAO {

  private static Logger log = Logger.getLogger(BaseUserDAO.class);


  public User getUser(String username, String passowrd) throws Exception {
    User user = getUser(username);
    if (user == null) return null;

    if (user.getPassword().equals(passowrd)) {
      BaseUserDAO.log.debug("User '"+username+"' password is OK");
      return user;
    } else {
      BaseUserDAO.log.debug("Invalid password for user '"+username+"'");
      return null;
    }
  }


  public void insertUser(User user) throws Exception {
    throw new SaveException("Not implemented");
  }


  public void updateUser(User user) throws Exception {
    throw new SaveException("Not implemented");
  }


  /** Construct a new user object
   * @param username Username
   * @param password Password
   * @param roles Roles (comma separated)
   * @return User object
   */
  protected User constructUser(String username, String password, String roles) {
    User user = new UserBean();
    user.setUsername(username);
    user.setPassword(password);

    Set<Role> set = new HashSet<Role>();
    if (roles != null) {
      StringTokenizer st = new StringTokenizer(roles, ",");
      while (st.hasMoreTokens()) {
        String roleName = st.nextToken().trim();
        if (roleName.length() > 0) {
          Role role = new RoleBean();
          role.setName(roleName);
          set.add(role);
        }
      }
    }
    user.setRoles(set);
    
    return user;
  }


  /** Convert user's roles to a string
   * @param user User
   * @return String containing comma separated roles
   */
  protected String rolesAsString(User user) {
    if (user == null || user.getRoles() == null) return null;
    String str = "";
    for (Role role : user.getRoles())
      str += role.getName()+",";
    if (str.length() > 0) str = str.substring(0, str.length()-1);
    return str;
  }
}
