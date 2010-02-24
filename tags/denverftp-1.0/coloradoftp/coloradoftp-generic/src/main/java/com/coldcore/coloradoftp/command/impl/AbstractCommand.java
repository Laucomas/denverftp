package com.coldcore.coloradoftp.command.impl;

import com.coldcore.coloradoftp.command.Command;
import com.coldcore.coloradoftp.command.Reply;
import com.coldcore.coloradoftp.connection.ControlConnection;
import com.coldcore.coloradoftp.factory.ObjectFactory;
import com.coldcore.coloradoftp.factory.ObjectName;
import com.coldcore.coloradoftp.session.DataOpenerType;
import com.coldcore.coloradoftp.session.LoginState;
import com.coldcore.coloradoftp.session.Session;
import com.coldcore.coloradoftp.session.SessionAttributeName;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @see com.coldcore.coloradoftp.command.Command
 *
 * Base class with few helper methods.
 */
abstract public class AbstractCommand implements Command {

  protected String name;
  protected String parameter;
  protected ControlConnection controlConnection;
  private Reply reply; //Via getter only!


  protected Reply getReply() {
    if (reply == null) {
      reply = (Reply) ObjectFactory.getObject(ObjectName.REPLY);
      reply.setCommand(this);
    }
    return reply;
  }


  /** Test if user is loggen in.
   *  If user is not logged in this method fills the internal reply with a default error message.
   * @return TRUE if user is logged in, FALSE if user is not logged in
   */
  protected boolean testLogin() {
    Session session = getConnection().getSession();
    LoginState loginState = (LoginState) session.getAttribute(SessionAttributeName.LOGIN_STATE);
    if (loginState != null) return true;
    Reply reply = getReply();
    reply.setCode("530");
    reply.setText("Not logged in.");
    return false;
  }


  /** Prepares everything for a new data connection.
   * If failed then this method fills the internal reply with a default error message.
   * It is recommended to call this method last because it is not easy to reverse changes this
   * method applies. 
   * @return TRUE if ready for a new data connection, FALSE otherwise
   */
  protected boolean prepareForDataConnection() {
    Session session = getConnection().getSession();
    DataOpenerType dtype = (DataOpenerType) session.getAttribute(SessionAttributeName.DATA_OPENER_TYPE);
    if (dtype == null) {
      Reply reply = getReply();
      reply.setCode("425");
      reply.setText("Can't open data connection.");
      return false;
    }

    //PASV is active only once as the connection is removed from the listeners set
    if (dtype == DataOpenerType.PASV) {
      session.removeAttribute(SessionAttributeName.DATA_OPENER_TYPE);
      return true;
    }

    //PORT must activate data connection initiator in the control connection
    if (dtype == DataOpenerType.PORT) {

      //Byte marker before "150" reply for a data connection initiator
      session.setAttribute(SessionAttributeName.BYTE_MARKER_150_REPLY, controlConnection.getBytesWrote());

      controlConnection.getDataConnectionInitiator().activate();
      return true;
    }

    throw new RuntimeException("BUG: Unknown data opener type provided");
  }


  /** Check syntax
   *  @param str String to check
   *  @param regexp Regular expression that defines syntax rules
   */
  protected boolean checkRegExp(String str, String regexp) {
    Pattern pattern = Pattern.compile(regexp);
    Matcher matcher = pattern.matcher(str);
    return matcher.matches();
  }


  public boolean processInInterruptState() {
    return false;
  }


  public boolean canClearInterruptState() {
    return false;
  }


  public String getName() {
    if (name == null) return null;
    return name.trim().toUpperCase(); //Return in uppercase
  }


  public void setName(String name) {
    this.name = name;
  }


  public String getParameter() {
    if (parameter == null) return ""; //Do not return NULL
    return parameter.trim();
  }


  public void setParameter(String parameter) {
    this.parameter = parameter;
  }


  public void setConnection(ControlConnection connection) {
    controlConnection = connection;
  }


  public ControlConnection getConnection() {
    return controlConnection;
  }


  public Reply executeOnParent(Command parent) {
    return null;  
  }
}
