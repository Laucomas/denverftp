/**
 * Gives out different IP addresses for LOCAL nd REMOTE connections.
 *
 * For more information about LOCAL/REMOTE:
 * @see com.coldcore.coloradoftp.plugin.intellipack.connection.IntelDataConnectionWrapper
 */
package com.coldcore.coloradoftp.plugin.intellipack.command;

import com.coldcore.coloradoftp.command.Reply;
import com.coldcore.coloradoftp.command.impl.ftp.PasvCommand;
import com.coldcore.coloradoftp.plugin.intellipack.util.Util;
import org.apache.log4j.Logger;

public class IntelPasvCommand extends PasvCommand {

  private static Logger log = Logger.getLogger(IntelPasvCommand.class);
  protected String localIpRegexp;
  protected String localIp;
  protected String remoteIp;


  public IntelPasvCommand() {
    super();
    localIpRegexp = "192.168.*";
  }


  /** Get regular expression of IP to determine connections from local hosts
   * @return Regular expression
   */
  public String getLocalIpRegexp() {
    return localIpRegexp;
  }


  /** Set regular expression of IP to determine connections from local hosts
   * @param localIpRegexp Regular expression
   */
  public void setLocalIpRegexp(String localIpRegexp) {
    if (localIpRegexp == null) throw new IllegalArgumentException("Invalid argument");
    this.localIpRegexp = localIpRegexp;
  }


  /** Get IP for LOCAL connections
   * @return IP address
   */
  public String getLocalIp() {
    return localIp;
  }


  /** Set IP for LOCAL connections
   * @param localIp IP address
   */
  public void setLocalIp(String localIp) {
    this.localIp = localIp;
  }


  /** Get IP for REMOTE connections
   * @return IP address
   */
  public String getRemoteIp() {
    return remoteIp;
  }


  /** Set IP for REMOTE connections
   * @param remoteIp IP address
   */
  public void setRemoteIp(String remoteIp) {
    this.remoteIp = remoteIp;
  }


  public Reply execute() {
    String ip = controlConnection.getSocketChannel().socket().getInetAddress().getHostAddress();
    boolean local = Util.checkRegExp(ip, localIpRegexp);
    log.debug("Local IP? "+local);

    //Change IP
    super.ip = local ? localIp : remoteIp;

    //Continue as normal
    return super.execute();
  }
}
