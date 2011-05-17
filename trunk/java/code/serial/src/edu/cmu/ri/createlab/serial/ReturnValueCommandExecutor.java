package edu.cmu.ri.createlab.serial;

import org.apache.log4j.Logger;

/**
 * <p>
 * <code>ReturnValueCommandExecutor</code> is a helper class for executing a {@link SerialPortReturnValueCommandStrategy}
 * and converting its result.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class ReturnValueCommandExecutor<T>
   {
   private static final Logger LOG = Logger.getLogger(ReturnValueCommandExecutor.class);

   private final SerialPortCommandExecutionQueue commandQueue;
   private final CommandExecutionFailureHandler failureHandler;

   public ReturnValueCommandExecutor(final SerialPortCommandExecutionQueue commandQueue,
                                     final CommandExecutionFailureHandler failureHandler)
      {
      this.commandQueue = commandQueue;
      this.failureHandler = failureHandler;
      }

   /**
    * Executes the given {@link SerialPortReturnValueCommandStrategy}, converts the response
    * (using {@link SerialPortReturnValueCommandStrategy#convertResponse(SerialPortCommandResponse)}), and then returns
    * the result.  If the command fails to execute, the {@link CommandExecutionFailureHandler#handleExecutionFailure()}
    * method is called and <code>null</code> is returned.
    */
   public final T execute(final SerialPortReturnValueCommandStrategy<T> commandStrategy)
      {
      try
         {
         final SerialPortCommandResponse response = commandQueue.execute(commandStrategy);
         return commandStrategy.convertResponse(response);
         }
      catch (Exception e)
         {
         LOG.error("Exception caught while trying to execute a command", e);
         failureHandler.handleExecutionFailure();
         }

      return null;
      }
   }
