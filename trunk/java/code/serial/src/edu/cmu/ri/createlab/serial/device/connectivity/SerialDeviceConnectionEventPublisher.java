package edu.cmu.ri.createlab.serial.device.connectivity;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface SerialDeviceConnectionEventPublisher
   {
   void addConnectionEventListener(final SerialDeviceConnectionEventListener listener);

   void removeConnectionEventListener(final SerialDeviceConnectionEventListener listener);
   }