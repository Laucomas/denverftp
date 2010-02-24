package com.coldcore.coloradoftp.plugin.xmlfs.parser;

/**
 * Parsing exception.
 *
 * Thrown by the configuration unit when it cannot parse XML file.
 */
public class ParsingException extends Exception {

  public ParsingException(String message) {
    super(message);
  }


  public ParsingException(String message, Throwable cause) {
    super(message, cause);
  }


  public ParsingException(Throwable cause) {
    super(cause);
  }
}