package edu.cmu.ri.createlab.usb.hid;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class HIDDeviceNotFoundException extends HIDException
   {
   public HIDDeviceNotFoundException()
      {
      }

   public HIDDeviceNotFoundException(final String message)
      {
      super(message);
      }

   public HIDDeviceNotFoundException(final String message, final Throwable cause)
      {
      super(message, cause);
      }

   public HIDDeviceNotFoundException(final Throwable cause)
      {
      super(cause);
      }
   }
