package com.coldcore.coloradoftp.connection;

/**
 * Data connection callback.
 *
 * When data connection terminates it notifies the callback. This is a good place to
 * insert post-upload/download logic.
 *
 *
 * ColoradoFTP - The Open Source FTP Server (http://cftp.coldcore.com)
 */
public interface DataConnectionCallback {

  /** Called after data transfer completes successfully and the data channel is closed
   * @param source Data connection that has triggered the event
   */
  public void onTransferComplete(DataConnection source);


  /** Called after data transfer aborts or client disconnects and the data channel is closed
   * @param source Data connection that has triggered the event
   */
  public void onTransferAbort(DataConnection source);
}
