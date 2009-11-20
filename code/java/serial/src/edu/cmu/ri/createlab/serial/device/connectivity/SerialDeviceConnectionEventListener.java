package edu.cmu.ri.createlab.serial.device.connectivity;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface SerialDeviceConnectionEventListener
   {
   void handleConnectionStateChange(final SerialDeviceConnectionState oldState,
                                    final SerialDeviceConnectionState newState,
                                    final String serialPortName);
   }