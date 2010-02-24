package com.coldcore.coloradoftp.filter;

import java.nio.channels.ByteChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * Filter for data upload/download.
 *
 * This data filter sits in the channel (e.g. mounted to file) and parses channels's data on the fly.
 * Filter must be able to do parsing both ways: from server to user and vice versa.
 *
 * Data filters are applied to server's data channels by a data filter applicator based on
 * the previous user commands (e.g. MODE, TYPE and STRU).
 *
 *
 * ColoradoFTP - The Open Source FTP Server (http://cftp.coldcore.com)
 */
public interface DataFilter extends ByteChannel {

  /** Set filter name
   * @param name Filter name
   */
  public void setName(String name);


  /** Get filter name
   * @return Filter name
   */
  public String getName();


  /** Set the original channel to filter
   * @param wbc Data channel
   */
  public void setChannel(WritableByteChannel wbc);


  /** Set the original channel to filter.
   * @param rbc Data channel
   */
  public void setChannel(ReadableByteChannel rbc);


  /** Test if this data filter may change the length of data in a stream.
   * Some data filters may modify content and size of the file being transferred
   * on the fly but with the REST command in place the output file may be currupted.
   * This method prevents commands which operate on file size from executing.
   * @return TRUE if data length in a stream may change, FALSE if data length will never change
   */
  public boolean mayModifyDataLength();
}
