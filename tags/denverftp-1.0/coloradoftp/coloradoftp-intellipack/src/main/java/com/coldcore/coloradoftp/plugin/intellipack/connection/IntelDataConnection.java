package com.coldcore.coloradoftp.plugin.intellipack.connection;

import com.coldcore.coloradoftp.connection.impl.GenericDataConnection;
import com.coldcore.coloradoftp.factory.ObjectFactory;
import com.coldcore.coloradoftp.plugin.intellipack.util.Util;
import org.apache.log4j.Logger;

import java.nio.channels.SocketChannel;

/**
 * Data connection with configurable speed (bytes per second) and timeout (seconds).
 *
 * This class devides data connection into 2 groups: LOCAL and REMOTE. Because of this
 * this class can work with machines that have 1 or 2 network cards. One card is
 * considered for local connections and other is for remote connections. Throughtput
 * of each card is configured individualy.
 */
public class IntelDataConnection extends GenericDataConnection {

  private static Logger log = Logger.getLogger(IntelDataConnection.class);
  protected long totalSpeedLocal;
  protected long totalSpeedRemote;
  protected String localIpRegexp;
  protected long lastSecondTime;
  protected long bytesLastSec;
  protected boolean local;
  protected int timeout;
  protected long lastActiveTime;
  protected Speedometer speedometer;


  public IntelDataConnection(int bufferSize) {
    super(bufferSize);

    localIpRegexp = "192.168.*";
    lastActiveTime = System.currentTimeMillis();
    lastSecondTime = System.currentTimeMillis();
  }


  /** Get channel max speed for connections from local hosts
   * @return Bytes per second
   */
  public long getTotalSpeedLocal() {
    return totalSpeedLocal;
  }


  /** Set channel max speed for connections from local hosts
   * @param totalSpeedLocal Bytes per second
   */
  public void setTotalSpeedLocal(long totalSpeedLocal) {
    if (totalSpeedLocal < 0) throw new IllegalArgumentException("Negative argument");
    this.totalSpeedLocal = totalSpeedLocal;
  }


  /** Get channel max speed for connections not from local hosts
   * @return Bytes per second
   */
  public long getTotalSpeedRemote() {
    return totalSpeedRemote;
  }


  /** Set channel max speed for connections not from local hosts
   * @param totalSpeedRemote Bytes per second
   */
  public void setTotalSpeedRemote(long totalSpeedRemote) {
    if (totalSpeedRemote < 0) throw new IllegalArgumentException("Negative argument");
    this.totalSpeedRemote = totalSpeedRemote;
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


  public void initialize(SocketChannel channel) {
    super.initialize(channel);

    String ip = channel.socket().getInetAddress().getHostAddress();
    local = Util.checkRegExp(ip, localIpRegexp);
    IntelDataConnection.log.debug("Local IP? "+local);

    speedometer = (Speedometer) ObjectFactory.getObject(
            local?"dataSpeedometer.local":"dataSpeedometer.remote");
  }


  protected void read() throws Exception {
    if (isOverSpeed()) {
      Thread.sleep(sleep);
      return;
    }

    long bytes = bytesRead;
    super.read();
    long diff = bytesRead-bytes;

    if (diff > 0) {
      speedometer.add(diff); //Update speed
      lastActiveTime = System.currentTimeMillis(); //Update last active time
    }
  }


  protected void write() throws Exception {
    if (isOverSpeed()) {
      Thread.sleep(sleep);
      return;
    }

    long bytes = bytesWrote;
    super.write();
    long diff = bytesWrote-bytes;

    if (diff > 0) {
      speedometer.add(diff); //Update speed
      lastActiveTime = System.currentTimeMillis(); //Update last active time
    }


  }


  /** Test if total channel speed barrier is broken
   * @return TRUE if it is broken or FALSE otherwise
   */
  protected boolean isOverSpeed() {
    long currentTime = System.currentTimeMillis();
    long transferred = bytesRead+bytesWrote;

    if (currentTime > lastSecondTime+1000L) {
      bytesLastSec = transferred;
      lastSecondTime = currentTime;
    }

    long speedLimit = local ? totalSpeedLocal : totalSpeedRemote;

    return speedometer.getBytesThisSecond() > speedLimit && speedLimit > 0;
  }


  public void service() throws Exception {
    //Timeout test
    long currentTime = System.currentTimeMillis();
    if (currentTime > lastActiveTime+timeout*1000L && timeout > 0 && userAborted) {
      IntelDataConnection.log.warn("Connection timeout, aborting data transfer");
      abort();
    }

    //Service...
    super.service();
  }
}
