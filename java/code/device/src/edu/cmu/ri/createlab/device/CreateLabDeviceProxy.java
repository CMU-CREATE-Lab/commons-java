package edu.cmu.ri.createlab.device;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface CreateLabDeviceProxy extends CreateLabDevicePingFailureEventPublisher
   {
   String getPortName();

   void disconnect();
   }