package edu.cmu.ri.createlab.device.connectivity;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface CreateLabDeviceConnectionEventPublisher
   {
   void addConnectionEventListener(final CreateLabDeviceConnectionEventListener listener);

   void removeConnectionEventListener(final CreateLabDeviceConnectionEventListener listener);
   }