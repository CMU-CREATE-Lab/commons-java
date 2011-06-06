package edu.cmu.ri.createlab.serial;

import java.util.Arrays;
import edu.cmu.ri.createlab.util.ByteUtils;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * The <code>CreateLabSerialDeviceVariableLengthReturnValueCommandStrategy</code> class is a
 * {@link CreateLabSerialDeviceCommandStrategy} which allows for variable-length responses.  This class assumes that the
 * response consists of a header of a known and constent length and which contains the length of the variable response.
 * The length of the variable response is obtained by passing the header to the {@link #getSizeOfVariableLengthResponse}
 * method, which is responsible for parsing the header and returned the computed length of the variable length portion
 * of the response.  The {@link SerialDeviceCommandResponse} returned by the {@link #execute(SerialDeviceIOHelper)} method
 * contains both the header bytes and the bytes from the variable-length portion of the response.
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public abstract class CreateLabSerialDeviceVariableLengthReturnValueCommandStrategy<DesiredClass> extends CreateLabSerialDeviceCommandStrategy implements SerialDeviceReturnValueCommandStrategy<DesiredClass>
   {
   private static final Logger LOG = Logger.getLogger(CreateLabSerialDeviceVariableLengthReturnValueCommandStrategy.class);

   /**
    * Creates a <code>CreateLabSerialDeviceVariableLengthReturnValueCommandStrategy</code> using the default values for
    * read timeout, slurp timeout, and max retries.
    *
    * @see CreateLabSerialDeviceCommandStrategy#CreateLabSerialDeviceCommandStrategy()
    * @see CreateLabSerialDeviceCommandStrategy#DEFAULT_READ_TIMEOUT_MILLIS
    * @see CreateLabSerialDeviceCommandStrategy#DEFAULT_SLURP_TIMEOUT_MILLIS
    * @see CreateLabSerialDeviceCommandStrategy#DEFAULT_MAX_NUMBER_OF_RETRIES
    */
   protected CreateLabSerialDeviceVariableLengthReturnValueCommandStrategy()
      {
      super();
      }

   /**
    * Creates a <code>CreateLabSerialDeviceVariableLengthReturnValueCommandStrategy</code> using the given value for
    * read timeout and the default values for slurp timeout and max retries.
    *
    * @see CreateLabSerialDeviceCommandStrategy#CreateLabSerialDeviceCommandStrategy(int)
    * @see CreateLabSerialDeviceCommandStrategy#DEFAULT_READ_TIMEOUT_MILLIS
    * @see CreateLabSerialDeviceCommandStrategy#DEFAULT_SLURP_TIMEOUT_MILLIS
    * @see CreateLabSerialDeviceCommandStrategy#DEFAULT_MAX_NUMBER_OF_RETRIES
    */
   protected CreateLabSerialDeviceVariableLengthReturnValueCommandStrategy(final int readTimeoutMillis)
      {
      super(readTimeoutMillis);
      }

   /**
    * Creates a <code>CreateLabSerialDeviceVariableLengthReturnValueCommandStrategy</code> using the given values for
    * read timeout, slurp timeout, and max retries.
    *
    * @see CreateLabSerialDeviceCommandStrategy#CreateLabSerialDeviceCommandStrategy(int, int, int)
    * @see CreateLabSerialDeviceCommandStrategy#DEFAULT_READ_TIMEOUT_MILLIS
    * @see CreateLabSerialDeviceCommandStrategy#DEFAULT_SLURP_TIMEOUT_MILLIS
    * @see CreateLabSerialDeviceCommandStrategy#DEFAULT_MAX_NUMBER_OF_RETRIES
    */
   protected CreateLabSerialDeviceVariableLengthReturnValueCommandStrategy(final int readTimeoutMillis, final int slurpTimeoutMillis, final int maxNumberOfRetries)
      {
      super(readTimeoutMillis, slurpTimeoutMillis, maxNumberOfRetries);
      }

   public final SerialDeviceCommandResponse execute(final SerialDeviceIOHelper ioHelper)
      {
      LOG.trace("CreateLabSerialDeviceVariableLengthReturnValueCommandStrategy.execute()");

      // get the command to be written
      final byte[] command = getCommand();

      // write the command and check for the command echo
      final boolean wasWriteSuccessful = writeCommand(ioHelper, command);

      if (wasWriteSuccessful)
         {
         LOG.trace("CreateLabSerialDeviceVariableLengthReturnValueCommandStrategy.execute(): Reading command return value...");

         final int numBytesExpectedInHeader = getSizeOfExpectedResponseHeader();

         final SerialDeviceCommandResponse headerResponse = read(ioHelper, numBytesExpectedInHeader);

         // check whether reading the header was successful
         if (headerResponse != null && headerResponse.wasSuccessful())
            {
            // now get the size of the variable-length response
            final byte[] headerData = headerResponse.getData();
            final int numBytesExpectedInVariableLengthResponse = getSizeOfVariableLengthResponse(headerData);

            // create a buffer large enough to store both the header data and the variable length data
            final byte[] data = Arrays.copyOf(headerData, headerData.length + numBytesExpectedInVariableLengthResponse);

            // check whether reading the variable-length data was successful
            final Integer numBytesActuallyReadOfVariableLengthResponse = read(ioHelper, numBytesExpectedInVariableLengthResponse, data, headerData.length);
            if (numBytesActuallyReadOfVariableLengthResponse != null)
               {
               if (numBytesActuallyReadOfVariableLengthResponse == numBytesExpectedInVariableLengthResponse)
                  {
                  // Success! return the data
                  return new SerialDeviceCommandResponse(data);
                  }
               else
                  {
                  // Failure...
                  final byte[] dataSubset = Arrays.copyOf(headerData, headerData.length + numBytesExpectedInVariableLengthResponse);
                  System.arraycopy(data, headerData.length, dataSubset, 0, numBytesActuallyReadOfVariableLengthResponse);

                  return new SerialDeviceCommandResponse(false, dataSubset);
                  }
               }
            else
               {
               if (LOG.isEnabledFor(Level.ERROR))
                  {
                  LOG.error("CreateLabSerialDeviceVariableLengthReturnValueCommandStrategy.execute(): Failed to read variable-length response for command " + getCommandAsString(command) + ".");
                  }
               }
            }
         else
            {
            if (LOG.isEnabledFor(Level.ERROR))
               {
               LOG.error("CreateLabSerialDeviceVariableLengthReturnValueCommandStrategy.execute(): Failed to read header response for command " + getCommandAsString(command) + ".");
               }
            }
         }

      return new SerialDeviceCommandResponse(false);
      }

   private String getCommandAsString(final byte[] command)
      {
      final StringBuilder s = new StringBuilder("[");

      for (final byte b : command)
         {
         s.append("(").append((char)b).append("|").append(ByteUtils.unsignedByteToInt(b)).append(")");
         }
      s.append("]");

      return s.toString();
      }

   /**
    * Returns the length in bytes of the response header.  The response header is assumed to contain the length (in
    * bytes) of the variable-length portion of the response.
    */
   protected abstract int getSizeOfExpectedResponseHeader();

   /**
    * Returns the length in bytes of the variable length portion of the response.  The response header is passed to this
    * method so that implementing classes can parse it in order to extract the size in bytes of the variable-length
    * response.
    */
   protected abstract int getSizeOfVariableLengthResponse(final byte[] header);

   /** The command to be written, including any arguments. */
   protected abstract byte[] getCommand();
   }