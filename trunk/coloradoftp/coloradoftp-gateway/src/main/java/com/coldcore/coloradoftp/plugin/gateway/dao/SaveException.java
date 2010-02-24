package com.coldcore.coloradoftp.plugin.gateway.dao;

/**
 * Thrown by user provider if user cannot be saved for some reason other
 * than system failure (already exists, invalid username etc.)
 */
public class SaveException extends RuntimeException {

  public SaveException() {}


  public SaveException(String message) {
    super(message);
  }


  public SaveException(String message, Throwable cause) {
    super(message, cause);
  }


  public SaveException(Throwable cause) {
    super(cause);
  }
}
