package edu.cmu.ri.createlab.serial;

import java.util.concurrent.TimeUnit;
import edu.cmu.ri.createlab.util.ByteUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public abstract class CreateLabSerialDeviceReturnValueCommandStrategy<DesiredClass> extends CreateLabSerialDeviceCommandStrategy implements SerialDeviceReturnValueCommandStrategy<DesiredClass>
   {
   private static final Logger LOG = Logger.getLogger(CreateLabSerialDeviceReturnValueCommandStrategy.class);

   /**
    * Creates a <code>CreateLabSerialDeviceReturnValueCommandStrategy</code> using the default values for read timeout, slurp
    * timeout, and max retries.
    *
    * @see #DEFAULT_READ_TIMEOUT_MILLIS
    * @see #DEFAULT_SLURP_TIMEOUT_MILLIS
    * @see #DEFAULT_MAX_NUMBER_OF_RETRIES
    */
   protected CreateLabSerialDeviceReturnValueCommandStrategy()
      {
      }

   /**
    * Creates a <code>CreateLabSerialDeviceReturnValueCommandStrategy</code> using the given value for read timeout and the default
    * values for slurp timeout and max retries.
    *
    * @see #DEFAULT_SLURP_TIMEOUT_MILLIS
    * @see #DEFAULT_MAX_NUMBER_OF_RETRIES
    *
    * @deprecated Use {@link #CreateLabSerialDeviceCommandStrategy(long, TimeUnit)} instead
    */
   protected CreateLabSerialDeviceReturnValueCommandStrategy(final int readTimeoutMillis)
      {
      super(readTimeoutMillis);
      }

   /**
    * Creates a <code>CreateLabSerialDeviceReturnValueCommandStrategy</code> using the given values for read timeout, slurp
    * timeout, and max retries.
    *
    * @deprecated Use {@link #CreateLabSerialDeviceCommandStrategy(long, TimeUnit, long, TimeUnit, int)} instead
    */
   protected CreateLabSerialDeviceReturnValueCommandStrategy(final int readTimeoutMillis, final int slurpTimeoutMillis, final int maxNumberOfRetries)
      {
      super(readTimeoutMillis, slurpTimeoutMillis, maxNumberOfRetries);
      }

   /**
    * Creates a <code>CreateLabSerialDeviceReturnValueCommandStrategy</code> using the given value for read timeout and the default
    * values for slurp timeout and max retries.
    *
    * @see #DEFAULT_SLURP_TIMEOUT_MILLIS
    * @see #DEFAULT_MAX_NUMBER_OF_RETRIES
    */
   protected CreateLabSerialDeviceReturnValueCommandStrategy(final long readTimeout, final TimeUnit readTimeoutTimeUnit)
      {
      super(readTimeout, readTimeoutTimeUnit);
      }

   /**
    * Creates a <code>CreateLabSerialDeviceReturnValueCommandStrategy</code> using the given values for read timeout and slurp
    * timeout and the default value for max retries.
    *
    * @see #DEFAULT_MAX_NUMBER_OF_RETRIES
    */
   protected CreateLabSerialDeviceReturnValueCommandStrategy(final long readTimeout, final TimeUnit readTimeoutTimeUnit, final long slurpTimeout, final TimeUnit slurpTimeoutTimeUnit)
      {
      super(readTimeout, readTimeoutTimeUnit, slurpTimeout, slurpTimeoutTimeUnit);
      }

   /**
    * Creates a <code>CreateLabSerialDeviceReturnValueCommandStrategy</code> using the given values for read timeout, slurp
    * timeout, and max retries.
    */
   protected CreateLabSerialDeviceReturnValueCommandStrategy(final long readTimeout, final TimeUnit readTimeoutTimeUnit, final long slurpTimeout, final TimeUnit slurpTimeoutTimeUnit, final int maxNumberOfRetries)
      {
      super(readTimeout, readTimeoutTimeUnit, slurpTimeout, slurpTimeoutTimeUnit, maxNumberOfRetries);
      }

   @Override
   public final SerialDeviceCommandResponse execute(final SerialDeviceIOHelper ioHelper)
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

         final SerialDeviceCommandResponse response = read(ioHelper, numBytesExpected);

         if (response != null && response.wasSuccessful())
            {
            // return the data
            return response;
            }
         else
            {
            if (LOG.isEnabledFor(Level.ERROR))
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

      return new SerialDeviceCommandResponse(false);
      }

   /** Returns the number of bytes of the expected response. */
   protected abstract int getSizeOfExpectedResponse();

   /** The command to be written, including any arguments. */
   protected abstract byte[] getCommand();
   }