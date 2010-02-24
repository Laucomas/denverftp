package com.coldcore.coloradoftp.plugin.intellipack.command;

import com.coldcore.coloradoftp.command.Command;
import com.coldcore.coloradoftp.command.Reply;
import com.coldcore.coloradoftp.command.impl.CommandWrapper;
import com.coldcore.coloradoftp.connection.Connection;
import com.coldcore.coloradoftp.connection.ConnectionPool;
import com.coldcore.coloradoftp.connection.DataConnection;
import com.coldcore.coloradoftp.factory.ObjectFactory;
import com.coldcore.coloradoftp.factory.ObjectName;
import org.apache.log4j.Logger;

import java.util.Set;

/**
 * This class ensures that a number of simultaneously opened files stays below the limit.
 * It checks if there are too many data connections already open before command execution.
 *
 * This class merely delegates all calls to a command of your choise. But because of the
 * reply it sends back the internal command must be a command that creates a new data
 * connection.
 *
 * Known commands that create data connections to files: STOR, STOU, RETR, PASV.
 */
public class IntelFileCommandWrapper extends CommandWrapper {

  private static Logger log = Logger.getLogger(IntelFileCommandWrapper.class);
  protected int maxDataConnections;


  public IntelFileCommandWrapper(Command command) {
    super(command);
  }


  /** Get number of total simultaneous data connections
   * @return Max amount of data connections
   */
  public int getMaxDataConnections() {
    return maxDataConnections;
  }


  /** Set number of total simultaneous data connections
   * @param maxDataConnections Max amount of data connections
   */
  public void setMaxDataConnections(int maxDataConnections) {
    this.maxDataConnections = maxDataConnections;
  }


  public Reply execute() {
    ConnectionPool dataConnectionPool = (ConnectionPool) ObjectFactory.getObject(ObjectName.DATA_CONNECTION_POOL);

    //Test if data connection is already open and waiting
    boolean open = false;
    Set<Connection> set = dataConnectionPool.list();
    for (Connection dc : set) {
      if (((DataConnection)dc).getControlConnection() == command.getConnection()) {
        log.debug("Data connection already open and waiting");
        open = true;
        break;
      }
    }

    //If no data connection then test if one can be created
    int total = dataConnectionPool.size();
    if (total > maxDataConnections && maxDataConnections > 0 && !open) {
      log.debug("Too many data connections (total "+total+")");
      Reply reply = (Reply) ObjectFactory.getObject(ObjectName.REPLY);
      reply.setCode("425");
      reply.setText("Too many data connections already, try again later.");
      return reply;
    }

    //Continue as normal
    return command.execute();
  }

}
