package edu.cmu.ri.createlab.serial;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import edu.cmu.ri.createlab.serial.config.FlowControl;
import edu.cmu.ri.createlab.serial.config.Parity;
import edu.cmu.ri.createlab.serial.config.SerialIOConfiguration;
import edu.cmu.ri.createlab.util.thread.DaemonThreadFactory;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import org.apache.log4j.Logger;

/**
 * The SerialPortCommandExecutionQueue serializes communication commands with a serial port to ensure that they are
 * executed in the order received, without the possibility of one command's inputs or response conflicting with
 * another's.
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class SerialPortCommandExecutionQueue
   {
   private static final Logger LOG = Logger.getLogger(SerialPortCommandExecutionQueue.class);
   private static final int OPEN_PORT_TIMEOUT_MILLIS = 1000;
   private static final int RECEIVE_TIMEOUT_MILLIS = 1000;
   private static final int TASK_EXECUTION_DEFAULT_TIMEOUT_MILLIS = 5000;

   public static SerialPortCommandExecutionQueue create(final String applicationName, final SerialIOConfiguration config) throws SerialPortException, IOException
      {
      return create(applicationName, config, TASK_EXECUTION_DEFAULT_TIMEOUT_MILLIS);
      }

   public static SerialPortCommandExecutionQueue create(final String applicationName, final SerialIOConfiguration config, final int taskExecutionTimeoutMillis) throws SerialPortException, IOException
      {
      LOG.trace("SerialPortCommandExecutionQueue.create()");

      final CommPortIdentifier portIdentifier = SerialPortEnumerator.getSerialPortIdentifer(config.getPortDeviceName());

      if (portIdentifier != null)
         {
         SerialPort port = null;

         try
            {
            // try to open the port
            port = (SerialPort)portIdentifier.open(applicationName, OPEN_PORT_TIMEOUT_MILLIS);

            // now configure the port
            if (port != null)
               {
               port.setSerialPortParams(config.getBaudRate().getValue(),
                                        config.getCharacterSize().getValue(),
                                        config.getStopBits().getValue(),
                                        convertParity(config.getParity()));

               // set the flow control
               if (FlowControl.HARDWARE.equals(config.getFlowControl()))
                  {
                  port.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN);
                  port.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_OUT);
                  }
               else if (FlowControl.SOFTWARE.equals(config.getFlowControl()))
                  {
                  port.setFlowControlMode(SerialPort.FLOWCONTROL_XONXOFF_IN);
                  port.setFlowControlMode(SerialPort.FLOWCONTROL_XONXOFF_OUT);
                  }
               else
                  {
                  port.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
                  }

               // try to set the receive timeout
               if (LOG.isDebugEnabled())
                  {
                  LOG.debug("SerialPortCommandExecutionQueue.create(): Setting serial port receive timeout to " + RECEIVE_TIMEOUT_MILLIS + "...");
                  }
               port.enableReceiveTimeout(RECEIVE_TIMEOUT_MILLIS);
               if (LOG.isDebugEnabled())
                  {
                  LOG.debug("SerialPortCommandExecutionQueue.create(): Check whether setting serial port receive timeout worked: (is enabled=" + port.isReceiveTimeoutEnabled() + ",timeout=" + port.getReceiveTimeout() + ")");
                  }

               // now that the port is opened and configured, create the queue
               return new SerialPortCommandExecutionQueue(port, taskExecutionTimeoutMillis);
               }
            }
         catch (PortInUseException e)
            {
            throw new SerialPortException("Failed to open serial port [" + config.getPortDeviceName() + "] because it is already in use", e);
            }
         catch (UnsupportedCommOperationException e)
            {
            port.close();
            throw new SerialPortException("Failed to configure serial port [" + config.getPortDeviceName() + "]", e);
            }
         }

      throw new SerialPortException("Failed to obtain the serial port [" + config.getPortDeviceName() + "].  Make sure that it exists and is not in use by another process.");
      }

   private static int convertParity(final Parity parity)
      {
      switch (parity)
         {
         case NONE:
            return SerialPort.PARITY_NONE;
         case EVEN:
            return SerialPort.PARITY_EVEN;
         case ODD:
            return SerialPort.PARITY_ODD;

         default:
            throw new IllegalArgumentException("Unexpected Parity [" + parity + "]");
         }
      }

   private final SerialPort port;
   private final int taskExecutionTimeoutMillis;
   private final DefaultSerialPortIOHelper ioHelper;
   private final ExecutorService executor = Executors.newSingleThreadExecutor(new DaemonThreadFactory("SerialPortCommandExecutionQueue.executor"));

   /**
    * Creates a new SerialPortCommandExecutionQueue for the given {@link SerialPort}.
    *
    * @throws IOException if an error occurs while obtaining the port's input or output streams
    */
   private SerialPortCommandExecutionQueue(final SerialPort port, final int taskExecutionTimeoutMillis) throws IOException
      {
      this.port = port;
      this.taskExecutionTimeoutMillis = taskExecutionTimeoutMillis;
      this.ioHelper = new DefaultSerialPortIOHelper(new BufferedInputStream(port.getInputStream()),
                                                    new BufferedOutputStream(port.getOutputStream()));
      }

   /**
    * Adds the given {@link SerialPortCommandStrategy} to the queue, blocks until its execution is complete, and then
    * returns the result.  Returns <code>null</code> if an error occurred while trying to obtain the result.
    */
   public SerialPortCommandResponse execute(final SerialPortCommandStrategy commandStrategy)
      {
      LOG.trace("SerialPortCommandExecutionQueue.execute()");

      // create the command
      final SerialPortCommand command = new SerialPortCommand(commandStrategy, ioHelper);

      // create the future task
      final FutureTask<SerialPortCommandResponse> task = new FutureTask<SerialPortCommandResponse>(command);

      try
         {
         // execute the task
         LOG.trace("SerialPortCommandExecutionQueue.execute():   Calling execute()");
         executor.execute(task);

         // block and wait for the return value
         LOG.trace("SerialPortCommandExecutionQueue.execute():   Calling get() and returning response");
         return task.get(taskExecutionTimeoutMillis, TimeUnit.MILLISECONDS);
         }
      catch (RejectedExecutionException e)
         {
         LOG.error("SerialPortCommandExecutionQueue.execute():RejectedExecutionException while trying to schedule the command for execution", e);
         }
      catch (InterruptedException e)
         {
         LOG.error("SerialPortCommandExecutionQueue.execute():InterruptedException while trying to get the SerialPortCommandResponse", e);
         }
      catch (ExecutionException e)
         {
         LOG.error("SerialPortCommandExecutionQueue.execute():ExecutionException while trying to get the SerialPortCommandResponse [" + e.getCause() + "]", e);
         }
      catch (TimeoutException e)
         {
         LOG.error("SerialPortCommandExecutionQueue.execute():TimeoutException while trying to get the SerialPortCommandResponse [" + e.getCause() + "]", e);
         }

      LOG.trace("SerialPortCommandExecutionQueue.execute():   Returning null response");
      return null;
      }

   /**
    * Adds the given {@link SerialPortCommandStrategy} to the queue, blocks until its execution is complete, and then
    * returns only the status of the result.  This is merely a convenience method which delegates to
    * {@link #execute(SerialPortCommandStrategy commandStrategy)} for cases where you only need the result status of the
    * command to be executed.
    *
    * @see #execute(SerialPortCommandStrategy commandStrategy)
    */
   public boolean executeAndReturnStatus(final SerialPortCommandStrategy commandStrategy)
      {
      final SerialPortCommandResponse response = execute(commandStrategy);

      return response != null && response.wasSuccessful();
      }

   /**
    * Shuts down the command queue and then closes the serial port.  Commands in the queue are allowed to execute before
    * shutdown, but no new commands will be accepted.
    */
   public void shutdown()
      {
      // shut down the command queue
      try
         {
         LOG.debug("SerialPortCommandExecutionQueue.shutdown(): Shutting down the serial port command execution queue");
         final List<Runnable> unexecutedTasks = executor.shutdownNow();
         LOG.debug("SerialPortCommandExecutionQueue.shutdown(): Unexecuted tasks: " + (unexecutedTasks == null ? 0 : unexecutedTasks.size()));
         LOG.debug("SerialPortCommandExecutionQueue.shutdown(): Waiting for the serial port command execution queue to shutdown.");
         executor.awaitTermination(10, TimeUnit.SECONDS);
         LOG.debug("SerialPortCommandExecutionQueue.shutdown(): Serial port command execution queue successfully shutdown");
         }
      catch (Exception e)
         {
         LOG.error("SerialPortCommandExecutionQueue.shutdown(): Exception while trying to shut down the serial port command execution queue", e);
         }

      // Shut down the serial port.  We use an executor here with a timeout on the call to get() from the FutureTask because
      // port.close() just hangs if the serial port isn't there, etc.
      ExecutorService closeSerialPortExecutor = null;
      try
         {
         LOG.debug("SerialPortCommandExecutionQueue.shutdown(): Now attempting to close the serial port...");
         closeSerialPortExecutor = Executors.newSingleThreadExecutor(new DaemonThreadFactory("SerialPortCommandExecutionQueue:closeSerialPortExecutor.executor"));
         final FutureTask<Boolean> task = new FutureTask<Boolean>(
               new Callable<Boolean>()
               {
               public Boolean call() throws Exception
                  {
                  port.close();
                  return Boolean.TRUE;
                  }
               });
         closeSerialPortExecutor.execute(task);
         task.get(5, TimeUnit.SECONDS);
         LOG.debug("SerialPortCommandExecutionQueue.shutdown(): Serial port closed successfully.");
         }
      catch (final TimeoutException e)
         {
         LOG.error("SerialPortCommandExecutionQueue.shutdown(): TimeoutException while trying to close the serial port", e);
         }
      catch (Exception e)
         {
         LOG.error("SerialPortCommandExecutionQueue.shutdown(): Exception while trying to close the serial port", e);
         }
      finally
         {
         if (closeSerialPortExecutor != null)
            {
            try
               {
               closeSerialPortExecutor.shutdownNow();
               closeSerialPortExecutor.awaitTermination(10, TimeUnit.SECONDS);
               }
            catch (Exception e)
               {
               LOG.error("SerialPortCommandExecutionQueue.shutdown(): Exception while trying to shut down the executor responsible for closing the serial port.");
               }
            }
         }
      }
   }
