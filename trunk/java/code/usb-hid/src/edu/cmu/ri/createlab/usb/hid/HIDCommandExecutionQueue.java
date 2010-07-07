package edu.cmu.ri.createlab.usb.hid;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import edu.cmu.ri.createlab.util.thread.DaemonThreadFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The HIDCommandExecutionQueue serializes communication commands with an HID Device to ensure that they are
 * executed in the order received, without the possibility of one command's inputs or response conflicting with
 * another's.
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class HIDCommandExecutionQueue
   {
   private static final Log LOG = LogFactory.getLog(HIDCommandExecutionQueue.class);

   private final HIDDevice hidDevice;
   private final ExecutorService executor = Executors.newSingleThreadExecutor(new DaemonThreadFactory("HIDCommandExecutionQueue.executor"));

   public HIDCommandExecutionQueue(final HIDDevice hidDevice)
      {
      if (hidDevice == null)
         {
         throw new IllegalArgumentException("The HIDDevice cannot be null");
         }
      this.hidDevice = hidDevice;
      }

   /**
    * Adds the given {@link HIDCommandStrategy} to the queue, blocks until its execution is complete, and then returns the
    * result.  Returns <code>null</code> if an error occurred while trying to obtain the result.
    */
   public HIDCommandResult execute(final HIDCommandStrategy commandStrategy)
      {
      LOG.trace("HIDCommandExecutionQueue.execute()");

      // create the command
      final HIDCommand command = new HIDCommand(commandStrategy, hidDevice);

      // create the future task
      final FutureTask<HIDCommandResult> task = new FutureTask<HIDCommandResult>(command);

      try
         {
         // execute the task
         LOG.trace("HIDCommandExecutionQueue.execute():   Calling execute()");
         executor.execute(task);

         // block and wait for the return value
         LOG.trace("HIDCommandExecutionQueue.execute():   Calling get() and returning response");
         return task.get();
         }
      catch (RejectedExecutionException e)
         {
         LOG.error("HIDCommandExecutionQueue.execute():RejectedExecutionException while trying to schedule the command for execution", e);
         }
      catch (InterruptedException e)
         {
         LOG.error("HIDCommandExecutionQueue.execute():InterruptedException while trying to get the HIDCommandResult", e);
         }
      catch (ExecutionException e)
         {
         LOG.error("HIDCommandExecutionQueue.execute():ExecutionException while trying to get the HIDCommandResult [" + e.getCause() + "]", e);
         }

      LOG.trace("HIDCommandExecutionQueue.execute():   Returning null response");
      return null;
      }

   /**
    * Adds the given {@link HIDCommandStrategy} to the queue, blocks until its execution is complete, and then returns only
    * the status of the result.  This is merely a convenience method which delegates to {@link #execute(HIDCommandStrategy
    * commandStrategy)} for cases where you only need the result status of the command to be executed.
    *
    * @see #execute(HIDCommandStrategy commandStrategy)
    */
   public boolean executeAndReturnStatus(final HIDCommandStrategy commandStrategy)
      {
      final HIDCommandResult response = execute(commandStrategy);

      return response != null && response.wasSuccessful();
      }

   /**
    * Shuts down the command queue and then closes the HID device.  Commands in the queue are allowed to execute before
    * shutdown, but no new commands will be accepted.
    */
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
