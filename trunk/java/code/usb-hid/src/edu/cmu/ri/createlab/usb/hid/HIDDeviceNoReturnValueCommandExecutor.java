package edu.cmu.ri.createlab.usb.hid;

import edu.cmu.ri.createlab.util.commandexecution.CommandExecutionFailureHandler;
import edu.cmu.ri.createlab.util.commandexecution.CommandStrategy;
import edu.cmu.ri.createlab.util.commandexecution.NoReturnValueCommandExecutor;

/**
 * <p>
 * <code>HIDDeviceNoReturnValueCommandExecutor</code> is a helper class for executing a {@link CommandStrategy}
 * and returning the status of the response.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class HIDDeviceNoReturnValueCommandExecutor extends NoReturnValueCommandExecutor<HIDDevice, HIDCommandResponse>
   {
   public HIDDeviceNoReturnValueCommandExecutor(final HIDCommandExecutionQueue commandQueue,
                                                final CommandExecutionFailureHandler failureHandler)
      {
      super(commandQueue, failureHandler);
      }
   }
