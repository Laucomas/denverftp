package com.coldcore.coloradoftp.filter;

import java.util.Set;

/**
 * Data filter factory (creates all available data filters).
 *
 * Filter factories are used to get filters that must be applied to data channels
 * upon uload or download.
 *
 * Commands like TYPE, MODE and STRU use this factory to list all available filters
 * user can apply to the data channel. When a user wants to make upload or download
 * the filter applicator applies required filters to server's data channel.
 *
 *
 * ColoradoFTP - The Open Source FTP Server (http://cftp.coldcore.com)
 */
public interface DataFilterFactory {

  /** List all available filter names (name match command's agrument filter was created for)
   * @return Filter names (copy of the original set)
   */
  public Set<String> listNames();


  /** Create filter object
   * @param name Filter name
   * @return If found then filter object, if filter name exists but no value is
   *         set then NULL (no filter required), otherwise throws an exception
   */
  public DataFilter create(String name);
}
