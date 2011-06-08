package edu.cmu.ri.createlab.serial;

import java.util.concurrent.TimeUnit;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public abstract class CreateLabSerialDeviceNoReturnValueCommandStrategy extends CreateLabSerialDeviceCommandStrategy
   {
   /**
    * Creates a <code>CreateLabSerialDeviceNoReturnValueCommandStrategy</code> using the default values for read timeout, slurp
    * timeout, and max retries.
    *
    * @see #DEFAULT_READ_TIMEOUT_MILLIS
    * @see #DEFAULT_SLURP_TIMEOUT_MILLIS
    * @see #DEFAULT_MAX_NUMBER_OF_RETRIES
    */
   protected CreateLabSerialDeviceNoReturnValueCommandStrategy()
      {
      }

   /**
    * Creates a <code>CreateLabSerialDeviceNoReturnValueCommandStrategy</code> using the given value for read timeout and the default
    * values for slurp timeout and max retries.
    *
    * @see #DEFAULT_SLURP_TIMEOUT_MILLIS
    * @see #DEFAULT_MAX_NUMBER_OF_RETRIES
    *
    * @deprecated Use {@link #CreateLabSerialDeviceCommandStrategy(long, TimeUnit)} instead
    */
   protected CreateLabSerialDeviceNoReturnValueCommandStrategy(final int readTimeoutMillis)
      {
      super(readTimeoutMillis);
      }

   /**
    * Creates a <code>CreateLabSerialDeviceNoReturnValueCommandStrategy</code> using the given values for read timeout, slurp
    * timeout, and max retries.
    *
    * @deprecated Use {@link #CreateLabSerialDeviceCommandStrategy(long, TimeUnit, long, TimeUnit, int)} instead
    */
   protected CreateLabSerialDeviceNoReturnValueCommandStrategy(final int readTimeoutMillis, final int slurpTimeoutMillis, final int maxNumberOfRetries)
      {
      super(readTimeoutMillis, slurpTimeoutMillis, maxNumberOfRetries);
      }

   /**
    * Creates a <code>CreateLabSerialDeviceNoReturnValueCommandStrategy</code> using the given value for read timeout and the default
    * values for slurp timeout and max retries.
    *
    * @see #DEFAULT_SLURP_TIMEOUT_MILLIS
    * @see #DEFAULT_MAX_NUMBER_OF_RETRIES
    */
   protected CreateLabSerialDeviceNoReturnValueCommandStrategy(final long readTimeout, final TimeUnit readTimeoutTimeUnit)
      {
      super(readTimeout, readTimeoutTimeUnit);
      }

   /**
    * Creates a <code>CreateLabSerialDeviceNoReturnValueCommandStrategy</code> using the given values for read timeout and slurp
    * timeout and the default value for max retries.
    *
    * @see #DEFAULT_MAX_NUMBER_OF_RETRIES
    */
   protected CreateLabSerialDeviceNoReturnValueCommandStrategy(final long readTimeout, final TimeUnit readTimeoutTimeUnit, final long slurpTimeout, final TimeUnit slurpTimeoutTimeUnit)
      {
      super(readTimeout, readTimeoutTimeUnit, slurpTimeout, slurpTimeoutTimeUnit);
      }

   /**
    * Creates a <code>CreateLabSerialDeviceNoReturnValueCommandStrategy</code> using the given values for read timeout, slurp
    * timeout, and max retries.
    */
   protected CreateLabSerialDeviceNoReturnValueCommandStrategy(final long readTimeout, final TimeUnit readTimeoutTimeUnit, final long slurpTimeout, final TimeUnit slurpTimeoutTimeUnit, final int maxNumberOfRetries)
      {
      super(readTimeout, readTimeoutTimeUnit, slurpTimeout, slurpTimeoutTimeUnit, maxNumberOfRetries);
      }

   @Override
   public final SerialDeviceCommandResponse execute(final SerialDeviceIOHelper ioHelper)
      {
      // get the command to be written
      final byte[] command = getCommand();

      // write the command and check for the command echo
      final boolean wasSuccessful = writeCommand(ioHelper, command);

      // return the response
      return new SerialDeviceCommandResponse(wasSuccessful);
      }

   protected abstract byte[] getCommand();
   }
