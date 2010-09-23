package edu.cmu.ri.createlab.userinterface.util;

import java.awt.Component;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.apache.log4j.Logger;

/**
 * <p>
 * <code>DialogHelper</code> provides an easy way for GUI applications to display alerts to the user, and ensure that
 * it executes from within the Swing GUI thread.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class DialogHelper
   {
   private static final Logger LOG = Logger.getLogger(DialogHelper.class);

   /** Shows an error message using the given title and message. */
   public static void showErrorMessage(final String title, final String message)
   {
   showErrorMessage(title, message, null);
   }

   /** Shows an error message using the given title and message. */
   public static void showErrorMessage(final String title, final String message, final Component parentComponent)
   {
   showMessageDialog(title, message, JOptionPane.ERROR_MESSAGE, parentComponent);
   }

   /** Shows an informational message using the given title and message. */
   public static void showInfoMessage(final String title, final String message)
   {
   showInfoMessage(title, message, null);
   }

   /** Shows an informational message using the given title and message. */
   public static void showInfoMessage(final String title, final String message, final Component parentComponent)
   {
   showMessageDialog(title, message, JOptionPane.INFORMATION_MESSAGE, parentComponent);
   }

   /**
    * Shows an question message using the given title and message.  Returns <code>true</code> if the user answered Yes,
    * <code>false</code> otherwise.
    */
   public static boolean showYesNoDialog(final String title, final String message)
   {
   return showYesNoDialog(title, message, null);
   }

   /**
    * Shows an question message using the given title and message.  Returns <code>true</code> if the user answered Yes,
    * <code>false</code> otherwise.
    */
   public static boolean showYesNoDialog(final String title, final String message, final Component parentComponent)
   {
   if (SwingUtilities.isEventDispatchThread())
      {
      return JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(parentComponent, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
      }
   else
      {
      final int[] response = new int[]{JOptionPane.NO_OPTION};

      try
         {
         SwingUtilities.invokeAndWait(
               new Runnable()
               {
               public void run()
                  {
                  response[0] = JOptionPane.showConfirmDialog(parentComponent, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
                  }
               }
         );
         }
      catch (InterruptedException e)
         {
         LOG.error("InterruptedException while trying to do SwingUtilities.invokeAndWait()", e);
         }
      catch (InvocationTargetException e)
         {
         LOG.error("InvocationTargetException while trying to do SwingUtilities.invokeAndWait()", e);
         }

      return JOptionPane.YES_OPTION == response[0];
      }
   }

   /** Shows an informational message using the given title and message. */
   private static void showMessageDialog(final String title, final String message, final int messageType, final Component parentComponent)
   {
   if (SwingUtilities.isEventDispatchThread())
      {
      JOptionPane.showMessageDialog(parentComponent, message, title, messageType);
      }
   else
      {
      SwingUtils.runInGUIThread(
            new Runnable()
            {
            public void run()
               {
               JOptionPane.showMessageDialog(parentComponent, message, title, messageType);
               }
            });
      }
   }

   private DialogHelper()
      {
      // private to prevent instantiation
      }
   }
