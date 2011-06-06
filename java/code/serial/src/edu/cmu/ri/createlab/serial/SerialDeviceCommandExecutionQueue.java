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
import edu.cmu.ri.createlab.util.commandexecution.CommandExecutionQueue;
import edu.cmu.ri.createlab.util.commandexecution.CommandStrategy;
import edu.cmu.ri.createlab.util.thread.DaemonThreadFactory;
import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import gnu.io.SerialPort;
import gnu.io.UnsupportedCommOperationException;
import org.apache.log4j.Logger;

/**
 * The SerialDeviceCommandExecutionQueue serializes communication commands with a serial port to ensure that they are
 * executed in the order received, without the possibility of one command's inputs or response conflicting with
 * another's.
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class SerialDeviceCommandExecutionQueue implements CommandExecutionQueue<CommandStrategy<SerialDeviceIOHelper, SerialDeviceCommandResponse>, SerialDeviceCommandResponse>
   {
   private static final Logger LOG = Logger.getLogger(SerialDeviceCommandExecutionQueue.class);
   private static final int OPEN_PORT_TIMEOUT_MILLIS = 1000;
   private static final int RECEIVE_TIMEOUT_MILLIS = 1000;
   private static final long DEFAULT_TASK_EXECUTION_TIMEOUT = 5;
   private static final TimeUnit DEFAULT_TASK_EXECUTION_TIMEOUT_TIMEUNIT = TimeUnit.SECONDS;

   /**
    * Creates a SerialDeviceCommandExecutionQueue for the application specified by the given
    * <code>applicationName</code> using the given {@link SerialIOConfiguration config}.  The task execution timeout
    * defaults to 5 seconds.
    */
   public static SerialDeviceCommandExecutionQueue create(final String applicationName, final SerialIOConfiguration config) throws SerialPortException, IOException
      {
      return create(applicationName, config, DEFAULT_TASK_EXECUTION_TIMEOUT, DEFAULT_TASK_EXECUTION_TIMEOUT_TIMEUNIT);
      }

   /**
    * Creates a SerialDeviceCommandExecutionQueue for the application specified by the given
    * <code>applicationName</code> using the given {@link SerialIOConfiguration config}.  The task execution timeout
    * used will be in milliseconds and is specified by the <code>taskExecutionTimeoutMillis</code> parameter.
    *
    * @deprecated use {@link #create(String, SerialIOConfiguration, long, TimeUnit)} instead
    */
   public static SerialDeviceCommandExecutionQueue create(final String applicationName, final SerialIOConfiguration config, final int taskExecutionTimeoutMillis) throws SerialPortException, IOException
      {
      return create(applicationName, config, taskExecutionTimeoutMillis, TimeUnit.MILLISECONDS);
      }

   /**
    * Creates a SerialDeviceCommandExecutionQueue for the application specified by the given
    * <code>applicationName</code> using the given {@link SerialIOConfiguration config}.  The timeout used is specified
    * by the <code>taskExecutionTimeout</code> and <code>taskExecutionTimeoutTimeUnit</code> parameters.
    */
   public static SerialDeviceCommandExecutionQueue create(final String applicationName, final SerialIOConfiguration config, final long taskExecutionTimeout, final TimeUnit taskExecutionTimeoutTimeUnit) throws SerialPortException, IOException
      {
      LOG.trace("SerialDeviceCommandExecutionQueue.create()");

      final CommPortIdentifier portIdentifier = SerialPortEnumerator.getSerialPortIdentifier(config.getPortDeviceName());

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
                  LOG.debug("SerialDeviceCommandExecutionQueue.create(): Setting serial port receive timeout to " + RECEIVE_TIMEOUT_MILLIS + "...");
                  }
               port.enableReceiveTimeout(RECEIVE_TIMEOUT_MILLIS);
               if (LOG.isDebugEnabled())
                  {
                  LOG.debug("SerialDeviceCommandExecutionQueue.create(): Check whether setting serial port receive timeout worked: (is enabled=" + port.isReceiveTimeoutEnabled() + ",timeout=" + port.getReceiveTimeout() + ")");
                  }

               // now that the port is opened and configured, create the queue
               return new SerialDeviceCommandExecutionQueue(port, taskExecutionTimeout, taskExecutionTimeoutTimeUnit);
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
   private final long taskExecutionTimeout;
   private final TimeUnit taskExecutionTimeoutTimeUnit;
   private final DefaultSerialDeviceIOHelper ioHelper;
   private final ExecutorService executor = Executors.newSingleThreadExecutor(new DaemonThreadFactory("SerialDeviceCommandExecutionQueue.executor"));

   /**
    * Creates a new SerialDeviceCommandExecutionQueue for the given {@link SerialPort}.
    *
    * @throws IOException if an error occurs while obtaining the port's input or output streams
    */
   private SerialDeviceCommandExecutionQueue(final SerialPort port, final long taskExecutionTimeout, final TimeUnit taskExecutionTimeoutTimeUnit) throws IOException
      {
      this.port = port;
      this.taskExecutionTimeout = taskExecutionTimeout;
      this.taskExecutionTimeoutTimeUnit = taskExecutionTimeoutTimeUnit;
      this.ioHelper = new DefaultSerialDeviceIOHelper(new BufferedInputStream(port.getInputStream()),
                                                      new BufferedOutputStream(port.getOutputStream()));
      }

   /**
    * Adds the given {@link CommandStrategy} to the queue, blocks until its execution is complete or times out, and then
    * returns the result.  Returns <code>null</code> if an error occurred while trying to obtain the result. The timeout
    * used depends on which of the <code>create()</code> methods was used to construct the
    * <code>SerialDeviceCommandExecutionQueue</code>.
    */
   @Override
   public SerialDeviceCommandResponse execute(final CommandStrategy<SerialDeviceIOHelper, SerialDeviceCommandResponse> commandStrategy)
      {
      return execute(commandStrategy, taskExecutionTimeout, taskExecutionTimeoutTimeUnit);
      }

   /**
    * Adds the given {@link CommandStrategy} to the queue, blocks until its execution is complete or times out, and then
    * returns the result.  Returns <code>null</code> if an error occurred while trying to obtain the result. The timeout
    * used is specified by the <code>timeout</code> and <code>timeoutTimeUnit</code> parameters.
    */
   public SerialDeviceCommandResponse execute(final CommandStrategy<SerialDeviceIOHelper, SerialDeviceCommandResponse> commandStrategy, final long timeout, final TimeUnit timeoutTimeUnit)
      {
      LOG.trace("SerialDeviceCommandExecutionQueue.execute()");

      // create the command
      final SerialDeviceCommand command = new SerialDeviceCommand(commandStrategy, ioHelper);

      // create the future task
      final FutureTask<SerialDeviceCommandResponse> task = new FutureTask<SerialDeviceCommandResponse>(command);

      try
         {
         // execute the task
         LOG.trace("SerialDeviceCommandExecutionQueue.execute():   Calling execute()");
         executor.execute(task);

         if (LOG.isTraceEnabled())
            {
            LOG.trace("SerialDeviceCommandExecutionQueue.execute():   Calling get() and returning response (timeout [" + timeout + "], timeUnit [" + timeoutTimeUnit + "])");
            }

         // block and wait for the return value
         return task.get(timeout, timeoutTimeUnit);
         }
      catch (RejectedExecutionException e)
         {
         LOG.error("SerialDeviceCommandExecutionQueue.execute():RejectedExecutionException while trying to schedule the command for execution", e);
         }
      catch (InterruptedException e)
         {
         LOG.error("SerialDeviceCommandExecutionQueue.execute():InterruptedException while trying to get the SerialDeviceCommandResponse", e);
         }
      catch (ExecutionException e)
         {
         LOG.error("SerialDeviceCommandExecutionQueue.execute():ExecutionException while trying to get the SerialDeviceCommandResponse [" + e.getCause() + "]", e);
         }
      catch (TimeoutException e)
         {
         LOG.error("SerialDeviceCommandExecutionQueue.execute():TimeoutException while trying to get the SerialDeviceCommandResponse [" + e.getCause() + "]", e);
         }

      LOG.trace("SerialDeviceCommandExecutionQueue.execute():   Returning null response");
      return null;
      }

   /**
    * Adds the given {@link CommandStrategy} to the queue, blocks until its execution is complete, and then
    * returns only the status of the result.  This is merely a convenience method which delegates to
    * {@link #execute(CommandStrategy commandStrategy)} for cases where you only need the result status of the command
    * to be executed. The timeout used depends on which of the <code>create()</code> methods was used to construct the
    * <code>SerialDeviceCommandExecutionQueue</code>.
    *
    * @see #execute(CommandStrategy commandStrategy)
    */
   @Override
   public boolean executeAndReturnStatus(final CommandStrategy<SerialDeviceIOHelper, SerialDeviceCommandResponse> commandStrategy)
      {
      return executeAndReturnStatus(commandStrategy, taskExecutionTimeout, taskExecutionTimeoutTimeUnit);
      }

   /**
    * Adds the given {@link CommandStrategy} to the queue, blocks until its execution is complete, and then
    * returns only the status of the result.  This is merely a convenience method which delegates to
    * {@link #execute(CommandStrategy commandStrategy)} for cases where you only need the result status of
    * the command to be executed. The timeout used is specified by the <code>timeout</code> and
    * <code>timeoutTimeUnit</code> parameters.
    *
    * @see #execute(CommandStrategy commandStrategy)
    */
   public boolean executeAndReturnStatus(final CommandStrategy<SerialDeviceIOHelper, SerialDeviceCommandResponse> commandStrategy, final long timeout, final TimeUnit timeoutTimeUnit)
      {
      final SerialDeviceCommandResponse response = execute(commandStrategy, timeout, timeoutTimeUnit);

      return response != null && response.wasSuccessful();
      }

   /**
    * Shuts down the command queue and then closes the serial port.  Commands in the queue are allowed to execute before
    * shutdown, but no new commands will be accepted.
    */
   @Override
   public void shutdown()
      {
      // shut down the command queue
      try
         {
         LOG.debug("SerialDeviceCommandExecutionQueue.shutdown(): Shutting down the serial port command execution queue");
         final List<Runnable> unexecutedTasks = executor.shutdownNow();
         LOG.debug("SerialDeviceCommandExecutionQueue.shutdown(): Unexecuted tasks: " + (unexecutedTasks == null ? 0 : unexecutedTasks.size()));
         LOG.debug("SerialDeviceCommandExecutionQueue.shutdown(): Waiting for the serial port command execution queue to shutdown.");
         executor.awaitTermination(10, TimeUnit.SECONDS);
         LOG.debug("SerialDeviceCommandExecutionQueue.shutdown(): Serial port command execution queue successfully shutdown");
         }
      catch (Exception e)
         {
         LOG.error("SerialDeviceCommandExecutionQueue.shutdown(): Exception while trying to shut down the serial port command execution queue", e);
         }

      // Shut down the serial port.  We use an executor here with a timeout on the call to get() from the FutureTask because
      // port.close() just hangs if the serial port isn't there, etc.
      ExecutorService closeSerialPortExecutor = null;
      try
         {
         LOG.debug("SerialDeviceCommandExecutionQueue.shutdown(): Now attempting to close the serial port...");
         closeSerialPortExecutor = Executors.newSingleThreadExecutor(new DaemonThreadFactory("SerialDeviceCommandExecutionQueue:closeSerialPortExecutor.executor"));
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
         LOG.debug("SerialDeviceCommandExecutionQueue.shutdown(): Serial port closed successfully.");
         }
      catch (final TimeoutException e)
         {
         LOG.error("SerialDeviceCommandExecutionQueue.shutdown(): TimeoutException while trying to close the serial port", e);
         }
      catch (Exception e)
         {
         LOG.error("SerialDeviceCommandExecutionQueue.shutdown(): Exception while trying to close the serial port", e);
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
            catch (Exception ignored)
               {
               LOG.error("SerialDeviceCommandExecutionQueue.shutdown(): Exception while trying to shut down the executor responsible for closing the serial port.");
               }
            }
         }
      }
   }
