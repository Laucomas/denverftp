package com.coldcore.coloradoftp.session;

/**
 * User login state.
 * This is used by FTP commands to query user's login state.
 *
 * Once user logs in he/she is assigned a proper login state which must not
 * be removed from the session ever (even after QUIT command).
 * The only way to clear login state is to disconnect a control connection.
 *
 *
 * ColoradoFTP - The Open Source FTP Server (http://cftp.coldcore.com)
 */
public enum LoginState {
  ANONYMOUS, //Anonymous login
  REGULAR    //Regular registered user
}
