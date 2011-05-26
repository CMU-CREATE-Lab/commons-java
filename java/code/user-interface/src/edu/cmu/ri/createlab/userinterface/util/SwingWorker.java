package edu.cmu.ri.createlab.userinterface.util;

import javax.swing.SwingUtilities;

/**
 * <p>
 * This is the 3rd version of SwingWorker (also known as SwingWorker 3), an abstract class that you subclass to perform
 * GUI-related work in a dedicated thread.  For instructions on and examples of using this class, see:
 * </p>
 * <p>
 * http://java.sun.com/docs/books/tutorial/uiswing/misc/threads.html
 * </p>
 * <p>
 * Note that the API changed slightly in the 3rd version: You must now invoke {@link #start()} on the SwingWorker after
 * creating it.
 * </p>
 *
 * @deprecated Use {@link javax.swing.SwingWorker} instead.
 */
public abstract class SwingWorker
   {
   private Object value;// see getValue(), setValue()

   /** Class to maintain reference to current worker thread under separate synchronization control. */
   private static final class ThreadVar
      {
      private Thread thread;

      ThreadVar(final Thread t)
         {
         thread = t;
         }

      final synchronized Thread get()
         {
         return thread;
         }

      final synchronized void clear()
         {
         thread = null;
         }
      }

   private ThreadVar threadVar;

   /** Get the value produced by the worker thread, or null if it hasn't been constructed yet. */
   protected synchronized Object getValue()
      {
      return value;
      }

   /** Set the value produced by worker thread */
   private synchronized void setValue(final Object x)
      {
      value = x;
      }

   /** Compute the value to be returned by the <code>get</code> method. */
   public abstract Object construct();

   /**
    * Called on the event dispatching thread (not on the worker thread) after the {@link #construct} method has returned.
    */
   public void finished()
      {
      // by default, do nothing
      }

   /** A new method that interrupts the worker thread.  Call this method to force the worker to stop what it's doing. */
   public void interrupt()
      {
      final Thread t = threadVar.get();
      if (t != null)
         {
         t.interrupt();
         }
      threadVar.clear();
      }

   /**
    * Return the value created by the {@link #construct} method. Returns null if either the constructing thread or the
    * current thread was interrupted before a value was produced.
    *
    * @return the value created by the {@link #construct} method
    */
   public Object get()
      {
      while (true)
         {
         final Thread t = threadVar.get();
         if (t == null)
            {
            return getValue();
            }
         try
            {
            t.join();
            }
         catch (InterruptedException e)
            {
            // propagate
            Thread.currentThread().interrupt();
            return null;
            }
         }
      }

   /** Start a thread that will call the {@link #construct} method and then exit. */
   public SwingWorker()
      {
      final Runnable doFinished = new Runnable()
      {
      public void run()
         {
         finished();
         }
      };

      final Runnable doConstruct = new Runnable()
      {
      public void run()
         {
         try
            {
            setValue(construct());
            }
         finally
            {
            threadVar.clear();
            }

         SwingUtilities.invokeLater(doFinished);
         }
      };

      final Thread t = new Thread(doConstruct);
      threadVar = new ThreadVar(t);
      }

   /** Start the worker thread. */
   public void start()
      {
      final Thread t = threadVar.get();
      if (t != null)
         {
         t.start();
         }
      }
   }