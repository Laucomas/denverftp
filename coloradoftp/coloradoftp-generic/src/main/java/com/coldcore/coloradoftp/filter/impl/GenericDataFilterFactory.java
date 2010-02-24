/**
 * @see com.coldcore.coloradoftp.filter.DataFilterFactory
 */
package com.coldcore.coloradoftp.filter.impl;

import com.coldcore.coloradoftp.factory.ObjectFactory;
import com.coldcore.coloradoftp.filter.DataFilter;
import com.coldcore.coloradoftp.filter.DataFilterFactory;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GenericDataFilterFactory implements DataFilterFactory {

  private Map<String,String> map;


  public GenericDataFilterFactory(Map<String,String> filtersRef) {
    map = new HashMap<String,String>(filtersRef);
  }


  public Set<String> listNames() {
    return new HashSet<String>(map.keySet());
  }


  public DataFilter create(String name) {
    if (!map.containsKey(name))
      throw new IllegalArgumentException("Filter "+name+" cannot be loaded");

    String ref = map.get(name);
    if (ref == null || ref.length() == 0) return null; //No filter required

    DataFilter filter = (DataFilter) ObjectFactory.getObject(ref);
    filter.setName(name);
    return filter;
  }
}
