package edu.cmu.ri.createlab.util.runtime;

import org.apache.log4j.Logger;

/**
 * <p>
 * <code>ShutdownHook</code> provides a common way to register classes which need to be shutdown with {@link Runtime}.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class ShutdownHook extends Thread
   {
   private static final Logger LOG = Logger.getLogger(ShutdownHook.class);

   private final Shutdownable shutdownable;

   public ShutdownHook(final Shutdownable shutdownable)
      {
      this.shutdownable = shutdownable;
      }

   /**
    * Calls the {@link Shutdownable#shutdown() shutdown()} method of the {@link Shutdownable} object given to the constructor.
    */
   public void run()
      {
      try
         {
         shutdownable.shutdown();
         }
      catch (Exception e)
         {
         LOG.error("Exception while shutting down: ", e);
         }
      }
   }
