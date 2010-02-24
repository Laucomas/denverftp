package com.coldcore.coloradoftp.factory;

/**
 * Common object names for an object factory to refer to.
 *
 *
 * ColoradoFTP - The Open Source FTP Server (http://cftp.coldcore.com)
 */
public class ObjectName {

  /** Single instance objects */
  public static final String CORE = "core";
  public static final String CONTROL_CONNECTOR = "controlConnector";
  public static final String CONTROL_CONNECTION_POOL = "controlConnectionPool";
  public static final String DATA_CONNECTION_POOL = "dataConnectionPool";
  public static final String COMMAND_PROCESSOR = "commandProcessor";
  public static final String COMMAND_FACTORY = "commandFactory";
  public static final String DATA_PORT_LISTENER_SET = "dataPortListenerSet";
  public static final String FILESYSTEM = "filesystem";
  public static final String TYPE_FILTER_FACTORY = "typeFilterFactory";
  public static final String MODE_FILTER_FACTORY = "modeFilterFactory";
  public static final String STRU_FILTER_FACTORY = "struFilterFactory";
  public static final String DATA_FILTER_APPLICATOR = "dataFilterApplicator";

  /** Multiple instance objects */
  public static final String CONTROL_CONNECTION = "controlConnection";
  public static final String DATA_CONNECTION = "dataConnection";
  public static final String REPLY = "reply";
  public static final String SESSION = "session";
  public static final String DATA_PORT_LISTENER = "dataPortListener";
  public static final String DATA_CONNECTION_INITIATOR = "dataConnectionInitiator";
  public static final String LISTING_FILE = "listingFile";

  /** Single of multiple instance, depends on implementation */
  public static final String COMMAND_NOT_IMPLEMENTED = "notImplementedCommand";
  public static final String COMMAND_POISONED = "poisonedCommand";
  public static final String COMMAND_WELCOME = "welcomeCommand";
  public static final String COMMAND_LOCAL_ERROR = "localErrorCommand";
  public static final String COMMAND_SYNTAX_ERROR = "syntaxErrorCommand";
}
