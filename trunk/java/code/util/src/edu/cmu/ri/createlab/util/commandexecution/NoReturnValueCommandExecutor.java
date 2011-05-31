package edu.cmu.ri.createlab.util.commandexecution;

import org.apache.log4j.Logger;

/**
 * <p>
 * <code>NoReturnValueCommandExecutor</code> is a helper class for executing a {@link CommandStrategy} and returning the
 * status of the response.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class NoReturnValueCommandExecutor<DeviceIOClass, ResponseClass extends CommandResponse>
   {
   private static final Logger LOG = Logger.getLogger(NoReturnValueCommandExecutor.class);

   private final CommandExecutionQueue<CommandStrategy<DeviceIOClass, ResponseClass>, ResponseClass> commandQueue;
   private final CommandExecutionFailureHandler failureHandler;

   public NoReturnValueCommandExecutor(final CommandExecutionQueue<CommandStrategy<DeviceIOClass, ResponseClass>, ResponseClass> commandQueue,
                                       final CommandExecutionFailureHandler failureHandler)
      {
      this.commandQueue = commandQueue;
      this.failureHandler = failureHandler;
      }

   /**
    * Executes the given {@link CommandStrategy} and returns the status of the response.  If the command fails to
    * execute, the {@link CommandExecutionFailureHandler#handleExecutionFailure()} method is called and
    * <code>false</code> is returned.
    */
   public final boolean execute(final CommandStrategy<DeviceIOClass, ResponseClass> commandStrategy)
      {
      try
         {
         return commandQueue.executeAndReturnStatus(commandStrategy);
         }
      catch (Exception e)
         {
         LOG.error("Exception caught while trying to execute a command", e);
         failureHandler.handleExecutionFailure();
         }
      return false;
      }
   }
