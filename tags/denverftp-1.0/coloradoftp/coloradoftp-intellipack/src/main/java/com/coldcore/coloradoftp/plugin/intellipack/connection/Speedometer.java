package com.coldcore.coloradoftp.plugin.intellipack.connection;

/**
 * Contains number of bytes transferred by all connections.
 * This class reset itself every second.
 */
public class Speedometer {

  protected long bytesLastSecond;
  protected long bytesTransferredSec;
  protected long lastSecondTime;


  public Speedometer() {
    lastSecondTime = System.currentTimeMillis();
  }


  /** Test if this is a time to reset and resets */
  protected void checkTime() {
    long currentTime = System.currentTimeMillis();
    if (currentTime > lastSecondTime+1000L) {
      bytesLastSecond = bytesTransferredSec;
      bytesTransferredSec = 0L;
      lastSecondTime = currentTime;
    }
  }

  /** Add amount of transferred bytes
   * @param amount
   */
  public void add(long amount) {
    bytesTransferredSec += amount;
  }


  /** Get amount of butes transferred during the current second
   * @return Bytes transferred by all data connections
   */
  public long getBytesThisSecond() {
    checkTime();
    return bytesTransferredSec;
  }


  /** Get bytes transferred during the last second
   * @return Bytes per second of all data connections during the last second
   */
  public long getLastSecondBytes() {
    checkTime();
    return bytesLastSecond;
  }
}
