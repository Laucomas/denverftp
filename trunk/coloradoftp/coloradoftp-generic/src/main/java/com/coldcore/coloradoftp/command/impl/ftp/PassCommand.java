/**
 * Command PASS.
 * See FTP spec for details on the command.
 *
 * This implementation denies all logins but anonymous.
 * Extend this class to perform a passowrd control.
 */
package com.coldcore.coloradoftp.command.impl.ftp;

import com.coldcore.coloradoftp.command.Reply;
import com.coldcore.coloradoftp.command.impl.AbstractCommand;
import com.coldcore.coloradoftp.session.LoginState;
import com.coldcore.coloradoftp.session.Session;
import com.coldcore.coloradoftp.session.SessionAttributeName;
import org.apache.log4j.Logger;

public class PassCommand extends AbstractCommand {

  private static Logger log = Logger.getLogger(PassCommand.class);
  private String emailRegExp;


  public PassCommand() {
    emailRegExp = "([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]*)";
  }


  /** Get email regular expression
   * @return Email regular expression
   */
  public String getEmailRegExp() {
    return emailRegExp;
  }


  /** Set email regular expression
   * @param emailRegExp Email regular expression
   */
  public void setEmailRegExp(String emailRegExp) {
    this.emailRegExp = emailRegExp;
  }


  public Reply execute() {
    Reply reply = getReply();

    String password = getParameter();

    Session session = getConnection().getSession();
    LoginState loginState = (LoginState) session.getAttribute(SessionAttributeName.LOGIN_STATE);
    if (loginState != null) {
      log.debug("Already logged in user submits PASS command again");
      reply.setCode("503");
      reply.setText("Already logged in.");
      return reply;
    }

    String username = (String) session.getAttribute(SessionAttributeName.USERNAME);
    if (username == null) {
      reply.setCode("503");
      reply.setText("Send your user name.");
      return reply;
    }

    if (password.length() == 0) {
      log.debug("Invalid syntax of submitted password");
      reply.setCode("501");
      reply.setText("Send your password.");
      return reply;
    }

    //Anonymous login (password is user's email)
    if (username.equalsIgnoreCase("anonymous")) {
      if (checkRegExp(password, emailRegExp)) {
        log.debug("Anonymous login successful for username: "+username+" ("+password+")");
        session.setAttribute(SessionAttributeName.LOGIN_STATE, LoginState.ANONYMOUS);
        session.setAttribute(SessionAttributeName.PASSWORD, password);
        reply.setCode("230");
        reply.setText("User logged in, proceed.");
        return reply;
      } else {
        log.debug("Anonymous login failed for username: "+username+" ("+password+")");
        reply.setCode("530");
        reply.setText("Not logged in.");
        return reply;
      }
    }

    //Regular login
    if (checkLogin(username, password)) {
      log.debug("Login successful for username: "+username);
      session.setAttribute(SessionAttributeName.LOGIN_STATE, LoginState.REGULAR);
      /* For security reasons password is not stored into the session */
      reply.setCode("230");
      reply.setText("User logged in, proceed.");
      return reply;
    }

    //Login failed
    log.debug("Login failed for username: "+username);
    reply.setCode("530");
    reply.setText("Not logged in.");
    return reply;
  }


  /** Test user login.
   * This implementation always returns FALSE.
   * @param username Username
   * @param password Password
   * @return TRUE if login is OK, FALSE otherwise
   */
  protected boolean checkLogin(String username, String password) {
    return false;
  }
}
