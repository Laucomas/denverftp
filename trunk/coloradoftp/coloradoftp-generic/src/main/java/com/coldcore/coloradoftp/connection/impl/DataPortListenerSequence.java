package com.coldcore.coloradoftp.connection.impl;

import com.coldcore.coloradoftp.connection.DataPortListener;
import com.coldcore.coloradoftp.factory.ObjectFactory;
import com.coldcore.coloradoftp.factory.ObjectName;

import java.util.HashSet;
import java.util.Iterator;

/**
 * Factory-like class to create a sequence of data port listeners.
 * This class can be used as an imput parameter into data port listener set.
 *
 * Class contructor takes port numbers and creates a sequence of data port listeners with assigned
 * port from port 1 to port 2 (inclusive). A very easy way rather than to create every data port
 * listener individualy.
 *
 * This class relies on the ObjectFactory to get instances of DataPortListeners, but the ObjectFactory
 * cannot be used in a constructor. This class overwrites important methods of the HashSet initializing
 * itself when those will be called (e.g. by DataPortListenerSet).
 */
public class DataPortListenerSequence extends HashSet<DataPortListener> {

  protected int port1;
  protected int port2;
  protected boolean initialized;

  
  public DataPortListenerSequence(int port1, int port2) {
    this.port1 = port1;
    this.port2 = port2;
  }


  protected void initialize() {
    if (initialized) return;
    initialized = true;
    for (int port = port1; port <= port2; port++) {
      DataPortListener listener = (DataPortListener) ObjectFactory.getObject(ObjectName.DATA_PORT_LISTENER);
      listener.setPort(port);
      add(listener);
    }
  }


  public Iterator<DataPortListener> iterator() {
    initialize();
    return super.iterator();
  }


  public int size() {
    initialize();
    return super.size();
  }


  public boolean isEmpty() {
    initialize();
    return super.isEmpty();
  }
}
