/**
 * @see com.coldcore.coloradoftp.filter.DataFilterApplicator
 */
package com.coldcore.coloradoftp.filter.impl;

import com.coldcore.coloradoftp.factory.ObjectFactory;
import com.coldcore.coloradoftp.factory.ObjectName;
import com.coldcore.coloradoftp.filter.DataFilter;
import com.coldcore.coloradoftp.filter.DataFilterApplicator;
import com.coldcore.coloradoftp.filter.DataFilterFactory;
import com.coldcore.coloradoftp.session.Session;
import com.coldcore.coloradoftp.session.SessionAttributeName;
import org.apache.log4j.Logger;

import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class GenericDataFilterApplicator implements DataFilterApplicator {

  private static Logger log = Logger.getLogger(GenericDataFilterApplicator.class);


  /** Load filter for TYPE
   * @param session User session
   * @return Data filter (never NULL)
   */
  protected DataFilter getTypeFilter(Session session) {
    String type = (String) session.getAttribute(SessionAttributeName.DATA_TYPE);
    if (type == null) type = "A";
    DataFilterFactory factory = (DataFilterFactory) ObjectFactory.getObject(ObjectName.TYPE_FILTER_FACTORY);
    return factory.create(type);
  }


  /** Load filter for MODE
   * @param session User session
   * @return Data filter (never NULL)
   */
  protected DataFilter getModeFilter(Session session) {
    String mode = (String) session.getAttribute(SessionAttributeName.DATA_MODE);
    if (mode == null) mode = "S";
    DataFilterFactory factory = (DataFilterFactory) ObjectFactory.getObject(ObjectName.MODE_FILTER_FACTORY);
    return factory.create(mode);
  }


  /** Load filter for STRUcture
   * @param session User session
   * @return Data filter (never NULL)
   */
  protected DataFilter getStruFilter(Session session) {
    String stru = (String) session.getAttribute(SessionAttributeName.DATA_STRUCTURE);
    if (stru == null) stru = "F";
    DataFilterFactory factory = (DataFilterFactory) ObjectFactory.getObject(ObjectName.STRU_FILTER_FACTORY);
    return factory.create(stru);
  }


  public ReadableByteChannel applyFilters(ReadableByteChannel rbc, Session userSession) {
    DataFilter filter = null;
    DataFilter append;

    append = getStruFilter(userSession);
    if (append != null) {
      append.setChannel(filter == null ? rbc : (ReadableByteChannel)filter);
      filter = append;
      log.debug("Applied STRU data filter: "+filter.getName());
    }

    append = getTypeFilter(userSession);
    if (append != null) {
      append.setChannel(filter == null ? rbc : (ReadableByteChannel)filter);
      filter = append;
      log.debug("Applied TYPE data filter: "+filter.getName());
    }

    append = getModeFilter(userSession);
    if (append != null) {
      append.setChannel(filter == null ? rbc : (ReadableByteChannel)filter);
      filter = append;
      log.debug("Applied MODE data filter: "+filter.getName());
    }

    return filter == null ? rbc : filter;
  }


  public WritableByteChannel applyFilters(WritableByteChannel wbc, Session userSession) {
    DataFilter filter = null;
    DataFilter append;

    append = getStruFilter(userSession);
    if (append != null) {
      append.setChannel(filter == null ? wbc : (WritableByteChannel) filter);
      filter = append;
      log.debug("Applied STRU data filter: "+filter.getName());
    }

    append = getTypeFilter(userSession);
    if (append != null) {
      append.setChannel(filter == null ? wbc : (WritableByteChannel) filter);
      filter = append;
      log.debug("Applied TYPE data filter: "+filter.getName());
    }

    append = getModeFilter(userSession);
    if (append != null) {
      append.setChannel(filter == null ? wbc : (WritableByteChannel) filter);
      filter = append;
      log.debug("Applied MODE data filter: "+filter.getName());
    }

    return filter == null ? wbc : filter;
  }
}
