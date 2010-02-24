/**
 * @see com.coldcore.coloradoftp.core.CoreStorage
 *
 * This class is thread safe as it takes care of all synchronizations.
 */
package com.coldcore.coloradoftp.core.impl;

import com.coldcore.coloradoftp.core.CoreStorage;

import java.util.*;

public class SyncCoreStorage implements CoreStorage {

  protected Map<String,Object> attributes;


  public SyncCoreStorage() {
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
}
