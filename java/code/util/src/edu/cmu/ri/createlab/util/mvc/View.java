package edu.cmu.ri.createlab.util.mvc;

import javax.swing.SwingUtilities;

/**
 * <p>
 * <code>View</code> provides common functionality for MVC view classes.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public abstract class View<T> implements MVCEventListener<T>
   {
   public final void handleEvent(final T eventData)
      {
      if (SwingUtilities.isEventDispatchThread())
         {
         handleEventInGUIThread(eventData);
         }
      else
         {
         SwingUtilities.invokeLater(
               new Runnable()
               {
               public void run()
                  {
                  handleEventInGUIThread(eventData);
                  }
               });
         }
      }

   /**
    * Method for handling the event, guaranteed to run in the GUI thread.
    */
   protected abstract void handleEventInGUIThread(final T eventData);
   }

