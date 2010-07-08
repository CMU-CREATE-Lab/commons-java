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
   private final Integer commandId;

   public HIDWriteStatus(final int numBytesRequestedToWrite, final int numBytesActuallyWritten, final boolean wasSuccessful, final Integer commandId)
      {
      this.numBytesRequestedToWrite = numBytesRequestedToWrite;
      this.numBytesActuallyWritten = numBytesActuallyWritten;
      this.wasSuccessful = wasSuccessful;
      this.commandId = commandId;
      }

   public int getNumBytesRequestedToWrite()
      {
      return numBytesRequestedToWrite;
      }

   public int getNumBytesActuallyWritten()
      {
      return numBytesActuallyWritten;
      }

   public boolean wasSuccessful()
      {
      return wasSuccessful;
      }

   public Integer getCommandId()
      {
      return commandId;
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
      if (commandId != null ? !commandId.equals(that.commandId) : that.commandId != null)
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
      result = 31 * result + (commandId != null ? commandId.hashCode() : 0);
      return result;
      }

   @Override
   public String toString()
      {
      final StringBuilder sb = new StringBuilder();
      sb.append("HIDWriteStatus");
      sb.append("{numBytesRequestedToWrite=").append(numBytesRequestedToWrite);
      sb.append(", numBytesActuallyWritten=").append(numBytesActuallyWritten);
      sb.append(", wasSuccessful=").append(wasSuccessful);
      sb.append(", commandId=").append(commandId);
      sb.append('}');
      return sb.toString();
      }
   }
