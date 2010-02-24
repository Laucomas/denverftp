package com.coldcore.coloradoftp.connection;

/**
 * Exception which is thrown by a connection when it dies from poison.
 * This is considered as a normal connection termination.
 *
 *
 * ColoradoFTP - The Open Source FTP Server (http://cftp.coldcore.com)
 */
public class PoisonedException extends TerminatedException {

  public String toString() {
    return "Connection terminated (poisoned)";
  }
}
