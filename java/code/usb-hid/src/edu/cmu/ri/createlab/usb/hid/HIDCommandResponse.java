package edu.cmu.ri.createlab.usb.hid;

import java.util.Arrays;
import edu.cmu.ri.createlab.util.commandexecution.CommandResponse;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class HIDCommandResponse implements CommandResponse
   {
   private static final String EOL = System.getProperty("line.separator", "\n");

   private final boolean writeSuccess;
   private final boolean readSuccess;
   private final byte[] data;

   /**
    * Creates a new HIDCommandResponse having the given <code>writeSuccess</code>, <code>readSuccess</code> and data.
    * This constructor creates a copy of the given byte array (if it's non-null), so the caller is free to mutate the
    * array after calling this constructor without fear of mutating this object instance.
    */
   public HIDCommandResponse(final boolean writeSuccess, final boolean readSuccess, final byte[] data)
      {
      this.writeSuccess = writeSuccess;
      this.readSuccess = readSuccess;
      this.data = (data == null) ? null : data.clone();
      }

   /**
    * Returns <code>true</code> if the write command and subsequent read were both successful; <code>false</code>
    * otherwise.
    */
   @Override
   public boolean wasSuccessful()
      {
      return writeSuccess && readSuccess;
      }

   /** Returns <code>true</code> if the write was successful; <code>false</code> otherwise. */
   public boolean wasWriteSuccessful()
      {
      return writeSuccess;
      }

   /** Returns <code>true</code> if the read was successful; <code>false</code> otherwise. */
   public boolean wasReadSuccessful()
      {
      return readSuccess;
      }

   /** Returns a copy of the data as an array of bytes.  May return null. */
   @Override
   public byte[] getData()
      {
      return (data == null) ? null : data.clone();
      }

   @Override
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

      final HIDCommandResponse that = (HIDCommandResponse)o;

      if (readSuccess != that.readSuccess)
         {
         return false;
         }
      if (writeSuccess != that.writeSuccess)
         {
         return false;
         }
      if (!Arrays.equals(data, that.data))
         {
         return false;
         }

      return true;
      }

   @Override
   public int hashCode()
      {
      int result = (writeSuccess ? 1 : 0);
      result = 31 * result + (readSuccess ? 1 : 0);
      result = 31 * result + (data != null ? Arrays.hashCode(data) : 0);
      return result;
      }

   public String toString()
      {
      final String dataSize = data == null ? "null" : data.length + " byte(s)";
      final StringBuilder s = new StringBuilder("HIDCommandResponse{" + EOL);
      s.append("   writeSuccess: ").append(writeSuccess).append(EOL);
      s.append("   readSuccess: ").append(readSuccess).append(EOL);
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
