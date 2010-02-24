package com.coldcore.coloradoftp.connection;

/**
 * Data connection transfer mode.
 * Explains a reason what's for users request data transfers.
 *
 *
 * ColoradoFTP - The Open Source FTP Server (http://cftp.coldcore.com)
 */
public enum DataConnectionMode {
  STOR, //Read data from the user and save it into file
  STOU, //Same as STOR, only send "250" reply at the end of the transfer
  RETR, //Read data from file and send it to user
  LIST  //Send directory content to user
}
