package com.coldcore.coloradoftp.session;

/**
 * Common attribute names for a user session to refer to.
 *
 *
 * ColoradoFTP - The Open Source FTP Server (http://cftp.coldcore.com)
 */
public class SessionAttributeName {

  public static final String USERNAME = "username";
  public static final String PASSWORD = "password";
  public static final String DATA_TYPE = "data.type"; //Default is "A" (text)
  public static final String DATA_MARKER = "data.marker";
  public static final String DATA_STRUCTURE = "data.structure"; //Default is "F" (file)
  public static final String DATA_MODE = "data.mode"; //Default is "S" (stream)
  public static final String DATA_CONNECTION_MODE = "data.connection.mode";
  public static final String DATA_CONNECTION_CHANNEL = "data.connection.channel";
  public static final String DATA_CONNECTION_FILENAME = "data.connection.filename";
  public static final String LOGIN_STATE = "login.state";
  public static final String DATA_OPENER_TYPE = "data.opener.type";
  public static final String BYTE_MARKER_150_REPLY = "byte.marker.150.reply";
  public static final String BYTE_MARKER_POISONED = "byte.marker.poisoned";
  public static final String CURRENT_DIRECTORY = "current.directory";
}
