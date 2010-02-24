/**
 * @see com.coldcore.coloradoftp.core.Core
 */
package com.coldcore.coloradoftp.core.impl;

import com.coldcore.coloradoftp.connection.ConnectionPool;
import com.coldcore.coloradoftp.connection.ControlConnector;
import com.coldcore.coloradoftp.connection.DataPortListenerSet;
import com.coldcore.coloradoftp.core.Core;
import com.coldcore.coloradoftp.core.CoreStatus;
import com.coldcore.coloradoftp.factory.ObjectFactory;
import com.coldcore.coloradoftp.factory.ObjectName;
import org.apache.log4j.Logger;

public class GenericCore implements Core {

  private static Logger log = Logger.getLogger(GenericCore.class);
  protected CoreStatus status;
  protected ControlConnector controlConnector;
  protected DataPortListenerSet dataPortListenerSet;
  protected ConnectionPool controlConnectionPool;
  protected ConnectionPool dataConnectionPool;


  public GenericCore() {
    status = CoreStatus.STOPPED;
  }


  synchronized public void start() {
    if (status != CoreStatus.STOPPED) {
      log.warn("Server was running when start routine was submitted");
      throw new IllegalStateException("Stop the server first");
    }
    
    //Get all required objects
    controlConnector = (ControlConnector) ObjectFactory.getObject(ObjectName.CONTROL_CONNECTOR);
    dataPortListenerSet = (DataPortListenerSet) ObjectFactory.getObject(ObjectName.DATA_PORT_LISTENER_SET);
    controlConnectionPool = (ConnectionPool) ObjectFactory.getObject(ObjectName.CONTROL_CONNECTION_POOL);
    dataConnectionPool = (ConnectionPool) ObjectFactory.getObject(ObjectName.DATA_CONNECTION_POOL);

    try {
      //Initialize connection pools
      controlConnectionPool.initialize();
      dataConnectionPool.initialize();

      //Bind data port listeners
      if (!bindDataPortListeners()) throw new Exception("Unable to bind data port listeners");

      //Bind control connector
      if (!bindControlConnector()) throw new Exception("Unable to bind control connector");

    } catch (Throwable e) {
      //Terminate everything
      boolean noerrors = terminate();

      if (noerrors) log.info("Server terminated");
      else log.warn("Server terminated (with errors)");

      throw new RuntimeException("Cannot start server", e);
    }

    status = CoreStatus.RUNNING;
    log.info("Server started");
  }


  /** Terminate everything
   * @return TRUE if there were no errors, FALSE otherwise
   */
  protected boolean terminate() {
    boolean noerrors = true;
    if (!unbindControlConnector()) noerrors = false;
    if (!unbindDataPortListeners()) noerrors = false;
    dataConnectionPool.destroy();
    controlConnectionPool.destroy();

    //Wait a bit (just in case)
    try {
      Thread.sleep(100L);
    } catch (Throwable e) {}

    return noerrors;
  }


  synchronized public void stop() {
    if (status == CoreStatus.STOPPED) {
      log.warn("Server was stopped when stop routine was submitted");
      throw new IllegalStateException("Cannot stop the server, it is stopped already");
    }

    //Terminate everything
    boolean noerrors = terminate();

    status = CoreStatus.STOPPED;
    if (noerrors) log.info("Server stopped");
    else log.warn("Server stopped (with errors)");

    if (!noerrors) throw new RuntimeException("Abnormal server termination");
  }


  public void poison() {
    //Setting status to poisoned should tell connection pools and control connector to poison all connections
    status = CoreStatus.POISONED;
  }


  public CoreStatus getStatus() {
    return status;
  }

  
  synchronized public int getControlPort() {
	  if (controlConnector == null) {
		  controlConnector = (ControlConnector) ObjectFactory.getObject(ObjectName.CONTROL_CONNECTOR);
	  }
	  return controlConnector.getPort();
  }
  

  /** Bind data port listeners to ports. This method ignores those who refuse to bind.
   * @return TRUE if one or more listeners were bound or if there are no listeners at all, FALSE if all listeners failed
   */
  protected boolean bindDataPortListeners() {
    if (dataPortListenerSet.list().size() == 0) return true;
    return dataPortListenerSet.bind() > 0;
  }


  /** Unbind data port listeners. This method ignores those who refuse to unbind.
   * @return TRUE if all bound listeners unbound, FALSE if some listeners failed to be unbound
   */
  protected boolean unbindDataPortListeners() {
    int bound = dataPortListenerSet.boundNumber();
    int unbound = dataPortListenerSet.unbind();
    return unbound == bound;
  }


  /** Bind control connector
   * @return TRUE if connector was bound, FALSE if connector failed
   */
  protected boolean bindControlConnector() {
    try {
      controlConnector.bind();
      return true;
    } catch (Throwable e) {
      log.fatal("Cannot bind control connector on port "+controlConnector.getPort(), e);
      return false;
    }
  }


  /** Unbind control connector
   * @return TRUE if connector was unbound, FALSE if connector failed to unbind
   */
  protected boolean unbindControlConnector() {
    try {
      if (controlConnector.isBound()) controlConnector.unbind();
      return true;
    } catch (Throwable e) {
      log.fatal("Cannot unbind control connector on port "+controlConnector.getPort(), e);
      return false;
    }
  }
}
