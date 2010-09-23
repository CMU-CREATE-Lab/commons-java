package edu.cmu.ri.createlab.serial;

import java.util.Arrays;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class SerialPortCommandResponse
   {
   private static final String EOL = System.getProperty("line.separator", "\n");

   private final boolean success;
   private final byte[] data;

   /** Creates a new SerialPortCommandResponse having the given success and <code>null</code> data. */
   public SerialPortCommandResponse(final boolean success)
   {
   this(success, null);
   }

   /**
    * Creates a new SerialPortCommandResponse having the given success and data.  This constructor creates a copy of the
    * given byte array (if it's non-null), so the caller is free to mutate the array after calling this constructor
    * without fear of mutating this object instance.
    */
   public SerialPortCommandResponse(final boolean success, final byte[] data)
   {
   this.success = success;
   this.data = (data == null) ? null : data.clone();
   }

   /**
    * Creates a new SerialPortCommandResponse using the given data.  The status code is <code>true</code> if the given
    * data array is non-null; <code>false</code> otherwise.  This constructor creates a copy of the given byte array
    * (if it's non-null), so the caller is free to mutate the array after calling this constructor without fear of
    * mutating this object instance.
    */
   public SerialPortCommandResponse(final byte[] data)
   {
   this(data != null, data);
   }

   /**
    * Returns <code>true</code> if the command was successful; <code>false</code> otherwise.  It is up to each command
    * to define what success means.
    */
   public boolean wasSuccessful()
   {
   return success;
   }

   /** Returns a copy of the data as an array of bytes.  May return null. */
   public byte[] getData()
   {
   return (data == null) ? null : data.clone();
   }

   public boolean equals(final Object o)
      {
      if (this == o)
         {
         return true;
         }
      if (o == null || getClass() != o.getClass())
         {
         return false;
         }

      final SerialPortCommandResponse that = (SerialPortCommandResponse)o;

      if (success != that.success)
         {
         return false;
         }
      if (!Arrays.equals(data, that.data))
         {
         return false;
         }

      return true;
      }

   public int hashCode()
      {
      int result;
      result = (success ? 1 : 0);
      result = 31 * result + (data != null ? Arrays.hashCode(data) : 0);
      return result;
      }

   public String toString()
      {
      final String dataSize = data == null ? "null" : data.length + " byte(s)";
      final StringBuffer s = new StringBuffer("SerialPortCommandResponse{" + EOL);
      s.append("   success: ").append(success).append(EOL);
      s.append("   data:    ").append(dataSize).append(EOL);
      if ((data != null) && (data.length > 0))
         {
         for (final byte b : data)
            {
            final int theByte = b & 0xff;
            s.append("      ").append(theByte).append(EOL);
            }
         }
      s.append("}").append(EOL);

      return s.toString();
      }
   }