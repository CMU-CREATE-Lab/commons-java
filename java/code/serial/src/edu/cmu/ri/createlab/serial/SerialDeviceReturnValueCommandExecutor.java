package edu.cmu.ri.createlab.serial;

import edu.cmu.ri.createlab.util.commandexecution.CommandExecutionFailureHandler;
import edu.cmu.ri.createlab.util.commandexecution.ReturnValueCommandExecutor;

/**
 * <p>
 * <code>SerialDeviceReturnValueCommandExecutor</code> is a helper class for executing a {@link SerialDeviceReturnValueCommandStrategy}
 * and converting its response.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class SerialDeviceReturnValueCommandExecutor<DesiredClass> extends ReturnValueCommandExecutor<SerialDeviceIOHelper, SerialDeviceCommandResponse, DesiredClass>
   {
   public SerialDeviceReturnValueCommandExecutor(final SerialDeviceCommandExecutionQueue commandQueue,
                                                 final CommandExecutionFailureHandler failureHandler)
      {
      super(commandQueue, failureHandler);
      }
   }
