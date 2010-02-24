package com.coldcore.coloradoftp.plugin.gateway.command;

import com.coldcore.coloradoftp.plugin.gateway.User;
import com.coldcore.coloradoftp.plugin.gateway.dao.UserDAO;
import com.coldcore.coloradoftp.session.Session;
import org.apache.log4j.Logger;

/**
 * PASS command.
 */
public class PassCommand extends com.coldcore.coloradoftp.command.impl.ftp.PassCommand {

  private static Logger log = Logger.getLogger(PassCommand.class);
  protected UserDAO userDAO;


  /** Get user DAO
   * @return DAO
   */
  public UserDAO getUserDAO() {
    return userDAO;
  }


  /** Set user DAO
   * @param userDAO DAO
   */
  public void setUserDAO(UserDAO userDAO) {
    this.userDAO = userDAO;
  }


  protected boolean checkLogin(String username, String password) {
    if (userDAO == null) {
      log.warn("No UserDAO, no login is possible");
      return false;
    }

    Session session = getConnection().getSession();
    session.removeAttribute("user.object");

    try {
      User user = userDAO.getUser(username, password);

      if (user != null)
        session.setAttribute("user.object", user);

      return user != null;

    } catch (Exception e) {
      throw new RuntimeException("User DAO failed", e);
    }
  }
}
