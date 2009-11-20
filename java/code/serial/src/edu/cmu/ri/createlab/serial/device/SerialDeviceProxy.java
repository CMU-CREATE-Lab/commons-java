package edu.cmu.ri.createlab.serial.device;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface SerialDeviceProxy extends SerialDevicePingFailureEventPublisher
   {
   void disconnect();
   }