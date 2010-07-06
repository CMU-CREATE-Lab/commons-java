package edu.cmu.ri.createlab.device;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface CreateLabDevicePingFailureEventListener
   {
   /**
    * Called when the {@link CreateLabDeviceProxy} fails to ping the device.  Listeners should assume that the
    * connection is broken, and the port has been closed, so notification of the ping method is merely to allow
    * listeners to perform any necessary cleanup.
    *
    */
   void handlePingFailureEvent();
   }