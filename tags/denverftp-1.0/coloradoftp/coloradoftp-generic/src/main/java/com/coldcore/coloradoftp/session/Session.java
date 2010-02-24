package com.coldcore.coloradoftp.session;

import com.coldcore.coloradoftp.connection.ControlConnection;

import java.util.Set;

/**
 * User session.
 *
 * This object is available from a control connection and it is created for every user.
 * Session is used to store and exchange user-related information. All FTP commands and
 * other implementations who need to store or get user-related data should use user session.
 *
 *
 * ColoradoFTP - The Open Source FTP Server (http://cftp.coldcore.com)
 */
public interface Session {

  /** Set session attribute
   * @param key Key
   * @param value Value
   */
  public void setAttribute(String key, Object value);


  /** Get session attribute
   * @param key Key
   * @return Attribute value
   */
  public Object getAttribute(String key);


  /** Remove session attribute
   * @param key Key
   */
  public void removeAttribute(String key);


  /** Get names of all session attributes
   * @return Set with names
   */
  public Set<String> getAttributeNames();


  /** Get control connection of this session
   * @return Control connection
   */
  public ControlConnection getControlConnection();


  /** Set control connection of this session
   * @param controlConnection Control connection
   */
  public void setControlConnection(ControlConnection controlConnection);

}
