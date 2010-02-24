package com.coldcore.coloradoftp.session;

/**
 * The type of the data opener which must be used for the upcoming data transfer.
 * There are two types: data connection is initiated by a user (PASV) or data connection is initiated
 * by the server (PORT). PASV and PORT commands must set this property to a correct value,
 * so the data transfer commands know how to initialize a new data connections.
 *
 *
 * ColoradoFTP - The Open Source FTP Server (http://cftp.coldcore.com)
 */
public enum DataOpenerType {
  PASV, //Connection will be initiated by user and already awaits in one of the data port listeners
  PORT  //Connection will be initiated by server, so data connection initiator must be configured
}
