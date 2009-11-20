package edu.cmu.ri.createlab.serial.device;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface SerialDevicePingFailureEventPublisher
   {
   void addSerialDevicePingFailureEventListener(final SerialDevicePingFailureEventListener listener);

   void removeSerialDevicePingFailureEventListener(final SerialDevicePingFailureEventListener listener);
   }