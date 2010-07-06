package edu.cmu.ri.createlab.device;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public interface CreateLabDevicePingFailureEventPublisher
   {
   void addCreateLabDevicePingFailureEventListener(final CreateLabDevicePingFailureEventListener listener);

   void removeCreateLabDevicePingFailureEventListener(final CreateLabDevicePingFailureEventListener listener);
   }