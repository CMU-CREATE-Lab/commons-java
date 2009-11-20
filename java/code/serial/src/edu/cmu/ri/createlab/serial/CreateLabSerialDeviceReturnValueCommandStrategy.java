package edu.cmu.ri.createlab.serial;

import edu.cmu.ri.createlab.util.ByteUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public abstract class CreateLabSerialDeviceReturnValueCommandStrategy extends CreateLabSerialDeviceCommandStrategy implements SerialPortCommandStrategy
   {
   private static final Log LOG = LogFactory.getLog(CreateLabSerialDeviceNoReturnValueCommandStrategy.class);

   public final SerialPortCommandResponse execute(final SerialPortIOHelper ioHelper)
      {
      LOG.trace("CreateLabSerialDeviceReturnValueCommandStrategy.execute()");

      // get the command to be written
      final byte[] command = getCommand();

      // write the command and check for the command echo
      final boolean wasWriteSuccessful = writeCommand(ioHelper, command);

      if (wasWriteSuccessful)
         {
         LOG.trace("CreateLabSerialDeviceReturnValueCommandStrategy.execute(): Reading command return value...");

         final int numBytesExpected = getSizeOfExpectedResponse();

         final SerialPortCommandResponse response = read(ioHelper, numBytesExpected);

         if (response != null && response.wasSuccessful())
            {
            // return the data
            return response;
            }
         else
            {
            if (LOG.isErrorEnabled())
               {
               final StringBuffer s = new StringBuffer("[");
               for (final byte b : command)
                  {
                  s.append("(").append((char)b).append("|").append(ByteUtils.unsignedByteToInt(b)).append(")");
                  }
               s.append("]");
               LOG.error("CreateLabSerialDeviceReturnValueCommandStrategy.execute(): Failed to read command return value for command " + s + ".");
               }
            }
         }

      return new SerialPortCommandResponse(false);
      }

   /** Returns the number of bytes of the expected response. */
   protected abstract int getSizeOfExpectedResponse();

   /** The command to be written, including any arguments. */
   protected abstract byte[] getCommand();
   }