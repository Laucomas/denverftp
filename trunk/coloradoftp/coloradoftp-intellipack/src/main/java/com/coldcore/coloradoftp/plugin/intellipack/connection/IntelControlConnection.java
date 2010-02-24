package com.coldcore.coloradoftp.plugin.intellipack.connection;

import com.coldcore.coloradoftp.command.Reply;
import com.coldcore.coloradoftp.connection.impl.GenericControlConnection;
import com.coldcore.coloradoftp.factory.ObjectFactory;
import com.coldcore.coloradoftp.factory.ObjectName;
import com.coldcore.coloradoftp.session.LoginState;
import com.coldcore.coloradoftp.session.SessionAttributeName;
import org.apache.log4j.Logger;

import java.nio.channels.SocketChannel;

/**
 * Control connection with timeout (seconds) and login timeout (seconds).
 */
public class IntelControlConnection extends GenericControlConnection {

  private static Logger log = Logger.getLogger(IntelControlConnection.class);
  protected int timeout;
  protected long lastActiveTime;
  protected int loginTimeout;
  protected long createdTime;
  protected Speedometer speedometer;


  public IntelControlConnection(int bufferSize) {
    super(bufferSize);

    lastActiveTime = System.currentTimeMillis();
    createdTime = System.currentTimeMillis();
  }


  /** Get connection inactive interval
   * @return Seconds
   */
  public int getTimeout() {
    return timeout;
  }


  /** Set connection inactive interval
   * @param timeout Seconds
   */
  public void setTimeout(int timeout) {
    if (timeout < 0) throw new IllegalArgumentException("Negative argument");
    this.timeout = timeout;
  }


  /** Get login timeout (the time user can spend without logging in)
   * @return Seconds
   */
  public int getLoginTimeout() {
    return loginTimeout;
  }


  /** Set login timeout (the time user can spend without logging in)
   * @param loginTimeout Seconds
   */
  public void setLoginTimeout(int loginTimeout) {
    if (loginTimeout < 0) throw new IllegalArgumentException("Negative argument");
    this.loginTimeout = loginTimeout;
  }


  public void initialize(SocketChannel channel) {
    super.initialize(channel);
    speedometer = (Speedometer) ObjectFactory.getObject("controlSpeedometer");
  }


  protected void read() throws Exception {
    long bytes = bytesRead;
    super.read();
    long diff = bytesRead-bytes;

    if (diff > 0) {
      speedometer.add(diff); //Update speed
      lastActiveTime = System.currentTimeMillis(); //Update last active time
    }
  }


  protected void write() throws Exception {
    long bytes = bytesWrote;
    super.write();
    long diff = bytesWrote-bytes;

    if (diff > 0) speedometer.add(diff); //Update speed
  }


  public void service() throws Exception {
    //Control connection does not expire until there is a data connection
    long currentTime = System.currentTimeMillis();
    if (dataConnection != null) {
      lastActiveTime = currentTime;
    }

    //Timeout test (general)
    if (currentTime > lastActiveTime+timeout*1000L && timeout > 0) {
      IntelControlConnection.log.warn("Connection timeout, destroying");
      destroy();
      return;
    }

    //Timeout test (login)
    LoginState loginState = (LoginState) session.getAttribute(SessionAttributeName.LOGIN_STATE);
    if (loginState == null && !poisoned && currentTime > createdTime+loginTimeout*1000L && loginTimeout > 0) {
      IntelControlConnection.log.warn("Login timeout, poisoning");
      Reply reply = (Reply) ObjectFactory.getObject(ObjectName.REPLY);
      reply.setCode("421");
      reply.setText("Login timeout, closing control connection.");
      reply(reply);
      poison();
    }

    //Service...
    super.service();
  }
}
