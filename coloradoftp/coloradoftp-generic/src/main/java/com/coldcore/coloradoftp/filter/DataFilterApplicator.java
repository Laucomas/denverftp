package com.coldcore.coloradoftp.filter;

import com.coldcore.coloradoftp.session.Session;

import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * This class applies flters to data channels when user uploads or downloads data.
 *
 * Data transfer commands (e.g. RETR, STOR) must use this class to apply required filters
 * before they can proceed to data transfer operations.
 *
 * Commands store filter names in user session (e.g. TYPE, MODE, STRU), such commands must
 * take care to not to store non-existing filters because the applicatior class does not
 * check if required filters exist. If a filter cannot be loaded then an exception must be
 * thrown and the command processor will report error reply to a user.
 *
 * This class usually exists in single instance and applies filters based on the information
 * retrieved from user session.
 *
 * It is recommended to apply filters so that they execute in this order:
 * upload: MODE->TYPE->STRU
 * download: STRU->TYPE->MODE
 *
 *
 * ColoradoFTP - The Open Source FTP Server (http://cftp.coldcore.com)
 */
public interface DataFilterApplicator {

  /** Apply filters to data channel
   * @param rbc Data channel
   * @param userSession User session
   * @return Filtered stream
   */
  public ReadableByteChannel applyFilters(ReadableByteChannel rbc, Session userSession);


  /** Apply filters to data channel
   * @param wbc Data channel
   * @param userSession User session
   * @return Filtered stream
   */
  public WritableByteChannel applyFilters(WritableByteChannel wbc, Session userSession);
}
