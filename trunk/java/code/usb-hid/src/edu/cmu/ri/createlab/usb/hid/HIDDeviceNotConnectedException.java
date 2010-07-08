package edu.cmu.ri.createlab.usb.hid;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class HIDDeviceNotConnectedException extends HIDException
   {
   public HIDDeviceNotConnectedException()
      {
      }

   public HIDDeviceNotConnectedException(final String message)
      {
      super(message);
      }

   public HIDDeviceNotConnectedException(final String message, final Throwable cause)
      {
      super(message, cause);
      }

   public HIDDeviceNotConnectedException(final Throwable cause)
      {
      super(cause);
      }
   }
