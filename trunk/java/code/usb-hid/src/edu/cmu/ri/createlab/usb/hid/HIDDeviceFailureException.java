package edu.cmu.ri.createlab.usb.hid;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class HIDDeviceFailureException extends HIDException
   {
   public HIDDeviceFailureException()
      {
      }

   public HIDDeviceFailureException(final String message)
      {
      super(message);
      }

   public HIDDeviceFailureException(final String message, final Throwable cause)
      {
      super(message, cause);
      }

   public HIDDeviceFailureException(final Throwable cause)
      {
      super(cause);
      }
   }
