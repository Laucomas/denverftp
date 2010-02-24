/*
 * Date: Dec 15, 2001
 * Time: 4:04:35 AM
 * Byte array operatios.
 */

package com.coldcore.misc5;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public class CByte {

  private CByte() {}


  /** Reads data from the sorce into a byte array
   *  @param src The source to read data from
   *  @return The source content
   */
  public static byte[] toByteArray(ReadableByteChannel src) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
    WritableByteChannel   trg = Channels.newChannel(out);
    inputToOutput(src, trg);
    return out.toByteArray();
  }


  /** Reads the data from the source and writes it to the destination
   *  @param src The source to read data from
   *  @param trg The target to write data to
   */
  public static void inputToOutput(ReadableByteChannel src, WritableByteChannel trg) throws IOException {
    ByteBuffer bb = ByteBuffer.allocateDirect(4096);
    while (src.read(bb) != -1) {
      bb.flip();
      while (bb.hasRemaining()) trg.write(bb);
      bb.clear();
    }
  }


  /** Convenience method
   * @see com.coldcore.misc5.CByte#toByteArray(java.nio.channels.ReadableByteChannel)
   */
  public static byte[] toByteArray(InputStream in) throws IOException {
    return toByteArray(Channels.newChannel(in));
  }


  /** Convenience method
   * @see com.coldcore.misc5.CByte#inputToOutput(java.nio.channels.ReadableByteChannel, java.nio.channels.WritableByteChannel)
   */
  public static void inputToOutput(InputStream in, OutputStream out) throws IOException {
    inputToOutput(Channels.newChannel(in), Channels.newChannel(out));
  }
}
