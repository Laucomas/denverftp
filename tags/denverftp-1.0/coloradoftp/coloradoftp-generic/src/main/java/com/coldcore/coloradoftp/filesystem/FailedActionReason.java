package com.coldcore.coloradoftp.filesystem;

/**
 * Defines a reason of why filesystem failed to perform user action.
 *
 *
 * ColoradoFTP - The Open Source FTP Server (http://cftp.coldcore.com)
 */
public enum FailedActionReason {
  NO_PERMISSIONS,  //No permissions for operation
  PATH_ERROR,      //Requested path not found, already exists etc.
  SYSTEM_ERROR,    //System error
  NOT_IMPLEMENTED, //Operation is not implemented
  INVALID_INPUT,   //Supplied user's input if of invalid syntax
  OTHER            //Other reason
}
