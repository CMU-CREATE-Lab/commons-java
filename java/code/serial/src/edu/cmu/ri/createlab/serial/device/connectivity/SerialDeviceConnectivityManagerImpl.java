package edu.cmu.ri.createlab.serial.device.connectivity;

import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.SortedSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingUtilities;
import edu.cmu.ri.createlab.serial.SerialPortEnumerator;
import edu.cmu.ri.createlab.serial.SerialPortException;
import edu.cmu.ri.createlab.serial.device.SerialDevicePingFailureEventListener;
import edu.cmu.ri.createlab.serial.device.SerialDeviceProxy;
import edu.cmu.ri.createlab.serial.device.SerialDeviceProxyCreator;
import edu.cmu.ri.createlab.util.thread.DaemonThreadFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class SerialDeviceConnectivityManagerImpl implements SerialDeviceConnectivityManager
   {
   private static final Log LOG = LogFactory.getLog(SerialDeviceConnectivityManagerImpl.class);

   private final SerialDeviceProxyCreator serialDeviceProxyCreator;

   private final Collection<SerialDeviceConnectionEventListener> serialDeviceConnectionEventListeners = new HashSet<SerialDeviceConnectionEventListener>();

   // Make the scan scheduler single threaded (since we definitely don't want concurrent scans!), but also make it a
   // daemon thread, so that it doesn't prevent the JVM from shutting down.
   private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory("SerialDeviceConnectivityManagerImpl.executorService"));
   private final SerialPortScanner serialPortScanner = new SerialPortScanner();

   // variable for synchronization
   private final byte[] connectionStateChangeLock = new byte[0];

   // these variables must only ever be read/written from within a block synchronized on connectionStateChangeLock
   private SerialDeviceConnectionState connectionState = SerialDeviceConnectionState.DISCONNECTED;
   private SerialDeviceProxy proxy;
   private boolean isScanning = false;

   // runnables
   private final Runnable scanAndConnectWorkhorseRunnable =
         new Runnable()
         {
         public void run()
            {
            LOG.debug("SerialDeviceConnectivityManagerImpl: scanAndConnectWorkhorseRunnable.run()");

            // todo: add a check to see if we're already connected (to prevent dupe connections)

            // make sure we're not already scanning
            cancelScanning();

            synchronized (connectionStateChangeLock)
               {
               // schedule a scan
               scheduleScan(0);
               }
            }
         };
   private final Runnable disconnectWorkhorseRunnable = new DisconnectWorkhorseRunnable(true);
   private final Runnable disconnectButNotFromProxyWorkhorseRunnable = new DisconnectWorkhorseRunnable(false);
   private final Runnable cancelScanningWorkhorseRunnable =
         new Runnable()
         {
         public void run()
            {
            synchronized (connectionStateChangeLock)
               {
               isScanning = false;
               }
            // we want to disconnect here (rather than just setting the connection state) to handle the case where the
            // user cancels the scan while the serial device handshake is taking place.  In that case, the handshake will
            // complete and the connection state will change to connected before the cancel gets to run (due to the
            // synchronization).  Thus, we want to make sure we're actually disconnected.
            disconnect();
            }
         };

   public SerialDeviceConnectivityManagerImpl(final SerialDeviceProxyCreator serialDeviceProxyCreator)
      {
      this.serialDeviceProxyCreator = serialDeviceProxyCreator;
      }

   public SerialDeviceProxy getSerialDeviceProxy()
      {
      synchronized (connectionStateChangeLock)
         {
         return proxy;
         }
      }

   public void addConnectionEventListener(final SerialDeviceConnectionEventListener listener)
      {
      if (listener != null)
         {
         serialDeviceConnectionEventListeners.add(listener);
         }
      }

   public void removeConnectionEventListener(final SerialDeviceConnectionEventListener listener)
      {
      if (listener != null)
         {
         serialDeviceConnectionEventListeners.remove(listener);
         }
      }

   public SerialDeviceConnectionState getConnectionState()
      {
      synchronized (connectionStateChangeLock)
         {
         return connectionState;
         }
      }

   /**
    * Sets the {@link SerialDeviceConnectionState} for the given serial port and notifies the {@link SerialDeviceConnectionEventListener}s.  A
    * {@link NullPointerException} is thrown if the given state and/or the serial port name is <code>null</code>.
    */
   // WARNING: this method must only ever be called from within a synchronized block
   private void setConnectionState(final SerialDeviceConnectionState newState, final String serialPortName)
      {
      LOG.trace("SerialDeviceConnectivityManagerImpl.setConnectionState()");

      if (newState == null)
         {
         throw new NullPointerException("The SerialDeviceConnectionState cannot be null");
         }
      if (serialPortName == null)
         {
         throw new NullPointerException("The serial port name cannot be null");
         }

      final SerialDeviceConnectionState oldState = this.connectionState;
      this.connectionState = newState;

      // notify listeners
      if (LOG.isTraceEnabled())
         {
         LOG.trace("SerialDeviceConnectivityManagerImpl.setConnectionState(): notifying listeners of state change from [" + oldState.getStateName() + "] to [" + newState.getStateName() + "]...");
         }
      for (final SerialDeviceConnectionEventListener serialDeviceConnectionEventListener : serialDeviceConnectionEventListeners)
         {
         try
            {
            if (LOG.isTraceEnabled())
               {
               LOG.trace("SerialDeviceConnectivityManagerImpl.setConnectionState():    notifying listener [" + serialDeviceConnectionEventListener + "]");
               }
            serialDeviceConnectionEventListener.handleConnectionStateChange(oldState, newState, serialPortName);
            }
         catch (Exception e)
            {
            if (LOG.isErrorEnabled())
               {
               LOG.error("SerialDeviceConnectivityManagerImpl.setConnectionState(): Exception while notifying listener " +
                         "[" + serialDeviceConnectionEventListener + "] of connection state change from " +
                         "[" + oldState + "] to [" + newState + "] on serial port [" + serialPortName + "]", e);
               }
            }
         }
      }

   public void scanAndConnect()
      {
      runNotInGUIThread(scanAndConnectWorkhorseRunnable);
      }

   public void cancelScanning()
      {
      runNotInGUIThread(cancelScanningWorkhorseRunnable);
      }

   public void disconnect()
      {
      disconnect(true);
      }

   private void disconnect(final boolean willDisconnectFromProxy)
      {
      if (willDisconnectFromProxy)
         {
         runNotInGUIThread(disconnectWorkhorseRunnable);
         }
      else
         {
         runNotInGUIThread(disconnectButNotFromProxyWorkhorseRunnable);
         }
      }

   private void runNotInGUIThread(final Runnable runnable)
      {
      if (SwingUtilities.isEventDispatchThread())
         {
         executorService.execute(runnable);
         }
      else
         {
         runnable.run();
         }
      }

   // WARNING: this method must only ever be called from within a synchronized block
   private void scheduleScan(final int delayInSeconds)
      {
      isScanning = true;
      executorService.schedule(serialPortScanner, delayInSeconds, TimeUnit.SECONDS);
      }

   private class SerialPortScanner implements Runnable
      {
      public void run()
         {
         LOG.debug("SerialDeviceConnectivityManagerImpl$SerialPortScanner.run()");

         synchronized (connectionStateChangeLock)
            {
            // Make sure we're still in scan mode.  We might not be if the user clicked cancel AFTER this scan was
            // scheduled, but BEFORE it actually ran.
            if (isScanning)
               {
               setConnectionState(SerialDeviceConnectionState.SCANNING, "");
               }
            else
               {
               return;
               }
            }

         // check each available serial port for the target serial device, and connect to the first one found
         final SortedSet<String> availableSerialPorts = SerialPortEnumerator.getAvailableSerialPorts();
         if ((availableSerialPorts != null) && (!availableSerialPorts.isEmpty()))
            {
            for (final String portName : availableSerialPorts)
               {
               synchronized (connectionStateChangeLock)
                  {
                  if (isScanning && portName != null)
                     {
                     try
                        {
                        LOG.debug("SerialDeviceConnectivityManagerImpl$SerialPortScanner.run(): Attempting connection to port [" + portName + "]...");
                        setConnectionState(SerialDeviceConnectionState.SCANNING, portName);

                        proxy = serialDeviceProxyCreator.createSerialDeviceProxy(portName);
                        if (proxy == null)
                           {
                           LOG.debug("SerialDeviceConnectivityManagerImpl$SerialPortScanner.run(): connection failed");
                           }
                        else
                           {
                           LOG.debug("SerialDeviceConnectivityManagerImpl$SerialPortScanner.run(): connection established!");

                           isScanning = false;
                           proxy.addSerialDevicePingFailureEventListener(
                                 new SerialDevicePingFailureEventListener()
                                 {
                                 public void handlePingFailureEvent()
                                    {
                                    // if the ping failed, then assume the proxy has already called disconnect,
                                    // so we don't need to tell the proxy to disconnect again.
                                    disconnect(false);
                                    }
                                 });
                           setConnectionState(SerialDeviceConnectionState.CONNECTED, portName);

                           return;
                           }
                        }
                     catch (IOException e)
                        {
                        LOG.error("IOException while trying to connect to create SerialDeviceProxy for port [" + portName + "]", e);
                        }
                     catch (SerialPortException e)
                        {
                        LOG.error("SerialPortException while trying to connect to create SerialDeviceProxy for port [" + portName + "]", e);
                        }
                     }
                  }
               }
            }
         else
            {
            LOG.debug("SerialDeviceConnectivityManagerImpl$SerialPortScanner.run(): No available serial ports.");
            }

         // If we got here, then we failed to connect.  So, schedule a new scan if the user didn't cancel the scan...
         synchronized (connectionStateChangeLock)
            {
            if (isScanning)
               {
               LOG.debug("Failed to connect, but we're still in scan mode, so schedule a new scan");
               scheduleScan(1);
               }
            }
         }
      }

   private class DisconnectWorkhorseRunnable implements Runnable
      {
      private final boolean willDisconnectFromProxy;

      private DisconnectWorkhorseRunnable(final boolean willDisconnectFromProxy)
         {
         this.willDisconnectFromProxy = willDisconnectFromProxy;
         }

      public void run()
         {
         synchronized (connectionStateChangeLock)
            {
            LOG.debug("SerialDeviceConnectivityManagerImpl.disconnect()");

            if (willDisconnectFromProxy && proxy != null)
               {
               proxy.disconnect();
               }
            proxy = null;
            setConnectionState(SerialDeviceConnectionState.DISCONNECTED, "");
            }
         }
      }
   }
