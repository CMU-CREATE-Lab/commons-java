package edu.cmu.ri.createlab.usb.hid;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class HIDConnectionException extends HIDException
   {
   public HIDConnectionException()
      {
      }

   public HIDConnectionException(final String message)
      {
      super(message);
      }

   public HIDConnectionException(final String message, final Throwable cause)
      {
      super(message, cause);
      }

   public HIDConnectionException(final Throwable cause)
      {
      super(cause);
      }
   }
