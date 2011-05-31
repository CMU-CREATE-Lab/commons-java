package edu.cmu.ri.createlab.usb.hid;

import edu.cmu.ri.createlab.util.commandexecution.CommandExecutionFailureHandler;
import edu.cmu.ri.createlab.util.commandexecution.CommandStrategy;
import edu.cmu.ri.createlab.util.commandexecution.ReturnValueCommandExecutor;

/**
 * <p>
 * <code>HIDDeviceReturnValueCommandExecutor</code> is a helper class for executing a {@link CommandStrategy}
 * and converting its response.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class HIDDeviceReturnValueCommandExecutor<DesiredClass> extends ReturnValueCommandExecutor<HIDDevice, HIDCommandResponse, DesiredClass>
   {
   public HIDDeviceReturnValueCommandExecutor(final HIDCommandExecutionQueue commandQueue,
                                              final CommandExecutionFailureHandler failureHandler)
      {
      super(commandQueue, failureHandler);
      }
   }
