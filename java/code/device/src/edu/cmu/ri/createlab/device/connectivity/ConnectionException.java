package edu.cmu.ri.createlab.device.connectivity;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public class ConnectionException extends RuntimeException
   {
   public ConnectionException()
      {
      }

   public ConnectionException(final String s)
      {
      super(s);
      }

   public ConnectionException(final String s, final Throwable throwable)
      {
      super(s, throwable);
      }

   public ConnectionException(final Throwable throwable)
      {
      super(throwable);
      }
   }
