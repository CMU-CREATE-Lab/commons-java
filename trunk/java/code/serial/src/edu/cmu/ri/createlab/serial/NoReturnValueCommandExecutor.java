package edu.cmu.ri.createlab.serial;

import org.apache.log4j.Logger;

/**
 * <p>
 * <code>NoReturnValueCommandExecutor</code> is a helper class for executing a {@link SerialPortCommandStrategy}
 * and returning the status of the response.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class NoReturnValueCommandExecutor
   {
   private static final Logger LOG = Logger.getLogger(NoReturnValueCommandExecutor.class);

   private final SerialPortCommandExecutionQueue commandQueue;
   private final CommandExecutionFailureHandler failureHandler;

   public NoReturnValueCommandExecutor(final SerialPortCommandExecutionQueue commandQueue,
                                       final CommandExecutionFailureHandler failureHandler)
      {
      this.commandQueue = commandQueue;
      this.failureHandler = failureHandler;
      }

   /**
    * Executes the given {@link SerialPortCommandStrategy} and returns the status of the response.  If the command fails
    * to execute, the {@link CommandExecutionFailureHandler#handleExecutionFailure()} method is called and
    * <code>false</code> is returned.
    */
   public final boolean execute(final SerialPortCommandStrategy commandStrategy)
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
