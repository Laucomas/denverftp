/**
 * Command USER.
 * See FTP spec for details on the command.
 */
package com.coldcore.coloradoftp.command.impl.ftp;

import com.coldcore.coloradoftp.command.Reply;
import com.coldcore.coloradoftp.command.impl.AbstractCommand;
import com.coldcore.coloradoftp.session.LoginState;
import com.coldcore.coloradoftp.session.Session;
import com.coldcore.coloradoftp.session.SessionAttributeName;
import org.apache.log4j.Logger;

public class UserCommand extends AbstractCommand {

  private static Logger log = Logger.getLogger(UserCommand.class);
  private boolean anonymous;


  public UserCommand() {
    anonymous = true;
  }


  public boolean isAnonymous() {
    return anonymous;
  }


  public void setAnonymous(boolean anonymous) {
    this.anonymous = anonymous;
  }


  public Reply execute() {
    Reply reply = getReply();

    String username = getParameter();
    username = username.toLowerCase();

    Session session = getConnection().getSession();
    LoginState loginState = (LoginState) session.getAttribute(SessionAttributeName.LOGIN_STATE);
    if (loginState != null) {
      log.debug("Already logged in user submits USER command again");
      reply.setCode("503");
      reply.setText("Already logged in.");
      return reply;
    }

    session.removeAttribute(SessionAttributeName.USERNAME);

    if (username.length() == 0) {
      log.debug("Invalid syntax of submitted username");
      reply.setCode("501");
      reply.setText("Send your user name.");
      return reply;
    }

    if (username.equals("anonymous") && !anonymous) {
      log.debug("Anonymous login is disabled");
      reply.setCode("332");
      reply.setText("Anonymous login disabled, need account for login.");
      return reply;
    }

    session.setAttribute(SessionAttributeName.USERNAME, username);
    log.debug("Accepted username: "+username);

    if (username.equals("anonymous")) {
      reply.setCode("331");
      reply.setText("Guest login okay, send your complete e-mail address as password.");
      return reply;
    }

    reply.setCode("331");
    reply.setText("User name okay, need password.");
    return reply;
  }
}
