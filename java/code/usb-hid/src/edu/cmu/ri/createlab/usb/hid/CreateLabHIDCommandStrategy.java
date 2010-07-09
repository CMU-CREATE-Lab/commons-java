package edu.cmu.ri.createlab.usb.hid;

import edu.cmu.ri.createlab.util.ByteUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <code>CreateLabHIDCommandStrategy</code> is an abstract {@link HIDCommandStrategy} used for talking with CREATE Lab
 * HID devices.
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public abstract class CreateLabHIDCommandStrategy implements HIDCommandStrategy
   {
   private static final Log LOG = LogFactory.getLog(CreateLabHIDCommandStrategy.class);
   private static final int TIMEOUT_IN_NANOSECONDS = 1000000000;

   public final HIDCommandResult execute(final HIDDevice hidDevice) throws HIDDeviceNotConnectedException, HIDDeviceFailureException
      {
      LOG.trace("CreateLabHIDCommandStrategy.execute()");

      // get the command to be written
      final byte[] command = getCommand();

      // write the command
      final HIDWriteStatus writeStatus = hidDevice.write(command);

      if (!writeStatus.wasSuccessful())
         {
         if (LOG.isErrorEnabled())
            {
            LOG.error("CreateLabHIDCommandStrategy.execute(): Number of bytes written [" + writeStatus.getNumBytesActuallyWritten() + "] does not match number of bytes in the command [" + writeStatus.getNumBytesRequestedToWrite() + "].  Command ID = [" + writeStatus.getCommandId() + "]");
            }
         }

      // don't bother to try to read unless the command ID is non-null (meaning we actually tried a write)
      boolean readWasSuccessful = false;
      byte[] dataRead = null;
      if (writeStatus.getCommandId() == null)
         {
         LOG.error("CreateLabHIDCommandStrategy.execute(): command ID returned by the write command was null, so we won't attempt to read");
         }
      else
         {
         // now read until we get the response with the matching command ID, or until we timeout
         final long startTime = System.nanoTime();
         final long endTime = startTime + TIMEOUT_IN_NANOSECONDS;
         int numReads = 0;

         do
            {
            final byte[] data = hidDevice.read();
            numReads++;

            // check the data (the length must be at least two since the first byte is the report ID and the last is the command ID)
            if (data == null || data.length < 2)
               {
               LOG.error("CreateLabHIDCommandStrategy.execute(): data read is null or empty, ignoring read");
               continue;
               }

            // the last element of the read data is the command ID
            final int returnedCommandId = ByteUtils.unsignedByteToInt(data[data.length - 1]);

            // see if we found our desired command ID
            if (writeStatus.getCommandId() == returnedCommandId)
               {
               // we found our response!
               readWasSuccessful = true;

               // the returned data array from an HID device is a fixed size, but a command will probably only care about
               // a subset of the bytes.  Do a copy of the bytes we care about, being careful about AIOOBEs.
               final int numBytesToCopy = Math.min(getSizeOfExpectedResponse(), data.length - 2);
               if (LOG.isWarnEnabled())
                  {
                  if (numBytesToCopy != getSizeOfExpectedResponse())
                     {
                     LOG.warn("CreateLabHIDCommandStrategy.execute(): size of expected response [" + getSizeOfExpectedResponse() + "] does not match num bytes we're actually allowed to copy [" + numBytesToCopy + "]");
                     }
                  }
               dataRead = new byte[numBytesToCopy];
               System.arraycopy(data, 1, dataRead, 0, numBytesToCopy);

               if (LOG.isTraceEnabled())
                  {
                  final long currentTime = System.nanoTime();
                  final double elapsedTimeInMillis = (double)(currentTime - startTime) / 1000000.0;
                  LOG.trace("CreateLabHIDCommandStrategy.execute(): read successful -- it took [" + numReads + "] reads and [" + elapsedTimeInMillis + "] ms to find the response to the write command.");
                  }
               }
            else
               {
               if (LOG.isErrorEnabled())
                  {
                  LOG.error("CreateLabHIDCommandStrategy.execute(): unexpected command ID in the data read.  Found [" + returnedCommandId + "], was expecting [" + writeStatus.getCommandId() + "]");
                  }
               }
            }
         while (!readWasSuccessful && System.nanoTime() < endTime);
         }

      return new HIDCommandResult(writeStatus.wasSuccessful(), readWasSuccessful, dataRead);
      }

   protected abstract byte[] getCommand();

   /** Returns the number of bytes of the expected response. */
   protected abstract int getSizeOfExpectedResponse();
   }
