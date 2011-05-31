package edu.cmu.ri.createlab.usb.hid;

import java.util.concurrent.Callable;
import edu.cmu.ri.createlab.util.commandexecution.CommandStrategy;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
final class HIDCommand implements Callable<HIDCommandResponse>
   {
   private final CommandStrategy<HIDDevice, HIDCommandResponse> commandStrategy;
   private final HIDDevice hidDevice;

   HIDCommand(final CommandStrategy<HIDDevice, HIDCommandResponse> commandStrategy, final HIDDevice hidDevice)
      {
      this.commandStrategy = commandStrategy;
      this.hidDevice = hidDevice;
      }

   public HIDCommandResponse call() throws Exception
      {
      return commandStrategy.execute(hidDevice);
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

      final HIDCommand that = (HIDCommand)o;

      if (commandStrategy != null ? !commandStrategy.equals(that.commandStrategy) : that.commandStrategy != null)
         {
         return false;
         }
      if (hidDevice != null ? !hidDevice.equals(that.hidDevice) : that.hidDevice != null)
         {
         return false;
         }

      return true;
      }

   public int hashCode()
      {
      int result = (commandStrategy != null ? commandStrategy.hashCode() : 0);
      result = 31 * result + (hidDevice != null ? hidDevice.hashCode() : 0);
      return result;
      }
   }