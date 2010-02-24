package com.coldcore.coloradoftp.factory;

/**
 * Internal factory of an object factory.
 * Object factory delegates methods to find objects to an internal factory. This class is
 * responsible for all the work.
 *
 *
 * ColoradoFTP - The Open Source FTP Server (http://cftp.coldcore.com)
 */
public interface InternalFactory {

  /** Get bean by name
   * @param name Bean name
   * @return Bean object or NULL
   */
  public Object getBean(String name);
}
