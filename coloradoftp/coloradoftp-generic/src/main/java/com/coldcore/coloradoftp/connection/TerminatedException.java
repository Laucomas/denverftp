package com.coldcore.coloradoftp.connection;

/**
 * Exception which is thrown by a connection when it is terminated.
 * This is considired as a normal connection termination.
 *
 * This class is a base class for all normal connection termination exceptions.
 *
 *
 * ColoradoFTP - The Open Source FTP Server (http://cftp.coldcore.com)
 */
public class TerminatedException extends Exception {

  public String toString() {
    return "Connection terminated (normal)";
  }
}
