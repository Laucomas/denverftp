package com.coldcore.coloradoftp.connection;

/**
 * Exception which is thrown by a data connection when a data transfer finishes successfully.
 * This is considered as a normal connection termination.
 *
 *
 * ColoradoFTP - The Open Source FTP Server (http://cftp.coldcore.com)
 */
public class TransferCompleteException extends TerminatedException {

  public String toString() {
    return "Connection terminated (transfer complete)";
  }
}
