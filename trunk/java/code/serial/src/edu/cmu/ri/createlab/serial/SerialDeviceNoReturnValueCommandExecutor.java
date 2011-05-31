package edu.cmu.ri.createlab.serial;

import edu.cmu.ri.createlab.util.commandexecution.CommandExecutionFailureHandler;
import edu.cmu.ri.createlab.util.commandexecution.CommandStrategy;
import edu.cmu.ri.createlab.util.commandexecution.NoReturnValueCommandExecutor;

/**
 * <p>
 * <code>SerialDeviceNoReturnValueCommandExecutor</code> is a helper class for executing a {@link CommandStrategy}
 * and returning the status of the response.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class SerialDeviceNoReturnValueCommandExecutor extends NoReturnValueCommandExecutor<SerialDeviceIOHelper, SerialDeviceCommandResponse>
   {
   public SerialDeviceNoReturnValueCommandExecutor(final SerialDeviceCommandExecutionQueue commandQueue,
                                                   final CommandExecutionFailureHandler failureHandler)
      {
      super(commandQueue, failureHandler);
      }
   }
