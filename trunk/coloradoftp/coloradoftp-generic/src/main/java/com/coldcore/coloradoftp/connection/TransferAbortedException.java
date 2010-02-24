package com.coldcore.coloradoftp.connection;

/**
 * Exception which is thrown by data connections when users abort data transfers or disconnect
 * while data transfers are still active.
 * This is considered as a normal connection termination.
 *
 *
 * ColoradoFTP - The Open Source FTP Server (http://cftp.coldcore.com)
 */
public class TransferAbortedException extends TerminatedException {

  public String toString() {
    return "Connection terminated (transfer aborted)";
  }
}
