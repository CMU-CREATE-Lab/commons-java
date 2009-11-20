package edu.cmu.ri.createlab.serial;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface SerialPortIOHelper
   {
   /**
    * Returns the number of bytes that can be read (or skipped over) from this input stream without blocking by the next
    * caller of a method for this input stream.
    *
    * @see InputStream#available()
    */
   int available() throws IOException;

   /**
    * Convenience method that returns <code>true</code> if a call to {@link #available()} would return a positive value;
    * <code>false</code> otherwise.
    */
   boolean isDataAvailable() throws IOException;

   /**
    * Reads the next byte of data from the input stream. The value byte is returned as an <code>int</code> in the range
    * <code>0</code> to <code>255</code>. If no byte is available because the end of the stream has been reached, the
    * value <code>-1</code> is returned. This method blocks until input data is available, the end of the stream is
    * detected, or an exception is thrown.
    *
    * @see InputStream#read()
    */
   int read() throws IOException;

   /**
    * Reads some number of bytes from the input stream and stores them into the buffer array <code>b</code>. The number
    * of bytes actually read is returned as an integer.  This method blocks until input data is available, end of file
    * is detected, or an exception is thrown.
    *
    * <p> If <code>buffer</code> is <code>null</code>, a <code>NullPointerException</code> is thrown.  If the length of
    * <code>buffer</code> is zero, then no bytes are read and <code>0</code> is returned; otherwise, there is an attempt
    * to read at least one byte. If no byte is available because the stream is at end of file, the value <code>-1</code>
    * is returned; otherwise, at least one byte is read and stored into <code>buffer</code>.
    *
    * @see InputStream#read(byte[])
    */
   int read(final byte[] buffer) throws IOException;

   /**
    * Writes <code>data.length</code> bytes from the specified byte array to this output stream and then flushes it.
    *
    * @see OutputStream#write(byte[])
    * @see OutputStream#flush()
    */
   void write(final byte[] data) throws IOException;
   }