package edu.cmu.ri.createlab.serial;

import edu.cmu.ri.createlab.util.ByteUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public abstract class CreateLabSerialDeviceReturnValueCommandStrategy extends CreateLabSerialDeviceCommandStrategy implements SerialPortCommandStrategy
   {
   private static final Log LOG = LogFactory.getLog(CreateLabSerialDeviceReturnValueCommandStrategy.class);

   /**
    * Creates a <code>CreateLabSerialDeviceReturnValueCommandStrategy</code> using the default values for read timeout,
    * slurp timeout, and max retries.
    *
    * @see CreateLabSerialDeviceCommandStrategy#CreateLabSerialDeviceCommandStrategy()
    * @see CreateLabSerialDeviceCommandStrategy#DEFAULT_READ_TIMEOUT_MILLIS
    * @see CreateLabSerialDeviceCommandStrategy#DEFAULT_SLURP_TIMEOUT_MILLIS
    * @see CreateLabSerialDeviceCommandStrategy#DEFAULT_MAX_NUMBER_OF_RETRIES
    */
   protected CreateLabSerialDeviceReturnValueCommandStrategy()
      {
      super();
      }

   /**
    * Creates a <code>CreateLabSerialDeviceReturnValueCommandStrategy</code> using the given values for read timeout,
    * slurp timeout, and max retries.
    *
    * @see CreateLabSerialDeviceCommandStrategy#CreateLabSerialDeviceCommandStrategy(int, int, int)
    * @see CreateLabSerialDeviceCommandStrategy#DEFAULT_READ_TIMEOUT_MILLIS
    * @see CreateLabSerialDeviceCommandStrategy#DEFAULT_SLURP_TIMEOUT_MILLIS
    * @see CreateLabSerialDeviceCommandStrategy#DEFAULT_MAX_NUMBER_OF_RETRIES
    */
   protected CreateLabSerialDeviceReturnValueCommandStrategy(final int readTimeoutMillis, final int slurpTimeoutMillis, final int maxNumberOfRetries)
      {
      super(readTimeoutMillis, slurpTimeoutMillis, maxNumberOfRetries);
      }

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