package com.coldcore.coloradoftp.filesystem;

/**
 * Exception which is thrown by a filesystem when a requested user action
 * cannot be performed for some reason.
 *
 * Command processor must be aware of this kind of exceptions as they
 * demand special treatment.
 *
 *
 * ColoradoFTP - The Open Source FTP Server (http://cftp.coldcore.com)
 */
public class FailedActionException extends RuntimeException {

  protected FailedActionReason reason;
  protected String text;


  public FailedActionException(FailedActionReason reason) {
    this.reason = reason;
  }


  public FailedActionException(FailedActionReason reason, String text) {
    this.reason = reason;
    this.text = text;
  }


  /** Get reason of failure
   * @return Failure reason
   */
  public FailedActionReason getReason() {
    if (reason == null) return FailedActionReason.SYSTEM_ERROR;
    return reason;
  }


  /** Get optional text
   * @return Text
   */
  public String getText() {
    return text;
  }
}
