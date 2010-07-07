package edu.cmu.ri.createlab.usb.hid;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class HIDException extends Exception
   {
   public HIDException()
      {
      }

   public HIDException(final String message)
      {
      super(message);
      }

   public HIDException(final String message, final Throwable cause)
      {
      super(message, cause);
      }

   public HIDException(final Throwable cause)
      {
      super(cause);
      }
   }
