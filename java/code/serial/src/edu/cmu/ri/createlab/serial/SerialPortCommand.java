package edu.cmu.ri.createlab.serial;

import java.util.concurrent.Callable;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class SerialPortCommand implements Callable<SerialPortCommandResponse>
   {
   private final SerialPortCommandStrategy commandStrategy;
   private final SerialPortIOHelper ioHelper;

   SerialPortCommand(final SerialPortCommandStrategy commandStrategy, final SerialPortIOHelper ioHelper)
      {
      this.commandStrategy = commandStrategy;
      this.ioHelper = ioHelper;
      }

   public SerialPortCommandResponse call() throws Exception
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

      final SerialPortCommand that = (SerialPortCommand)o;

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
      int result;
      result = (commandStrategy != null ? commandStrategy.hashCode() : 0);
      result = 31 * result + (ioHelper != null ? ioHelper.hashCode() : 0);
      return result;
      }
   }