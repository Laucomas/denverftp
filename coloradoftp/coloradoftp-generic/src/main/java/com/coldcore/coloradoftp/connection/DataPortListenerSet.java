package com.coldcore.coloradoftp.connection;

import java.util.Set;

/**
 * Set of data port listeners.
 *
 * Because there can be many data port listeners, an object is required to
 * replicate methods to all, rather than processing each data port listener
 * individualy.
 *
 *
 * ColoradoFTP - The Open Source FTP Server (http://cftp.coldcore.com)
 */
public interface DataPortListenerSet {

  /** Bind data port listeners
   * @return Number of bound listeners
   */
  public int bind();


  /** Unbind data port listeners
   * @return Number of unbound listeners
   */
  public int unbind();


  /** Get number of boud listeners
   * @return Number of boud listeners
   */
  public int boundNumber();


  /** List listeners
   * @return Listeners (copy of the original list)
   */
  public Set<DataPortListener> list();
}
