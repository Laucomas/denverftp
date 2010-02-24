/**
 * Created by IntelliJ IDEA.
 * User: Power
 * Date: Aug 8, 2003
 * Time: 4:45:59 PM
 * File operatios.
 */
package com.coldcore.misc5;

import java.io.*;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CFile {

  private File file;


  public CFile(File file) {
    this.file = file;
  }


  /** @return Internal file object. */
  public File getFile() {
    return file;
  }


  /** Sets internal file object.
   *  @param file File object.
   */
  public void setFile(File file) {
    this.file = file;
  }


  /** Tries to acquire an exclusive or shared lock on the entire file.
   *  Note: File locks are visible only to external processes and will not affect any processes within this JVM.
   *  @param file   The file that must be locked.
   *  @param shared Whether this will be shared or exclusive lock.
   *  @throws IOException When lock cannot be acquired immediately.
   *  @return A file lock if method succeeds.
   */
  public static FileLock lockFile(File file, boolean shared) throws IOException {
    FileChannel fc = null;
    try {
      fc = new RandomAccessFile(file, shared ? "r" : "rw").getChannel();
      FileLock fileLock = fc.tryLock(0L, Long.MAX_VALUE, shared);
      if (!testFileLock(fileLock)) throw new IOException("Unable to acquire a file lock.");
      return fileLock;
    } finally {
      try {
        if (fc != null) fc.close();
      } catch (IOException e) {}
    }
  }


  /** Releases the file lock.
   *  @param fileLock File lock to release.
   */
  public static void releaseFileLock(FileLock fileLock) throws IOException {
    if (testFileLock(fileLock)) fileLock.release();
  }


  /** Tests the file lock.
   *  @param fileLock File lock to test.
   *  @return TRUE if the lock is valid, FALSE othewise.
   */
  public static boolean testFileLock(FileLock fileLock) {
    return fileLock != null && fileLock.isValid();
  }


  /** Opens the file and writes data into it from the specified position.
   *  Warning! All data starting from that position will be overwritten.
   *  Note: If file does not exist it will be created.
   *  Warning! This method works incorrectly for channels longer then Integer.MAX_VALUE.
   *  @param src The source to read data from.
   *  @param pos The file position to begin writing from.
   */
  public void insertFileData(ReadableByteChannel src, long pos) throws IOException {
    FileLock fileLock = lockFile(file, false);

    FileChannel fc = null;
    try {
      fc = new RandomAccessFile(file, "rw").getChannel();
      fc.transferFrom(src, pos, Integer.MAX_VALUE);
    } finally {
      try {
        if (fc != null) fc.close();
      } catch (IOException e) {}
      try {
        releaseFileLock(fileLock);
      } catch (IOException e) {}
    }
  }


  /** Truncates the file.
   *  Note: If file does not exist it will be created.
   *  @param size The new size of the file.
   */
  public void truncateFile(long size) throws IOException {
    FileLock fileLock = lockFile(file, false);

    FileChannel fc = null;
    try {
      fc = new RandomAccessFile(file, "rw").getChannel();
      fc.truncate(size);
    } finally {
      try {
        if (fc != null) fc.close();
      } catch (IOException e) {}
      try {
        releaseFileLock(fileLock);
      } catch (IOException e) {}
    }
  }


  /** Inserts a hole into the file. This method moves all of data starting from specified position forward,
   *  this makes a "hole" in the file where new data can be inserted.
   *  Note: If file does not exist it will be created.
   *  @param pos  The file position to begin moving from.
   *  @param size The size of a hole.
   */
  public void insertFileHole(long pos, long size) throws IOException {
    FileLock fileLock = lockFile(file, false);

    FileChannel fc = null;
    try {
      fc = new RandomAccessFile(file, "rw").getChannel();

      long wpos  = fc.size();
      long rpos  = -1;
      long dsize = size;

      while (rpos != pos) {
        rpos = wpos - dsize;

        if (rpos < pos) {
          dsize -= pos-rpos;
          wpos  += pos-rpos;
          rpos   = pos;
        }

        fc.position(wpos);
        fc.transferTo(rpos, dsize, fc);

        wpos -= dsize;
      }
    } finally {
      try {
        if (fc != null) fc.close();
      } catch (IOException e) {}
      try {
        releaseFileLock(fileLock);
      } catch (IOException e) {}
    }
  }


  /** Inserts a hole into the file. This method moves all of data starting from specified position forward,
   *  this makes a "hole" in the file where new data can be inserted.
   *  Note: If file does not exist it will be created.
   *  @param pos  The file position to begin moving from.
   *  @param size The size of a hole.
   */
  public void removeFileHole(long pos, long size) throws IOException {
    FileLock fileLock = lockFile(file, false);

    FileChannel fc = null;
    try {
      fc = new RandomAccessFile(file, "rw").getChannel();
      fc.position(pos+size);
      fc.transferFrom(fc, pos, fc.size()-pos-size);
      fc.truncate(pos+size);
    } finally {
      try {
        if (fc != null) fc.close();
      } catch (IOException e) {}
      try {
        releaseFileLock(fileLock);
      } catch (IOException e) {}
    }
  }


  /** Reads data from the file.
   *  Warning! This method works incorrectly for files longer then Integer.MAX_VALUE because byte[] simply
   *  cannot store that much data.
   *  @return Data of the file.
   */
  public byte[] readFile() throws IOException {
    FileLock fileLock = lockFile(file, true);

    FileChannel fc = null;
    try {
      fc = new RandomAccessFile(file, "r").getChannel();
      ByteArrayOutputStream out = new ByteArrayOutputStream((int)fc.size());
      WritableByteChannel   trg = Channels.newChannel(out);
      fc.transferTo(0, fc.size(), trg);
      return out.toByteArray();
    } finally {
      try {
        if (fc != null) fc.close();
      } catch (IOException e) {}
      try {
        releaseFileLock(fileLock);
      } catch (IOException e) {}
    }
  }


  /** Copies this file into another file.
   *  Note: If new file already exists it will be overwritten.
   *  @param trgFile The target file.
   */
  private void copyFile(File trgFile) throws IOException {
    FileLock srcLock = lockFile(file,    true);
    FileLock trgLock = lockFile(trgFile, false);

    FileChannel src = null;
    FileChannel trg = null;
    try {
      src = new RandomAccessFile(file,    "r").getChannel();
      trg = new RandomAccessFile(trgFile, "rw").getChannel();
      trg.truncate(0);
      src.transferTo(0, src.size(), trg);
    } finally {
      try {
        if (trg != null) trg.close();
      } catch (IOException e) {}
      try {
        if (src != null) src.close();
      } catch (IOException e) {}
      try {
        releaseFileLock(trgLock);
      } catch (IOException e) {}
      try {
        releaseFileLock(srcLock);
      } catch (IOException e) {}
    }
  }


  /** Lists content of this dir.
   *  @param files If TRUE then only files will be returned, dirs otherwise.
   *  @return A sorted array that contains files or dirs.
   */
  public File[] list(boolean files) {
    File[] list = file.listFiles();
    ArrayList<File> a = new ArrayList<File>();
    for (File f : list)
      if ((files && f.isFile()) || (!files && !f.isFile()))
        a.add(f);

    Collections.sort(a, new Comparator() {
      public int compare(Object o1, Object o2) {
        return ((File) o1).getName().compareToIgnoreCase(((File) o2).getName());
      }
    });

    return (File[]) a.toArray(new File[a.size()]);
  }


  /** Copies this file or directory into another location.
   *  Note: This method overwrites only existing files that have the same names.
   *  @param trgFile A location to copy to.
   */
  public void copy(File trgFile) throws IOException {
    if (file.isFile()) copyFile(trgFile);
    else {
      //Create target dir.
      if (!trgFile.exists()) trgFile.mkdirs();

      //Copy files to target dir.
      File[] list = list(true);
      for (File f : list)
        new CFile(f).copyFile(new File(trgFile, f.getName()));

      //Copy sub dirs to target dir (recursive).
      list = list(false);
      for (File f : list)
        new CFile(f).copy(new File(trgFile, f.getName()));
    }
  }


  /** Deletes this file or directory.
   *  @return TRUE methoods suceeds completely, FALSE otherwise.
   */
  public boolean delete() {
    if (file.isFile()) return file.delete();
    else {
      boolean result = true;

      //Delete all files.
      File[] list = list(true);
      for (File f : list)
        result = f.delete() && result;

      //Delete all sub dirs (recursive).
      list = list(false);
      for (File f : list)
        result = new CFile(f).delete() && result;

      //Delete the dir.
      result = file.delete() && result;

      return result;
    }
  }


  /** Returns file or dir size.
   *  @param sizeLimit Method stops when reaches this limit (useful for dirs with huge content).
   *  @return Total size of all files in bytes.
   */
  public long size(long sizeLimit) {
    if (file.isFile()) return file.length();
    else {
      long size = 0;

      //Iterate all files.
      File[] list = list(true);
      for (File f : list)
        size += f.length();

      //Iterate all sub dirs (recursive).
      list = list(false);
      for (File f : list)
        size += new CFile(f).size(sizeLimit);

      return size;
    }
  }


  public String toString() {
    return file.getPath();
  }


  /** Overwrite the file
   * @param rbc Input data.
   */
  public void rewriteFile(ReadableByteChannel rbc) throws IOException {
    truncateFile(0);
    insertFileData(rbc, 0);
  }



  /** Convenience method
   * @see com.coldcore.misc5.CFile#rewriteFile(java.nio.channels.ReadableByteChannel)
   */
  public void rewriteFile(InputStream in) throws IOException {
    rewriteFile(Channels.newChannel(in));
  }



  /** Convenience method
   * @see com.coldcore.misc5.CFile#rewriteFile(java.nio.channels.ReadableByteChannel)
   */
  public void rewriteFile(byte[] b) throws IOException {
    rewriteFile(new ByteArrayInputStream(b));
  }
}