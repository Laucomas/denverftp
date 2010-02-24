package com.coldcore.coloradoftp.connection;

/**
 * Exception which is thrown by connections when users disconnect.
 * This is considered as a normal connection termination.
 *
 *
 * ColoradoFTP - The Open Source FTP Server (http://cftp.coldcore.com)
 */
public class BrokenPipeException extends TerminatedException {

  public String toString() {
    return "Connection terminated (broken pipe)";
  }
}
