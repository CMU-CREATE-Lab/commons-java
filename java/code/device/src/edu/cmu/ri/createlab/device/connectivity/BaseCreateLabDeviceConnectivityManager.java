package edu.cmu.ri.createlab.device.connectivity;

import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.SwingUtilities;
import edu.cmu.ri.createlab.device.CreateLabDevicePingFailureEventListener;
import edu.cmu.ri.createlab.device.CreateLabDeviceProxy;
import edu.cmu.ri.createlab.util.thread.DaemonThreadFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public abstract class BaseCreateLabDeviceConnectivityManager implements CreateLabDeviceConnectivityManager
   {
   private static final Logger LOG = Logger.getLogger(BaseCreateLabDeviceConnectivityManager.class);

   private final Collection<CreateLabDeviceConnectionEventListener> createLabDeviceConnectionEventListeners = new HashSet<CreateLabDeviceConnectionEventListener>();

   // Make the scan scheduler single threaded (since we definitely don't want concurrent scans!), but also make it a
   // daemon thread, so that it doesn't prevent the JVM from shutting down.
   private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory("BaseCreateLabDeviceConnectivityManager.executorService"));
   private final DeviceScanner deviceScanner = new DeviceScanner();

   // variable for synchronization
   private final byte[] connectionStateChangeLock = new byte[0];

   // these variables must only ever be read/written from within a block synchronized on connectionStateChangeLock
   private CreateLabDeviceConnectionState connectionState = CreateLabDeviceConnectionState.DISCONNECTED;
   private CreateLabDeviceProxy proxy;
   private boolean isScanning = false;

   // runnables
   private final Runnable scanAndConnectWorkhorseRunnable =
         new Runnable()
         {
         public void run()
            {
            LOG.debug("BaseCreateLabDeviceConnectivityManager: scanAndConnectWorkhorseRunnable.run()");

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
            // user cancels the scan while the device handshake is taking place.  In that case, the handshake will
            // complete and the connection state will change to connected before the cancel gets to run (due to the
            // synchronization).  Thus, we want to make sure we're actually disconnected.
            disconnect();
            }
         };

   public final CreateLabDeviceProxy getCreateLabDeviceProxy()
      {
      synchronized (connectionStateChangeLock)
         {
         return proxy;
         }
      }

   public final void addConnectionEventListener(final CreateLabDeviceConnectionEventListener listener)
      {
      if (listener != null)
         {
         createLabDeviceConnectionEventListeners.add(listener);
         }
      }

   public final void removeConnectionEventListener(final CreateLabDeviceConnectionEventListener listener)
      {
      if (listener != null)
         {
         createLabDeviceConnectionEventListeners.remove(listener);
         }
      }

   public final CreateLabDeviceConnectionState getConnectionState()
      {
      synchronized (connectionStateChangeLock)
         {
         return connectionState;
         }
      }

   /**
    * Sets the {@link CreateLabDeviceConnectionState} for the given device port and notifies the {@link CreateLabDeviceConnectionEventListener}s.  A
    * {@link NullPointerException} is thrown if the given state and/or the device port name is <code>null</code>.
    */
   // WARNING: this method must only ever be called from within a synchronized block
   protected final void setConnectionState(final CreateLabDeviceConnectionState newState, final String devicePortName)
      {
      LOG.trace("BaseCreateLabDeviceConnectivityManager.setConnectionState()");

      if (newState == null)
         {
         throw new NullPointerException("The CreateLabDeviceConnectionState cannot be null");
         }
      if (devicePortName == null)
         {
         throw new NullPointerException("The device port name cannot be null");
         }

      final CreateLabDeviceConnectionState oldState = this.connectionState;
      this.connectionState = newState;

      // notify listeners
      if (LOG.isTraceEnabled())
         {
         LOG.trace("BaseCreateLabDeviceConnectivityManager.setConnectionState(): notifying listeners of state change from [" + oldState.getStateName() + "] to [" + newState.getStateName() + "]...");
         }
      for (final CreateLabDeviceConnectionEventListener createLabDeviceConnectionEventListener : createLabDeviceConnectionEventListeners)
         {
         try
            {
            if (LOG.isTraceEnabled())
               {
               LOG.trace("BaseCreateLabDeviceConnectivityManager.setConnectionState():    notifying listener [" + createLabDeviceConnectionEventListener + "]");
               }
            createLabDeviceConnectionEventListener.handleConnectionStateChange(oldState, newState, devicePortName);
            }
         catch (Exception e)
            {
            if (LOG.isEnabledFor(Level.ERROR))
               {
               LOG.error("BaseCreateLabDeviceConnectivityManager.setConnectionState(): Exception while notifying listener " +
                         "[" + createLabDeviceConnectionEventListener + "] of connection state change from " +
                         "[" + oldState + "] to [" + newState + "] on device port [" + devicePortName + "]", e);
               }
            }
         }
      }

   public final void scanAndConnect()
      {
      runNotInGUIThread(scanAndConnectWorkhorseRunnable);
      }

   public final void cancelScanning()
      {
      runNotInGUIThread(cancelScanningWorkhorseRunnable);
      }

   public final void disconnect()
      {
      disconnect(true);
      }

   protected final void disconnect(final boolean willDisconnectFromProxy)
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
      executorService.schedule(deviceScanner, delayInSeconds, TimeUnit.SECONDS);
      }

   protected abstract CreateLabDeviceProxy scanForDeviceAndCreateProxy();

   private class DeviceScanner implements Runnable
      {
      public void run()
         {
         LOG.debug("CreateLabDeviceConnectivityManagerImpl$DeviceScanner.run()");

         synchronized (connectionStateChangeLock)
            {
            // Make sure we're still in scan mode.  We might not be if the user clicked cancel AFTER this scan was
            // scheduled, but BEFORE it actually ran.
            if (isScanning)
               {
               setConnectionState(CreateLabDeviceConnectionState.SCANNING, "");
               }
            else
               {
               return;
               }
            }

         synchronized (connectionStateChangeLock)
            {
            try
               {
               proxy = scanForDeviceAndCreateProxy();
               if (proxy == null)
                  {
                  LOG.debug("CreateLabDeviceConnectivityManagerImpl$DeviceScanner.run(): connection failed");
                  }
               else
                  {
                  LOG.debug("CreateLabDeviceConnectivityManagerImpl$DeviceScanner.run(): connection established!");

                  isScanning = false;
                  proxy.addCreateLabDevicePingFailureEventListener(
                        new CreateLabDevicePingFailureEventListener()
                        {
                        public void handlePingFailureEvent()
                           {
                           // if the ping failed, then assume the proxy has already called disconnect,
                           // so we don't need to tell the proxy to disconnect again.
                           disconnect(false);
                           }
                        });
                  setConnectionState(CreateLabDeviceConnectionState.CONNECTED, proxy.getPortName());

                  return;
                  }
               }
            catch (Exception e)
               {
               LOG.error("Exception while trying to scan for and connect to a CREATE Lab device", e);
               }
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
            LOG.debug("BaseCreateLabDeviceConnectivityManager.disconnect()");

            if (willDisconnectFromProxy && proxy != null)
               {
               proxy.disconnect();
               }
            proxy = null;
            setConnectionState(CreateLabDeviceConnectionState.DISCONNECTED, "");
            }
         }
      }
   }
