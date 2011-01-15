package edu.cmu.ri.createlab.util.mvc;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import edu.cmu.ri.createlab.util.thread.DaemonThreadFactory;

/**
 * <p>
 * <code>Model</code> provides common functionality for MVC model classes.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public abstract class Model<T, U>
   {
   private List<MVCEventListener<U>> eventListeners = new ArrayList<MVCEventListener<U>>();
   private ExecutorService executorService = Executors.newSingleThreadExecutor(new DaemonThreadFactory(this.getClass().getSimpleName()));

   public final synchronized void addEventListener(final MVCEventListener<U> listener)
      {
      if (listener != null)
         {
         eventListeners.add(listener);
         }
      }

   /**
    * Publishes the given object to all registered {@link MVCEventListener}s.  Publication is performed in a separate
    * thread so that control can quickly be returned to the caller.  Published objects are guaranteed to be received by
    * the listeners in the same order in which they were published.
    */
   protected final synchronized void publishEventToListeners(final U obj)
      {
      if (!eventListeners.isEmpty())
         {
         executorService.execute(
               new Runnable()
               {
               public void run()
                  {
                  for (final MVCEventListener<U> listener : eventListeners)
                     {
                     listener.handleEvent(obj);
                     }
                  }
               });
         }
      }

   public abstract U update(final T data);
   }