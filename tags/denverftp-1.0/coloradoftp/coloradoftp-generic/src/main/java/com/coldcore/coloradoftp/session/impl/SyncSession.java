/**
 * @see com.coldcore.coloradoftp.session.Session
 *
 * This class is thread safe as it takes care of all synchronizations.
 */
package com.coldcore.coloradoftp.session.impl;

import com.coldcore.coloradoftp.connection.ControlConnection;
import com.coldcore.coloradoftp.session.Session;

import java.util.*;

public class SyncSession implements Session {

  protected Map<String,Object> attributes;
  protected ControlConnection controlConnection;


  public SyncSession() {
    attributes = Collections.synchronizedMap(new HashMap<String,Object>());
  }


  public void setAttribute(String key, Object value) {
    attributes.put(key, value);
  }


  public Object getAttribute(String key) {
    return attributes.get(key);
  }


  public void removeAttribute(String key) {
    attributes.remove(key);
  }


  public Set<String> getAttributeNames() {
    return new HashSet<String>(attributes.keySet());
  }


  public ControlConnection getControlConnection() {
    return controlConnection;
  }


  public void setControlConnection(ControlConnection controlConnection) {
    this.controlConnection = controlConnection;
  }
}
