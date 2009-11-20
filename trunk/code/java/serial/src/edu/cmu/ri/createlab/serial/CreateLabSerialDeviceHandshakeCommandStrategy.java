package edu.cmu.ri.createlab.serial;

import java.io.IOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public abstract class CreateLabSerialDeviceHandshakeCommandStrategy extends CreateLabSerialDeviceCommandStrategy implements SerialPortCommandStrategy
   {
   private static final Log LOG = LogFactory.getLog(CreateLabSerialDeviceHandshakeCommandStrategy.class);

   public final SerialPortCommandResponse execute(final SerialPortIOHelper ioHelper)
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
      return new SerialPortCommandResponse(wasHandshakeSuccessful);
      }

   protected abstract byte[] getReceiveModeCharacters();

   protected abstract byte[] getStartupModeCharacters();
   }