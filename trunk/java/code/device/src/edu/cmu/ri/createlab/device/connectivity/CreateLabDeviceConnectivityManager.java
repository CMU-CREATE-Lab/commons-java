package edu.cmu.ri.createlab.device.connectivity;

import edu.cmu.ri.createlab.device.CreateLabDeviceProxyProvider;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface CreateLabDeviceConnectivityManager extends CreateLabDeviceProxyProvider, CreateLabDeviceConnectionEventPublisher
   {
   /** Periodically scans and connects to the first target device it finds. */
   void scanAndConnect();

   /** Cancels the scan, or does nothing if not scanning. */
   void cancelScanning();

   /** Disconnects from the device. */
   void disconnect();

   /** Returns the current {@link CreateLabDeviceConnectionState}. */
   CreateLabDeviceConnectionState getConnectionState();
   }