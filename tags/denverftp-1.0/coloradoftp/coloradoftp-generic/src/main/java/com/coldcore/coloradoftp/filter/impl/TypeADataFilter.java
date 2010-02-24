/**
 * @see com.coldcore.coloradoftp.filter.DataFilter
 *
 * TYPE A filter.
 *
 * Replaces on upload line feeds to platform default line feeds.
 * On download this class does nothing.
 *
 * This class is capable of replacing Windows (#13#10) and Unix (#10) line feeds.
 *
 * WARNING! Buffer size property must match the buffer size used by a data connection,
 * otherwise the performance will suffer.
 */
package com.coldcore.coloradoftp.filter.impl;

import org.apache.log4j.Logger;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

public class TypeADataFilter extends GenericDataFilter {

  private static Logger log = Logger.getLogger(TypeADataFilter.class);
  protected boolean windows;
  protected ByteArrayOutputStream bout;
  protected WritableByteChannel twbc;
  protected ByteBuffer rbuffer;
  protected byte[] tarray;


  public TypeADataFilter() throws IOException {
    windows = File.separator.equals("\\");

    bout = new ByteArrayOutputStream();
    twbc = Channels.newChannel(bout);
  }


  /** @deprecated Supports clients with v1.22 configuration files */
  public TypeADataFilter(int bufferSize) throws IOException {
    super();
  }


  /** Set type of OS and type of line feeds use (Windows or Unix)
   * @param windows TRUE for Windows, FALSE for Unix
   */
  public void setWindows(boolean windows) {
    this.windows = windows;
  }


  public int read(ByteBuffer dst) throws IOException {
    //Do not do anything until the destination buffer is clear, save extra efforts
    if (dst.position() > 0 || dst.limit() != dst.capacity()) return 0;

    int rcap = dst.capacity()/2;
    if (rbuffer == null || rbuffer.capacity() > rcap) {
      rbuffer = ByteBuffer.allocate(rcap);
      rbuffer.flip();
    }

    //Read data from the channel into the buffer and get it as a byte array
    rbuffer.clear();
    int read = rbc.read(rbuffer);
    if (read < 1) return read;
    byte[] bytes = rbuffer.array();

    //Replace \n to \r\n
    byte[] barr = new byte[read*2];
    byte b13 = (byte) 13;
    int put = 0;
    for (int z = 0; z < read; z++) {
      byte b = bytes[z];
      if (b == 13) continue;
      if (b == 10) barr[put++] = b13;
      barr[put++] = b;
    }

    //Write the array with replaced line feeds into the destination buffer
    dst.put(barr, 0, put);

    //Return how many bytes were put into the destination buffer
    return barr.length;
  }



  public int write(ByteBuffer src) throws IOException {

    int rcap = src.capacity()*2;
    if (rbuffer == null || rbuffer.capacity() > rcap) {
      rbuffer = ByteBuffer.allocate(rcap);
      rbuffer.flip();
    }

    //Read data from the source into the buffer and get it as a byte array
    rbuffer.clear();
    int read = src.remaining();
    rbuffer.put(src);
    byte[] bytes = rbuffer.array();

    //Replace \n to \r\n or \r\n to \n
    byte[] barr = new byte[read*2];
    byte b13 = (byte) 13;
    int put = 0;
    for (int z = 0; z < read; z++) {
      byte b = bytes[z];
      if (b == 13) continue;
      if (b == 10 && windows) barr[put++] = b13;
      barr[put++] = b;
    }

    //Write the array with replaced line feeds into the buffer
    rbuffer.clear();
    rbuffer.put(barr, 0, put);
    rbuffer.flip();

    //Forward the buffer to the underlying channel
    int i = wbc.write(rbuffer);
    if (i != put) {
      //File will be damaged, cannot allow that
      throw new RuntimeException("BUG: Data did not fit into channel");
    }

    //Return how many bytes were read from the source buffer
    return read;
  }


  public boolean mayModifyDataLength() {
    return true;
  }
}
