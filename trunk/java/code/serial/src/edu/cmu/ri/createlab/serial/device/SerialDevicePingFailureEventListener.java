package edu.cmu.ri.createlab.serial.device;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface SerialDevicePingFailureEventListener
   {
   /**
    * Called when the {@link SerialDeviceProxy} fails to ping the serial device.  Listeners should assume that the
    * connection is broken, and the serial port has been closed, so notification of the ping method is merely to allow
    * listeners to perform any necessary cleanup.
    *
    */
   void handlePingFailureEvent();
   }