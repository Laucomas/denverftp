package com.coldcore.coloradoftp.connection;

/**
 * Data connection.
 *
 * Data connections perform data transfers between users and the server. For example when a user
 * requires to upload a file, a new data connection is created. When all data is transferred, the
 * data connection dies. Server provides channels for data connections to read from or to
 * write data into. Channels are usually mounted to files, and it is the responsibility of
 * data connections to close channels in the end.
 *
 * User may have only one data connection which he/she can abort with the ABOR command.
 *
 * Data connection must send a reply to the user after:
 * 1. User aborts it (ABOR command)
 * 2. Data transfer finished
 * 3. Data transfer failed
 * The reply must comply with the command user submitted to trigger a data transfer. Data
 * connections are not allowed to die without producing a reply.
 *
 * For a data transfer to begin a data connection must know where to read file data from
 * or where to save it to (that is a channel). Also it requires a filename (but not for a
 * directory listing, which is also served by data connections) and a command name to
 * produce the correct reply in the end. The best approach wuld be to keep that information in
 * a user session and let a data connection to decide itself when, how and what to transfer.
 *
 * Data connections may be established in two ways: user connects to the server (PASV) or the
 * server connects to a user (PORT).
 *
 * When a user downloads a file, server can test if the complete file has beed downloaded (because
 * it has a stream from the file). But when a user uploads a file, server has no way to test if
 * the data transfer completed sucessully or if the user disconnected earlier before all the data
 * has been uploaded. As a result, all uploads may be considered as sucessful.
 *
 * Because control connection has a reference to a data connection, the data connection must
 * clear that reference when it is ready to be destroyed.
 *
 *
 * ColoradoFTP - The Open Source FTP Server (http://cftp.coldcore.com)
 */
public interface DataConnection extends Connection {

  /** Get control connection of this data connection
   * @return Control connection
   */
  public ControlConnection getControlConnection();


  /** Set control connection of this control connection
   * @param controlConnection Control connection
   */
  public void setControlConnection(ControlConnection controlConnection);


  /** User submitted ABOR command.
   * FTP spec: first comes "426" reply to tell that the data transfer has been
   * terminated, and then "226" reply to ABOR.
   */
  public void abort();


  /** Destory the connection silently (for urgent connection termination).
   * In this method data connection must not use control connection to post
   * a reply to a user.
   */
  public void destroyNoReply();


  /** Set a callback object to receive notifications from this data connection.
   * @param callback Callback
   */
  public void setDataConnectionCallback(DataConnectionCallback callback);
}
