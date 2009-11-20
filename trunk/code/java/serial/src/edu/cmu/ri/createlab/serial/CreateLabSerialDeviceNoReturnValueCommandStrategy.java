package edu.cmu.ri.createlab.serial;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public abstract class CreateLabSerialDeviceNoReturnValueCommandStrategy extends CreateLabSerialDeviceCommandStrategy implements SerialPortCommandStrategy
   {
   public final SerialPortCommandResponse execute(final SerialPortIOHelper ioHelper)
      {
      // get the command to be written
      final byte[] command = getCommand();

      // write the command and check for the command echo
      final boolean wasSuccessful = writeCommand(ioHelper, command);

      // return the response
      return new SerialPortCommandResponse(wasSuccessful);
      }

   /** Converts the given number to an ASCII character (note that this implies that the greatest index possible is 9). */
   protected final char convertDeviceIndexToASCII(final int index)
      {
      return String.valueOf(index).charAt(0);
      }

   protected abstract byte[] getCommand();
   }
