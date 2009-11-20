package edu.cmu.ri.createlab.serial;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class SerialPortException extends Exception
   {
   public SerialPortException()
      {
      super();
      }

   public SerialPortException(final String message)
      {
      super(message);
      }

   public SerialPortException(final String message, final Throwable cause)
      {
      super(message, cause);
      }

   public SerialPortException(final Throwable cause)
      {
      super(cause);
      }
   }
