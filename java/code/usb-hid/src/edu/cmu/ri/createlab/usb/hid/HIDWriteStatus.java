package edu.cmu.ri.createlab.usb.hid;

/**
 * <p>
 * <code>HIDWriteStatus</code> contains information about a completed HID write command.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class HIDWriteStatus
   {
   public static final HIDWriteStatus WRITE_FAILED = new HIDWriteStatus(0, 0, false, null);

   private final int numBytesRequestedToWrite;
   private final int numBytesActuallyWritten;
   private final boolean wasSuccessful;
   private final Integer commandID;

   public HIDWriteStatus(final int numBytesRequestedToWrite, final int numBytesActuallyWritten, final boolean wasSuccessful)
      {
      this(numBytesRequestedToWrite, numBytesActuallyWritten, wasSuccessful, null);
      }

   public HIDWriteStatus(final int numBytesRequestedToWrite, final int numBytesActuallyWritten, final boolean wasSuccessful, final Integer commandID)
      {
      this.numBytesRequestedToWrite = numBytesRequestedToWrite;
      this.numBytesActuallyWritten = numBytesActuallyWritten;
      this.wasSuccessful = wasSuccessful;
      this.commandID = commandID;
      }

   public int getNumBytesRequestedToWrite()
      {
      return numBytesRequestedToWrite;
      }

   public int getNumBytesActuallyWritten()
      {
      return numBytesActuallyWritten;
      }

   public boolean isWasSuccessful()
      {
      return wasSuccessful;
      }

   public Integer getCommandID()
      {
      return commandID;
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

      final HIDWriteStatus that = (HIDWriteStatus)o;

      if (numBytesActuallyWritten != that.numBytesActuallyWritten)
         {
         return false;
         }
      if (numBytesRequestedToWrite != that.numBytesRequestedToWrite)
         {
         return false;
         }
      if (wasSuccessful != that.wasSuccessful)
         {
         return false;
         }
      if (commandID != null ? !commandID.equals(that.commandID) : that.commandID != null)
         {
         return false;
         }

      return true;
      }

   @Override
   public int hashCode()
      {
      int result = numBytesRequestedToWrite;
      result = 31 * result + numBytesActuallyWritten;
      result = 31 * result + (wasSuccessful ? 1 : 0);
      result = 31 * result + (commandID != null ? commandID.hashCode() : 0);
      return result;
      }
   }
