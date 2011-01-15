package edu.cmu.ri.createlab.userinterface.util;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;

/**
 * <p>
 * <code>AbstractTimeConsumingAction</code> provides an easy way to execute time-consuming actions in a separate thread
 * (that is, not the GUI event-dispatching thread) with hooks to optionally perform GUI tasks before and after the
 * time-consuming action.  The class provides optional support for automatically changing the cursor to the
 * system-defined wait cursor (see {@link Cursor#WAIT_CURSOR}).
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
@SuppressWarnings({"CloneableClassWithoutClone"})
public abstract class AbstractTimeConsumingAction extends AbstractAction
   {
   private static final Logger LOG = Logger.getLogger(AbstractTimeConsumingAction.class);

   private final Component component;

   /** Constructs an {@link AbstractTimeConsumingAction} that will not change the cursor. */
   protected AbstractTimeConsumingAction()
      {
      this(null);
      }

   /**
    * Constructs an {@link AbstractTimeConsumingAction} that changes the cursor to the wait cursor
    * (see {@link Cursor#WAIT_CURSOR}) immediately before calling {@link #executeGUIActionBefore()} and changes it
    * back to the default cursor (see {@link Cursor#getDefaultCursor()}) immediately after calling
    * {@link #executeGUIActionAfter(Object)}.  If the given {@link Component} is <code>null</code>, the cursor will not
    * be changed and the object behaves exactly as if the caller had created it with the no-arg constructor
    * {@link #AbstractTimeConsumingAction()}.
    *
    * @see Component#setCursor(Cursor)
    */
   protected AbstractTimeConsumingAction(final Component component)
      {
      this.component = component;
      }

   private final Runnable action =
         new Runnable()
         {
         public void run()
            {
            if (component != null)
               {
               component.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
               }
            try
               {
               executeGUIActionBefore();
               }
            catch (Exception e)
               {
               LOG.error("Exception caught while executing executeGUIActionBefore()", e);
               }

            // execute the time-consuming action in a SwingWorker, so it doesn't bog down the GUI thread
            final SwingWorker swingWorker =
                  new SwingWorker()
                  {
                  public Object construct()
                     {
                     Object obj;
                     try
                        {
                        obj = executeTimeConsumingAction();
                        }
                     catch (Exception e)
                        {
                        LOG.error("Exception caught while executing executeTimeConsumingAction().  Returning null object.", e);
                        obj = null;
                        }

                     return obj;
                     }

                  public void finished()
                     {
                     try
                        {
                        executeGUIActionAfter(get());
                        }
                     catch (Exception e)
                        {
                        LOG.error("Exception caught while executing executeGUIActionAfter().", e);
                        }
                     if (component != null)
                        {
                        component.setCursor(Cursor.getDefaultCursor());
                        }
                     }
                  };
            swingWorker.start();
            }
         };

   public final void actionPerformed(final ActionEvent event)
      {
      SwingUtilities.invokeLater(action);
      }

   /** Runs in the GUI event-dispatching thread before the time-consuming action is executed. */
   @SuppressWarnings({"NoopMethodInAbstractClass"})
   protected void executeGUIActionBefore()
      {
      // do nothing by default
      }

   /**
    * Runs in a new thread so the GUI event-dispatching thread doesn't get bogged down. The {@link Object} returned from
    * this method will be passed to {@link #executeGUIActionAfter(Object)}.
    */
   protected abstract Object executeTimeConsumingAction();

   /** Runs in the GUI event-dispatching thread after the time-consuming action is executed. */
   @SuppressWarnings({"NoopMethodInAbstractClass"})
   protected void executeGUIActionAfter(final Object resultOfTimeConsumingAction)
      {
      // do nothing by default
      }
   }
