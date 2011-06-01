package edu.cmu.ri.createlab.device.connectivity;

import edu.cmu.ri.createlab.device.CreateLabDeviceProxy;
import edu.cmu.ri.createlab.device.CreateLabDeviceProxyProvider;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface CreateLabDeviceConnectivityManager<ProxyClass extends CreateLabDeviceProxy> extends CreateLabDeviceProxyProvider<ProxyClass>, CreateLabDeviceConnectionEventPublisher
   {
   /**
    * Scans and connects to the first target device it finds. This method blocks until a connection is established.  A
    * {@link ConnectionException} will only be thrown if some unrecoverable failure arises.
    */
   ProxyClass connect() throws ConnectionException;

   /**
    * Scans and connects to the first target device it finds. This method returns immediately because the scanning is
    * done in a background thread.
    */
   void scanAndConnect();

   /** Cancels the connection attemp, or does nothing if not scanning. */
   void cancelConnecting();

   /** Disconnects from the device. */
   void disconnect();

   /** Returns the current {@link CreateLabDeviceConnectionState}. */
   CreateLabDeviceConnectionState getConnectionState();
   }