package edu.cmu.ri.createlab.device.connectivity;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface CreateLabDeviceConnectionEventListener
   {
   void handleConnectionStateChange(final CreateLabDeviceConnectionState oldState,
                                    final CreateLabDeviceConnectionState newState,
                                    final String portName);
   }