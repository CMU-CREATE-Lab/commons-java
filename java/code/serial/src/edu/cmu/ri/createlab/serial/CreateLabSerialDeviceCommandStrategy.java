package edu.cmu.ri.createlab.serial;

import java.io.IOException;
import edu.cmu.ri.createlab.util.ByteUtils;
import edu.cmu.ri.createlab.util.commandexecution.CommandStrategy;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * <p>
 * <code>CreateLabSerialDeviceCommandStrategy</code> provides common functionality for all command strategies for CMU
 * CREATE Lab serial devices.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public abstract class CreateLabSerialDeviceCommandStrategy implements CommandStrategy<SerialDeviceIOHelper, SerialDeviceCommandResponse>
   {
   private static final Logger LOG = Logger.getLogger(CreateLabSerialDeviceCommandStrategy.class);

   /** Default maximum number of milliseconds to wait for data while reading from the serial port */
   public static final int DEFAULT_READ_TIMEOUT_MILLIS = 1000;

   /** Default maximum number of milliseconds to wait while slurping data from the serial port */
   public static final int DEFAULT_SLURP_TIMEOUT_MILLIS = 5000;

   /** Default maximum number of retries when writing a command */
   public static final int DEFAULT_MAX_NUMBER_OF_RETRIES = 5;

   private final int readTimeoutMillis;
   private final int slurpTimeoutMillis;
   private final int maxNumberOfRetries;

   /**
    * Creates a <code>CreateLabSerialDeviceCommandStrategy</code> using the default values for read timeout, slurp
    * timeout, and max retries.
    *
    * @see #CreateLabSerialDeviceCommandStrategy(int, int, int)
    * @see #DEFAULT_READ_TIMEOUT_MILLIS
    * @see #DEFAULT_SLURP_TIMEOUT_MILLIS
    * @see #DEFAULT_MAX_NUMBER_OF_RETRIES
    */
   protected CreateLabSerialDeviceCommandStrategy()
      {
      this(DEFAULT_READ_TIMEOUT_MILLIS, DEFAULT_SLURP_TIMEOUT_MILLIS, DEFAULT_MAX_NUMBER_OF_RETRIES);
      }

   /**
    * Creates a <code>CreateLabSerialDeviceCommandStrategy</code> using the given value for read timeout and the default
    * values for slurp timeout and max retries
    *
    * @see #CreateLabSerialDeviceCommandStrategy(int, int, int)
    * @see #DEFAULT_READ_TIMEOUT_MILLIS
    * @see #DEFAULT_SLURP_TIMEOUT_MILLIS
    * @see #DEFAULT_MAX_NUMBER_OF_RETRIES
    */
   protected CreateLabSerialDeviceCommandStrategy(final int readTimeoutMillis)
      {
      this(readTimeoutMillis, DEFAULT_SLURP_TIMEOUT_MILLIS, DEFAULT_MAX_NUMBER_OF_RETRIES);
      }

   /**
    * Creates a <code>CreateLabSerialDeviceCommandStrategy</code> using the given values for read timeout, slurp
    * timeout, and max retries.
    *
    * @see #DEFAULT_READ_TIMEOUT_MILLIS
    * @see #DEFAULT_SLURP_TIMEOUT_MILLIS
    * @see #DEFAULT_MAX_NUMBER_OF_RETRIES
    */
   protected CreateLabSerialDeviceCommandStrategy(final int readTimeoutMillis, final int slurpTimeoutMillis, final int maxNumberOfRetries)
      {
      this.readTimeoutMillis = readTimeoutMillis;
      this.slurpTimeoutMillis = slurpTimeoutMillis;
      this.maxNumberOfRetries = maxNumberOfRetries;
      }

   /**
    * Tries to read <code>numBytesToRead</code> bytes from the serial port.  Returns <code>null</code> if an exception
    * occurred while reading.  If the read was successful, it returns an array of bytes having a length equal to the
    * number of bytes actually read, which is equal to or smaller than <code>numBytesToRead</code>, but is guaranteed to
    * not be greater.
    */
   protected final SerialDeviceCommandResponse read(final SerialDeviceIOHelper ioHelper, final int numBytesToRead)
      {
      LOG.trace("CreateLabSerialDeviceCommandStrategy.read()");

      if (numBytesToRead <= 0)
         {
         throw new IllegalArgumentException("The number of bytes to read must be positive.");
         }

      // create a buffer to read the data into
      final byte[] data = new byte[numBytesToRead];

      try
         {
         // define the ending time
         final long endTime = readTimeoutMillis + System.currentTimeMillis();

         int numBytesRead = 0;
         while ((numBytesRead < numBytesToRead) && (System.currentTimeMillis() <= endTime))
            {
            if (ioHelper.isDataAvailable())
               {
               try
                  {
                  final int c = ioHelper.read();

                  if (LOG.isTraceEnabled())
                     {
                     LOG.trace("CreateLabSerialDeviceCommandStrategy.read():    read [" + (char)c + "|" + c + "]");
                     }

                  if (c >= 0)
                     {
                     data[numBytesRead++] = (byte)c;
                     }
                  else
                     {
                     LOG.error("CreateLabSerialDeviceCommandStrategy.read(): End of stream reached while trying to read the data");
                     return null;
                     }
                  }
               catch (IOException e)
                  {
                  LOG.error("CreateLabSerialDeviceCommandStrategy.read(): IOException while trying to read the data", e);
                  return null;
                  }
               }
            }

         // Now compare the amount of data read with what the caller expected.  If it's less, then make a copy of the
         // array containing only the bytes actually read and return that, but still mark the success as false.  This
         // allows the caller to compare the number of bytes read with the number expected and act accordingly.
         if (numBytesRead < numBytesToRead)
            {
            final byte[] dataSubset = new byte[numBytesRead];
            System.arraycopy(data, 0, dataSubset, 0, numBytesRead);

            return new SerialDeviceCommandResponse(false, dataSubset);
            }

         return new SerialDeviceCommandResponse(data);
         }
      catch (IOException e)
         {
         LOG.error("CreateLabSerialDeviceCommandStrategy.read(): IOException while reading the data", e);
         }

      return null;
      }

   /**
    * Writes the given <code>command</code> to the serial port and then reads from it to verify that the device
    * correctly echoed the command.  Will read a most <code>command.length</code> bytes.  Aborts reading upon reading
    * the first non-matching byte. Returns <code>true</code> if the command was echoed correctly, <code>false</code>
    * otherwise.
    */
   protected final boolean writeCommand(final SerialDeviceIOHelper ioHelper, final byte[] command)
      {
      // initialize the retry count
      int numWrites = 0;

      boolean echoDetected;
      do
         {
         echoDetected = writeCommandWorkhorse(ioHelper, command);
         numWrites++;
         if (!echoDetected)
            {
            if (LOG.isEnabledFor(Level.WARN))
               {
               LOG.warn("CreateLabSerialDeviceCommandStrategy.writeCommand(): failed to write command, will" + (numWrites < maxNumberOfRetries ? " " : " not ") + "retry");
               }
            slurp(ioHelper);
            }
         }
      while (!echoDetected && numWrites < maxNumberOfRetries);

      return echoDetected;
      }

   private void slurp(final SerialDeviceIOHelper ioHelper)
      {
      final long endTime = slurpTimeoutMillis + System.currentTimeMillis();

      try
         {
         // read until we exhaust the available data, or until we run out of time
         while (ioHelper.isDataAvailable() && System.currentTimeMillis() <= endTime)
            {
            try
               {
               final int c = ioHelper.read();
               if (c >= 0)
                  {
                  if (LOG.isTraceEnabled())
                     {
                     LOG.trace("CreateLabSerialDeviceCommandStrategy.slurp():    read [" + (char)c + "|" + c + "]");
                     }
                  }
               else
                  {
                  // todo
                  LOG.error("CreateLabSerialDeviceCommandStrategy.slurp(): End of stream reached while slurping--THIS MAY BE GOOD!");
                  break;
                  }
               }
            catch (IOException e)
               {
               LOG.error("CreateLabSerialDeviceCommandStrategy.slurp(): IOException while trying to slurp", e);
               break;
               }
            }
         }
      catch (IOException e)
         {
         LOG.error("CreateLabSerialDeviceCommandStrategy.slurp(): IOException while trying to slurp", e);
         }
      }

   private boolean writeCommandWorkhorse(final SerialDeviceIOHelper ioHelper, final byte[] command)
      {
      try
         {
         if (LOG.isTraceEnabled())
            {
            final StringBuffer s = new StringBuffer("[");
            for (final byte b : command)
               {
               s.append("(").append((char)b).append("|").append(ByteUtils.unsignedByteToInt(b)).append(")");
               }
            s.append("]");
            LOG.trace("CreateLabSerialDeviceCommandStrategy.writeCommandWorkhorse(): Writing the command [" + s + "]...");
            }

         ioHelper.write(command);

         LOG.trace("CreateLabSerialDeviceCommandStrategy.writeCommandWorkhorse(): Listening for command echo...");

         // initialize the counter for reading from the command
         int pos = 0;

         // initialize the flag which tracks whether the command was correctly echoed
         boolean isMatch = true;

         // define the ending time
         final long endTime = readTimeoutMillis + System.currentTimeMillis();
         while ((pos < command.length) && (System.currentTimeMillis() <= endTime))
            {
            if (ioHelper.isDataAvailable())
               {
               final byte expected = command[pos];
               final int actual = ioHelper.read();
               pos++;// increment the read counter

               if (LOG.isTraceEnabled())
                  {
                  LOG.trace("CreateLabSerialDeviceCommandStrategy.writeCommandWorkhorse():    read [" + (char)actual + "|" + actual + "]");
                  }

               // see if we reached the end of the stream
               if (actual >= 0)
                  {
                  final byte actualAsByte = (byte)actual;
                  // make sure this character in the command matches; break if not
                  if (expected != actualAsByte)
                     {
                     if (LOG.isEnabledFor(Level.WARN))
                        {
                        LOG.warn("CreateLabSerialDeviceCommandStrategy.writeCommandWorkhorse(): Mismatch detected: expected [" + ByteUtils.unsignedByteToInt(expected) + "], but read [" + ByteUtils.unsignedByteToInt(actualAsByte) + "]");
                        }
                     isMatch = false;
                     break;
                     }
                  }
               else
                  {
                  LOG.error("CreateLabSerialDeviceCommandStrategy.writeCommandWorkhorse(): End of stream reached while trying to read the command");
                  break;
                  }
               }
            }

         final boolean echoDetected = (pos == command.length) && isMatch;
         if (LOG.isTraceEnabled())
            {
            LOG.trace("CreateLabSerialDeviceCommandStrategy.writeCommandWorkhorse(): Command echo detected = " + echoDetected + " (isMatch=[" + isMatch + "], expected length=[" + command.length + "], actual length=[" + pos + "])");
            }

         return echoDetected;
         }
      catch (IOException e)
         {
         LOG.error("CreateLabSerialDeviceCommandStrategy.writeCommandWorkhorse(): IOException while trying to read the command", e);
         }

      return false;
      }

   /**
    * Reads from the serial port until the first character in the given <code>pattern</code> is seen (that's the
    * "slurp"), and then, once found, looks for the remainder of the pattern.  Returns <code>true</code> if the pattern
    * was found, <code>false</code> otherwise.
    */
   protected final boolean slurpAndMatchPattern(final SerialDeviceIOHelper ioHelper, final byte[] pattern)
      {
      LOG.trace("CreateLabSerialDeviceCommandStrategy.slurpAndMatchPattern()");

      boolean foundPattern = false;

      try
         {
         // define the ending time
         final long slurpEndTime = slurpTimeoutMillis + System.currentTimeMillis();
         final byte firstCharacter = pattern[0];

         // read until we run out of time, or we find the first character in the pattern
         boolean foundStartCharacter = false;
         while (!foundStartCharacter && (System.currentTimeMillis() <= slurpEndTime))
            {
            if (ioHelper.isDataAvailable())
               {
               try
                  {
                  final int c = ioHelper.read();
                  if (c >= 0)
                     {
                     if (LOG.isTraceEnabled())
                        {
                        LOG.trace("CreateLabSerialDeviceCommandStrategy.slurpAndMatchPattern():    read [" + (char)c + "|" + c + "]");
                        }
                     foundStartCharacter = (c == firstCharacter);
                     }
                  else
                     {
                     LOG.error("CreateLabSerialDeviceCommandStrategy.slurpAndMatchPattern(): End of stream reached while trying to read the pattern");
                     break;
                     }
                  }
               catch (IOException e)
                  {
                  LOG.error("CreateLabSerialDeviceCommandStrategy.slurpAndMatchPattern(): IOException while trying to read the pattern", e);
                  break;
                  }
               }
            }

         // if we found the start character, then try to read the remaining characters in the pattern
         final long readEndTime = readTimeoutMillis + System.currentTimeMillis();
         if (foundStartCharacter)
            {
            int numMatchedCharacters = 1;
            int characterPositionToRead = 1;// we already read the zeroth character, so start reading at position 1
            while ((numMatchedCharacters < pattern.length) &&
                   (characterPositionToRead < pattern.length) &&
                   (System.currentTimeMillis() <= readEndTime))
               {
               if (ioHelper.isDataAvailable())
                  {
                  final byte targetCharacter = pattern[characterPositionToRead++];

                  try
                     {
                     final int c = ioHelper.read();
                     if (c >= 0)
                        {
                        if (LOG.isTraceEnabled())
                           {
                           LOG.trace("CreateLabSerialDeviceCommandStrategy.slurpAndMatchPattern():    read [" + (char)c + "|" + c + "]");
                           }
                        if (c == targetCharacter)
                           {
                           numMatchedCharacters++;
                           }
                        else
                           {
                           // quit trying to read the pattern if we detect a mis-match
                           break;
                           }
                        }
                     else
                        {
                        LOG.error("CreateLabSerialDeviceCommandStrategy.slurpAndMatchPattern(): End of stream reached while trying to read the pattern");
                        break;
                        }
                     }
                  catch (IOException e)
                     {
                     LOG.error("CreateLabSerialDeviceCommandStrategy.slurpAndMatchPattern(): IOException while trying to read the pattern", e);
                     break;
                     }
                  }
               }

            foundPattern = (numMatchedCharacters == pattern.length);
            if (LOG.isTraceEnabled())
               {
               LOG.trace("CreateLabSerialDeviceCommandStrategy.slurpAndMatchPattern(): Found expected pattern = [" + foundPattern + "]");
               }
            }
         else
            {
            LOG.error("CreateLabSerialDeviceCommandStrategy.slurpAndMatchPattern(): Didn't find start character of the pattern.");
            }
         }
      catch (IOException e)
         {
         LOG.error("CreateLabSerialDeviceCommandStrategy.slurpAndMatchPattern(): IOException while trying to read the pattern", e);
         }

      return foundPattern;
      }
   }
