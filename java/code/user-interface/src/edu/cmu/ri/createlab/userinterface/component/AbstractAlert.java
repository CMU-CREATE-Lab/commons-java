package edu.cmu.ri.createlab.userinterface.component;

import java.awt.Component;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * <p>
 * <code>AbstractAlert</code> provides an easy way for GUI applications to display alerts to the user, and ensure that
 * it executes from within the Swing GUI thread.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public abstract class AbstractAlert
   {
   private final Component parentComponent;

   public AbstractAlert(final Component parentComponent)
      {
      this.parentComponent = parentComponent;
      }

   protected final Component getParentComponent()
      {
      return parentComponent;
      }

   /** Shows an error message using the given title and message. */
   protected final void showErrorMessage(final String title, final String message)
      {
      showDialog(title, message, JOptionPane.ERROR_MESSAGE);
      }

   /** Shows an informational message using the given title and message. */
   protected final void showInfoMessage(final String title, final String message)
      {
      showDialog(title, message, JOptionPane.INFORMATION_MESSAGE);
      }

   /** Shows an informational message using the given title and message. */
   private void showDialog(final String title, final String message, final int messageType)
      {
      runInGUIThread(
            new Runnable()
            {
            public void run()
               {
               JOptionPane.showMessageDialog(parentComponent, message, title, messageType);
               }
            });
      }

   protected final void runInGUIThread(final Runnable runnable)
      {
      if (SwingUtilities.isEventDispatchThread())
         {
         runnable.run();
         }
      else
         {
         SwingUtilities.invokeLater(runnable);
         }
      }
   }
