package edu.cmu.ri.createlab.serial.device.connectivity;

import edu.cmu.ri.createlab.serial.device.SerialDeviceProxyProvider;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface SerialDeviceConnectivityManager extends SerialDeviceProxyProvider, SerialDeviceConnectionEventPublisher
   {
   /** Periodically scans the serial ports and connects to the first target serial device it finds. */
   void scanAndConnect();

   /** Cancels the scan, or does nothing if not scanning. */
   void cancelScanning();

   /** Disconnects from the serial device. */
   void disconnect();

   /** Returns the current {@link SerialDeviceConnectionState}. */
   SerialDeviceConnectionState getConnectionState();
   }