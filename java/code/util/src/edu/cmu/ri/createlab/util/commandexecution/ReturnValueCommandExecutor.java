package edu.cmu.ri.createlab.util.commandexecution;

import java.util.concurrent.TimeUnit;
import org.apache.log4j.Logger;

/**
 * <p>
 * <code>ReturnValueCommandExecutor</code> is a helper class for executing a {@link ReturnValueCommandStrategy} and
 * converting its result.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class ReturnValueCommandExecutor<DeviceIOClass, ResponseClass extends CommandResponse, DesiredClass>
   {
   private static final Logger LOG = Logger.getLogger(ReturnValueCommandExecutor.class);

   private final CommandExecutionQueue<CommandStrategy<DeviceIOClass, ResponseClass>, ResponseClass> commandQueue;
   private final CommandExecutionFailureHandler failureHandler;

   public ReturnValueCommandExecutor(final CommandExecutionQueue<CommandStrategy<DeviceIOClass, ResponseClass>, ResponseClass> commandQueue,
                                     final CommandExecutionFailureHandler failureHandler)
      {
      this.commandQueue = commandQueue;
      this.failureHandler = failureHandler;
      }

   /**
    * Executes the given {@link ReturnValueCommandStrategy}, converts the response (using
    * {@link ReturnValueCommandStrategy#convertResponse(CommandResponse)}), and then returns the result.  If the command
    * fails to execute, the {@link CommandExecutionFailureHandler#handleExecutionFailure()} method is called and
    * <code>null</code> is returned.
    *
    * @see CommandExecutionQueue#execute(CommandStrategy)
    */
   public final DesiredClass execute(final ReturnValueCommandStrategy<DeviceIOClass, ResponseClass, DesiredClass> commandStrategy)
      {
      try
         {
         final ResponseClass response = commandQueue.execute(commandStrategy);
         return commandStrategy.convertResponse(response);
         }
      catch (Exception e)
         {
         LOG.error("Exception caught while trying to execute a command", e);
         failureHandler.handleExecutionFailure();
         }

      return null;
      }

   /**
    * Executes the given {@link ReturnValueCommandStrategy}, converts the response (using
    * {@link ReturnValueCommandStrategy#convertResponse(CommandResponse)}), and then returns the result.  If the command
    * fails to execute, the {@link CommandExecutionFailureHandler#handleExecutionFailure()} method is called and
    * <code>null</code> is returned.
    *
    * @see CommandExecutionQueue#execute(CommandStrategy, long, TimeUnit)
    */
   public final DesiredClass execute(final ReturnValueCommandStrategy<DeviceIOClass, ResponseClass, DesiredClass> commandStrategy, final long taskExecutionTimeout, final TimeUnit taskExecutionTimeoutTimeUnit)
      {
      try
         {
         final ResponseClass response = commandQueue.execute(commandStrategy, taskExecutionTimeout, taskExecutionTimeoutTimeUnit);
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