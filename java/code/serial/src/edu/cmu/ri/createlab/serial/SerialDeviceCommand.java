package edu.cmu.ri.createlab.serial;

import java.util.concurrent.Callable;
import edu.cmu.ri.createlab.util.commandexecution.CommandStrategy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class SerialDeviceCommand implements Callable<SerialDeviceCommandResponse>
   {
   private final CommandStrategy<SerialDeviceIOHelper, SerialDeviceCommandResponse> commandStrategy;
   private final SerialDeviceIOHelper ioHelper;

   SerialDeviceCommand(final CommandStrategy<SerialDeviceIOHelper, SerialDeviceCommandResponse> commandStrategy, final SerialDeviceIOHelper ioHelper)
      {
      this.commandStrategy = commandStrategy;
      this.ioHelper = ioHelper;
      }

   public SerialDeviceCommandResponse call() throws Exception
      {
      return commandStrategy.execute(ioHelper);
      }

   public boolean equals(final Object o)
      {
      if (this == o)
         {
         return true;
         }
      if (o == null || getClass() != o.getClass())
         {
         return false;
         }

      final SerialDeviceCommand that = (SerialDeviceCommand)o;

      if (commandStrategy != null ? !commandStrategy.equals(that.commandStrategy) : that.commandStrategy != null)
         {
         return false;
         }
      if (ioHelper != null ? !ioHelper.equals(that.ioHelper) : that.ioHelper != null)
         {
         return false;
         }

      return true;
      }

   public int hashCode()
      {
      int result = (commandStrategy != null ? commandStrategy.hashCode() : 0);
      result = 31 * result + (ioHelper != null ? ioHelper.hashCode() : 0);
      return result;
      }
   }