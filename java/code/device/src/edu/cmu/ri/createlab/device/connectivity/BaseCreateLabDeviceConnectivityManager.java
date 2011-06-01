package edu.cmu.ri.createlab.device.connectivity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
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
public abstract class BaseCreateLabDeviceConnectivityManager<ProxyClass extends CreateLabDeviceProxy> implements CreateLabDeviceConnectivityManager<ProxyClass>
   {
   private static final Logger LOG = Logger.getLogger(BaseCreateLabDeviceConnectivityManager.class);

   private final Semaphore connectionCompleteSemaphore = new Semaphore(1);
   private final Collection<CreateLabDeviceConnectionEventListener> createLabDeviceConnectionEventListeners = new HashSet<CreateLabDeviceConnectionEventListener>();

   // Make the scan scheduler single threaded (since we definitely don't want concurrent scans!), but also make it a
   // daemon thread, so that it doesn't prevent the JVM from shutting down.
   private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(new DaemonThreadFactory("BaseCreateLabDeviceConnectivityManager.executorService"));
   private final DeviceScanner deviceScanner = new DeviceScanner();

   // variable for synchronization
   private final byte[] connectionStateChangeLock = new byte[0];

   // these variables must only ever be read/written from within a block synchronized on connectionStateChangeLock
   private CreateLabDeviceConnectionState connectionState = CreateLabDeviceConnectionState.DISCONNECTED;
   private ProxyClass proxy;
   private boolean isScanning = false;

   // runnables
   private final Runnable scanAndConnectWorkhorseRunnable =
         new Runnable()
         {
         @Override
         public void run()
            {
            LOG.debug("BaseCreateLabDeviceConnectivityManager: scanAndConnectWorkhorseRunnable.run()");

            // todo: add a check to see if we're already connected (to prevent dupe connections)

            // make sure we're not already trying to connect
            cancelConnecting();

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
         @Override
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

   @Override
   public final ProxyClass connect() throws ConnectionException
      {
      LOG.debug("Connecting to the device...this may take a few seconds...");

      final List<ProxyClass> proxyClass = new ArrayList<ProxyClass>(1);

      final CreateLabDeviceConnectionEventListener listener =
            new CreateLabDeviceConnectionEventListener()
            {
            @Override
            public void handleConnectionStateChange(final CreateLabDeviceConnectionState oldState, final CreateLabDeviceConnectionState newState, final String portName)
               {
               if (CreateLabDeviceConnectionState.CONNECTED.equals(newState))
                  {
                  LOG.debug("BaseCreateLabDeviceConnectivityManager.handleConnectionStateChange(): Connected");

                  // connection complete, so release the lock
                  connectionCompleteSemaphore.release();
                  proxyClass.add(getCreateLabDeviceProxy());
                  }
               else if (CreateLabDeviceConnectionState.DISCONNECTED.equals(newState))
                  {
                  LOG.debug("BaseCreateLabDeviceConnectivityManager.handleConnectionStateChange(): Disconnected");
                  }
               else if (CreateLabDeviceConnectionState.SCANNING.equals(newState))
                  {
                  LOG.debug("BaseCreateLabDeviceConnectivityManager.handleConnectionStateChange(): Scanning...");
                  }
               else
                  {
                  LOG.error("BaseCreateLabDeviceConnectivityManager.handleConnectionStateChange(): Unexpected CreateLabDeviceConnectionState [" + newState + "]");
                  }
               }
            };

      addConnectionEventListener(listener);

      LOG.trace("BaseCreateLabDeviceConnectivityManager.connect(): 1) aquiring connection lock");

      // acquire the lock, which will be released once the connection is complete
      connectionCompleteSemaphore.acquireUninterruptibly();

      LOG.trace("BaseCreateLabDeviceConnectivityManager.connect(): 2) connecting");

      // try to connect
      scanAndConnect();

      LOG.trace("BaseCreateLabDeviceConnectivityManager.connect(): 3) waiting for connection to complete");

      // try to acquire the lock again, which will block until the connection is complete
      connectionCompleteSemaphore.acquireUninterruptibly();

      LOG.trace("BaseCreateLabDeviceConnectivityManager.connect(): 4) releasing lock");

      // we know the connection has completed (i.e. either connected or the connection failed) at this point, so just release the lock
      connectionCompleteSemaphore.release();

      // remove the connection event listener
      removeConnectionEventListener(listener);

      LOG.trace("BaseCreateLabDeviceConnectivityManager.connect(): 5) make sure we're actually connected");

      // if we're not connected, then throw an exception
      if (!CreateLabDeviceConnectionState.CONNECTED.equals(getConnectionState()))
         {
         LOG.error("BaseCreateLabDeviceConnectivityManager.connect(): Failed to connect to the device!  Aborting.");
         throw new ConnectionException("Failed to connect to the device");
         }

      LOG.trace("BaseCreateLabDeviceConnectivityManager.connect(): 6) All done!");

      if (proxyClass.size() == 1)
         {
         final ProxyClass theProxy = proxyClass.get(0);
         if (theProxy != null)
            {
            return theProxy;
            }
         }

      throw new ConnectionException("Failed to connect to the device");
      }

   @Override
   public final ProxyClass getCreateLabDeviceProxy()
      {
      synchronized (connectionStateChangeLock)
         {
         return proxy;
         }
      }

   @Override
   public final void addConnectionEventListener(final CreateLabDeviceConnectionEventListener listener)
      {
      if (listener != null)
         {
         createLabDeviceConnectionEventListeners.add(listener);
         }
      }

   @Override
   public final void removeConnectionEventListener(final CreateLabDeviceConnectionEventListener listener)
      {
      if (listener != null)
         {
         createLabDeviceConnectionEventListeners.remove(listener);
         }
      }

   @Override
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
   private void setConnectionState(final CreateLabDeviceConnectionState newState, final String devicePortName)
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

   @Override
   public final void scanAndConnect()
      {
      runNotInGUIThread(scanAndConnectWorkhorseRunnable);
      }

   @Override
   public final void cancelConnecting()
      {
      runNotInGUIThread(cancelScanningWorkhorseRunnable);
      }

   @Override
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

   protected abstract ProxyClass scanForDeviceAndCreateProxy();

   private class DeviceScanner implements Runnable
      {
      @Override
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
                        @Override
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

      @Override
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
