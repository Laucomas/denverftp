package com.coldcore.coloradoftp.connection.impl;

import com.coldcore.coloradoftp.connection.DataPortListener;
import com.coldcore.coloradoftp.connection.DataPortListenerSet;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Set;

/**
 * Set of data port listeners.
 */
public class GenericDataPortListenerSet implements DataPortListenerSet {

  private static Logger log = Logger.getLogger(GenericDataPortListenerSet.class);
  private Set<DataPortListener> listeners;


  public GenericDataPortListenerSet(Set<DataPortListener> listeners) {
    this.listeners = new HashSet<DataPortListener>(listeners);
  }


  public synchronized int bind() {
    if (listeners.isEmpty()) {
      log.warn("No data port listeners configured, PASV command will be disabled");
      return 0;
    }

    int bound = 0;
    for (DataPortListener listener : listeners)
      try {
        listener.bind();
        bound++;
      } catch (Throwable e) {
        log.error("Cannot bind data port listener on port "+listener.getPort()+" (ignoring)", e);
      }

    if (bound == 0) {
      log.fatal("Could not bind any of data port listeners");
      return 0;
    }

    log.info("Data port listeners bound: "+bound+"/"+listeners.size());
    return bound;
  }


  public synchronized int unbind() {
    int bound = 0;
    int unbound = 0;
    for (DataPortListener listener : listeners)
      if (listener.isBound())
        try {
          bound++;
          listener.unbind();
          unbound++;
        } catch (Throwable e) {
          log.error("Cannot unbind data port listener on port "+listener.getPort()+" (ignoring)", e);
        }

    log.info("Data port listeners unbound: "+unbound+"/"+bound);
    return unbound;
  }


  public int boundNumber() {
    int bound = 0;
    for (DataPortListener listener : listeners)
      if (listener.isBound()) bound++;
    return bound;
  }


  public Set<DataPortListener> list() {
    return new HashSet<DataPortListener>(listeners);
  }
}
