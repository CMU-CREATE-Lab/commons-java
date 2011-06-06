package edu.cmu.ri.createlab.usb.hid;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import edu.cmu.ri.createlab.util.commandexecution.CommandExecutionQueue;
import edu.cmu.ri.createlab.util.commandexecution.CommandStrategy;
import edu.cmu.ri.createlab.util.thread.DaemonThreadFactory;
import org.apache.log4j.Logger;

/**
 * The HIDCommandExecutionQueue serializes communication commands with an HID Device to ensure that they are
 * executed in the order received, without the possibility of one command's inputs or response conflicting with
 * another's.
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class HIDCommandExecutionQueue implements CommandExecutionQueue<CommandStrategy<HIDDevice, HIDCommandResponse>, HIDCommandResponse>
   {
   private static final Logger LOG = Logger.getLogger(HIDCommandExecutionQueue.class);

   private final HIDDevice hidDevice;
   private final ExecutorService executor = Executors.newSingleThreadExecutor(new DaemonThreadFactory("HIDCommandExecutionQueue.executor"));
   private final long taskExecutionTimeout;
   private final TimeUnit taskExecutionTimeoutTimeUnit;

   /**
    * Creates a <code>HIDCommandExecutionQueue</code> for the given {@link HIDDevice} with no task execution timeout
    * (thus task execution will block until complete).
    */
   public HIDCommandExecutionQueue(final HIDDevice hidDevice)
      {
      this(hidDevice, -1, null); // a null TimeUnit means no timeout
      }

   /**
    * Creates a <code>HIDCommandExecutionQueue</code> for the given {@link HIDDevice} with a task execution timeout
    * specified by the <code>taskExecutionTimeout</code> and <code>taskExecutionTimeoutTimeUnit</code> parameters.
    */
   public HIDCommandExecutionQueue(final HIDDevice hidDevice, final long taskExecutionTimeout, final TimeUnit taskExecutionTimeoutTimeUnit)
      {
      if (hidDevice == null)
         {
         throw new IllegalArgumentException("The HIDDevice cannot be null");
         }
      this.hidDevice = hidDevice;
      this.taskExecutionTimeout = taskExecutionTimeout;
      this.taskExecutionTimeoutTimeUnit = taskExecutionTimeoutTimeUnit;
      }

   /**
    * Adds the given {@link CommandStrategy} to the queue, blocks until its execution is complete or times out
    * (depending on which constructor was used to create the instance), and then returns the result.  Returns
    * <code>null</code> if an error occurred while trying to obtain the result.
    *
    */
   @Override
   public HIDCommandResponse execute(final CommandStrategy<HIDDevice, HIDCommandResponse> commandStrategy) throws HIDDeviceNotConnectedException, HIDDeviceFailureException
      {
      return execute(commandStrategy, taskExecutionTimeout, taskExecutionTimeoutTimeUnit);
      }

   /**
    * Adds the given {@link CommandStrategy} to the queue, blocks until its execution is complete or times out, and then
    * returns the result.  Returns <code>null</code> if an error occurred while trying to obtain the result.
    */
   @Override
   public HIDCommandResponse execute(final CommandStrategy<HIDDevice, HIDCommandResponse> commandStrategy, final long timeout, final TimeUnit timeoutTimeUnit) throws HIDDeviceNotConnectedException, HIDDeviceFailureException
      {
      LOG.trace("HIDCommandExecutionQueue.execute()");

      // create the command
      final HIDCommand command = new HIDCommand(commandStrategy, hidDevice);

      // create the future task
      final FutureTask<HIDCommandResponse> task = new FutureTask<HIDCommandResponse>(command);

      try
         {
         // execute the task
         LOG.trace("HIDCommandExecutionQueue.execute():   Calling execute()");
         executor.execute(task);

         // block and wait for the return value
         if (timeoutTimeUnit == null)
            {
            LOG.trace("HIDCommandExecutionQueue.execute():   Calling get() and returning response (no timeout)");
            return task.get();
            }
         else
            {
            if (LOG.isTraceEnabled())
               {
               LOG.trace("HIDCommandExecutionQueue.execute():   Calling get() and returning response (timeout [" + timeout + "], timeUnit [" + timeoutTimeUnit + "])");
               }
            return task.get(timeout, timeoutTimeUnit);
            }
         }
      catch (RejectedExecutionException e)
         {
         LOG.error("HIDCommandExecutionQueue.execute():RejectedExecutionException while trying to schedule the command for execution", e);
         }
      catch (InterruptedException e)
         {
         LOG.error("HIDCommandExecutionQueue.execute():InterruptedException while trying to get the HIDCommandResponse", e);
         }
      catch (ExecutionException e)
         {
         final Throwable cause = e.getCause();
         LOG.error("HIDCommandExecutionQueue.execute():ExecutionException while trying to get the HIDCommandResponse [" + cause + "]", e);
         if (cause instanceof HIDDeviceNotConnectedException)
            {
            LOG.info("HIDCommandExecutionQueue.execute(): Cause of ExecutionException is HIDDeviceNotConnectedException, so rethrowing HIDDeviceNotConnectedException...");
            throw (HIDDeviceNotConnectedException)cause;
            }
         else if (cause instanceof HIDDeviceFailureException)
            {
            LOG.info("HIDCommandExecutionQueue.execute(): Cause of ExecutionException is HIDDeviceFailureException, so rethrowing HIDDeviceFailureException...");
            throw (HIDDeviceFailureException)cause;
            }
         else
            {
            LOG.info("HIDCommandExecutionQueue.execute(): Cause of ExecutionException is unrecognized, so simply returning null");
            }
         }
      catch (TimeoutException e)
         {
         LOG.error("HIDCommandExecutionQueue.execute():TimeoutException while trying to get the HIDCommandResponse", e);
         }

      LOG.trace("HIDCommandExecutionQueue.execute():   Returning null response");
      return null;
      }

   /**
    * Adds the given {@link CommandStrategy} to the queue, blocks until its execution is complete or times out
    * (depending on which constructor was used to create the instance), and then returns only the status of the result.
    * This is merely a convenience method which delegates to {@link #execute(CommandStrategy commandStrategy)} for cases
    * where you only need the result status of the command to be executed.
    *
    * @see #execute(CommandStrategy commandStrategy)
    */
   @Override
   public boolean executeAndReturnStatus(final CommandStrategy<HIDDevice, HIDCommandResponse> commandStrategy) throws HIDDeviceNotConnectedException, HIDDeviceFailureException
      {
      return executeAndReturnStatus(commandStrategy, taskExecutionTimeout, taskExecutionTimeoutTimeUnit);
      }

   /**
    * Adds the given {@link CommandStrategy} to the queue, blocks until its execution is complete or times out, and then
    * returns only the status of the result.  This is merely a convenience method which delegates to
    * {@link #execute(CommandStrategy commandStrategy)} for cases where you only need the result status of the command
    * to be executed.  The timeout used is specified by the <code>timeout</code> and
    * <code>timeoutTimeUnit</code> parameters.
    *
    * @see #execute(CommandStrategy commandStrategy)
    */
   @Override
   public boolean executeAndReturnStatus(final CommandStrategy<HIDDevice, HIDCommandResponse> commandStrategy, final long timeout, final TimeUnit timeoutTimeUnit) throws HIDDeviceNotConnectedException, HIDDeviceFailureException
      {
      final HIDCommandResponse response = execute(commandStrategy, timeout, timeoutTimeUnit);

      return response != null && response.wasSuccessful();
      }

   /**
    * Shuts down the command queue and then closes the HID device.  Commands in the queue are allowed to execute before
    * shutdown, but no new commands will be accepted.
    */
   @Override
   public void shutdown()
      {
      // shut down the command queue
      try
         {
         LOG.debug("HIDCommandExecutionQueue.shutdown(): Shutting down the HID device command execution queue");
         final List<Runnable> unexecutedTasks = executor.shutdownNow();
         LOG.debug("HIDCommandExecutionQueue.shutdown(): Unexecuted tasks: " + (unexecutedTasks == null ? 0 : unexecutedTasks.size()));
         LOG.debug("HIDCommandExecutionQueue.shutdown(): Waiting for the HID device command execution queue to shutdown.");
         executor.awaitTermination(10, TimeUnit.SECONDS);
         LOG.debug("HIDCommandExecutionQueue.shutdown(): HID device command execution queue successfully shutdown");
         }
      catch (Exception e)
         {
         LOG.error("HIDCommandExecutionQueue.shutdown(): Exception while trying to shut down the HID device command execution queue", e);
         }

      // disconnect from the HID device
      try
         {
         LOG.debug("HIDCommandExecutionQueue.shutdown(): Now attempting to disconnect from the HID device...");
         if (hidDevice.disconnect())
            {
            LOG.debug("HIDCommandExecutionQueue.shutdown(): HID device disconnected successfully.");
            }
         else
            {
            LOG.debug("HIDCommandExecutionQueue.shutdown(): Failed to disconnect from the HID device.");
            }
         }
      catch (Exception e)
         {
         LOG.error("HIDCommandExecutionQueue.shutdown(): Exception while trying to disconnect from the HID device", e);
         }
      }
   }
