package edu.cmu.ri.createlab.serial;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public abstract class CreateLabSerialDeviceHandshakeCommandStrategy extends CreateLabSerialDeviceCommandStrategy
   {
   private static final Logger LOG = Logger.getLogger(CreateLabSerialDeviceHandshakeCommandStrategy.class);

   /**
    * Creates a <code>CreateLabSerialDeviceHandshakeCommandStrategy</code> using the default values for read timeout, slurp
    * timeout, and max retries.
    *
    * @see #DEFAULT_READ_TIMEOUT_MILLIS
    * @see #DEFAULT_SLURP_TIMEOUT_MILLIS
    * @see #DEFAULT_MAX_NUMBER_OF_RETRIES
    */
   protected CreateLabSerialDeviceHandshakeCommandStrategy()
      {
      }

   /**
    * Creates a <code>CreateLabSerialDeviceHandshakeCommandStrategy</code> using the given value for read timeout and the default
    * values for slurp timeout and max retries.
    *
    * @see #DEFAULT_SLURP_TIMEOUT_MILLIS
    * @see #DEFAULT_MAX_NUMBER_OF_RETRIES
    *
    * @deprecated Use {@link #CreateLabSerialDeviceCommandStrategy(long, TimeUnit)} instead
    */
   protected CreateLabSerialDeviceHandshakeCommandStrategy(final int readTimeoutMillis)
      {
      super(readTimeoutMillis);
      }

   /**
    * Creates a <code>CreateLabSerialDeviceHandshakeCommandStrategy</code> using the given values for read timeout, slurp
    * timeout, and max retries.
    *
    * @deprecated Use {@link #CreateLabSerialDeviceCommandStrategy(long, TimeUnit, long, TimeUnit, int)} instead
    */
   protected CreateLabSerialDeviceHandshakeCommandStrategy(final int readTimeoutMillis, final int slurpTimeoutMillis, final int maxNumberOfRetries)
      {
      super(readTimeoutMillis, slurpTimeoutMillis, maxNumberOfRetries);
      }

   /**
    * Creates a <code>CreateLabSerialDeviceHandshakeCommandStrategy</code> using the given value for read timeout and the default
    * values for slurp timeout and max retries.
    *
    * @see #DEFAULT_SLURP_TIMEOUT_MILLIS
    * @see #DEFAULT_MAX_NUMBER_OF_RETRIES
    */
   protected CreateLabSerialDeviceHandshakeCommandStrategy(final long readTimeout, final TimeUnit readTimeoutTimeUnit)
      {
      super(readTimeout, readTimeoutTimeUnit);
      }

   /**
    * Creates a <code>CreateLabSerialDeviceHandshakeCommandStrategy</code> using the given values for read timeout and slurp
    * timeout and the default value for max retries.
    *
    * @see #DEFAULT_MAX_NUMBER_OF_RETRIES
    */
   protected CreateLabSerialDeviceHandshakeCommandStrategy(final long readTimeout, final TimeUnit readTimeoutTimeUnit, final long slurpTimeout, final TimeUnit slurpTimeoutTimeUnit)
      {
      super(readTimeout, readTimeoutTimeUnit, slurpTimeout, slurpTimeoutTimeUnit);
      }

   /**
    * Creates a <code>CreateLabSerialDeviceHandshakeCommandStrategy</code> using the given values for read timeout, slurp
    * timeout, and max retries.
    */
   protected CreateLabSerialDeviceHandshakeCommandStrategy(final long readTimeout, final TimeUnit readTimeoutTimeUnit, final long slurpTimeout, final TimeUnit slurpTimeoutTimeUnit, final int maxNumberOfRetries)
      {
      super(readTimeout, readTimeoutTimeUnit, slurpTimeout, slurpTimeoutTimeUnit, maxNumberOfRetries);
      }

   @Override
   public final SerialDeviceCommandResponse execute(final SerialDeviceIOHelper ioHelper)
      {
      LOG.debug("CreateLabSerialDeviceHandshakeCommandStrategy.execute()");

      boolean wasHandshakeSuccessful = false;

      LOG.debug("CreateLabSerialDeviceHandshakeCommandStrategy.execute(): Listening for the startup mode song...");
      final boolean foundStartupSong = slurpAndMatchPattern(ioHelper, getStartupModeCharacters());

      // if we successfully detected the startup song, then write the response and check for its echo
      if (foundStartupSong)
         {
         // write the response pattern
         LOG.debug("CreateLabSerialDeviceHandshakeCommandStrategy.execute(): Writing receive mode command...");

         // write the characters in the receive mode response
         try
            {
            final byte[] receiveModeCharacters = getReceiveModeCharacters();

            ioHelper.write(receiveModeCharacters);

            LOG.debug("CreateLabSerialDeviceHandshakeCommandStrategy.execute(): Listening for receive mode echo...");

            // check for the response pattern echo
            wasHandshakeSuccessful = slurpAndMatchPattern(ioHelper, receiveModeCharacters);
            }
         catch (IOException e)
            {
            LOG.error("CreateLabSerialDeviceHandshakeCommandStrategy.execute(): IOException while trying to write the characters to put the serial device into receive mode", e);
            }
         }

      if (LOG.isDebugEnabled())
         {
         LOG.debug("CreateLabSerialDeviceHandshakeCommandStrategy.execute(): success = [" + wasHandshakeSuccessful + "]");
         }

      // if the handshake succeeded, then we need to sleep for at least 500 milliseconds to allow for the
      // serial device to break out of its song loop.  The device has a 500 millisecond wait between chirps, so
      // if we were to return immediately here without sleeping, and then immediately send a command, some or all
      // of that command could get swallowed and lost.
      if (wasHandshakeSuccessful)
         {
         try
            {
            LOG.debug("CreateLabSerialDeviceHandshakeCommandStrategy.execute(): Sleeping to allow for the device to break out of song mode.");
            Thread.sleep(600);
            }
         catch (InterruptedException e)
            {
            LOG.error("InterruptedException while sleeping", e);
            }
         }
      return new SerialDeviceCommandResponse(wasHandshakeSuccessful);
      }

   protected abstract byte[] getReceiveModeCharacters();

   protected abstract byte[] getStartupModeCharacters();
   }