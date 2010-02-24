package com.coldcore.coloradoftp.core;

/**
 * Server status.
 *
 *
 * ColoradoFTP - The Open Source FTP Server (http://cftp.coldcore.com)
 */
public enum CoreStatus {
  STOPPED,  //Stopped
  RUNNING,  //Running
  POISONED  //Shutting down and waiting for all connections to disconnect
}
