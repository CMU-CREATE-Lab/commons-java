package edu.cmu.ri.createlab.userinterface.util;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;
import edu.cmu.ri.createlab.userinterface.GUIConstants;
import edu.cmu.ri.createlab.userinterface.VerticalLabelUI;

/**
 * <p>
 * <code>SwingUtils</code> is utility class useful for creating and working with Swing GUIs.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class SwingUtils
   {
   /**
    * Executes the given {@link Runnable} in the event dispatch thread.
    */
   public static void runInGUIThread(final Runnable runnable)
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

   /** Creates a 5 x 5 rigid spacer. */
   public static Component createRigidSpacer()
   {
   return createRigidSpacer(5);
   }

   /** Creates a <code>size</code> x <code>size</code> rigid spacer. */
   public static Component createRigidSpacer(final int size)
   {
   return createRigidSpacer(size, size);
   }

   /** Creates a <code>width</code> x <code>height</code> rigid spacer. */
   public static Component createRigidSpacer(final int width, final int height)
   {
   return Box.createRigidArea(new Dimension(width, height));
   }

   public static JLabel createLabel(final String labelText)
      {
      return createLabel(labelText, GUIConstants.FONT_NORMAL);
      }

   public static JLabel createTinyFontLabel(final String labelText)
      {
      return createLabel(labelText, GUIConstants.FONT_TINY);
      }

   public static JLabel createLabel(final String labelText, final Font font)
      {
      final JLabel label = new JLabel(labelText);
      label.setFont(font);
      return label;
      }

   public static JLabel createVerticalLabel(final String labelText, final boolean isRotatedClockwise)
      {
      return createVerticalLabel(labelText, GUIConstants.FONT_NORMAL, isRotatedClockwise);
      }

   public static JLabel createVerticalTinyFontLabel(final String labelText, final boolean isRotatedClockwise)
      {
      return createVerticalLabel(labelText, GUIConstants.FONT_TINY, isRotatedClockwise);
      }

   private static JLabel createVerticalLabel(final String labelText, final Font font, final boolean isRotatedClockwise)
      {
      final JLabel label = new JLabel(labelText, JLabel.CENTER);
      label.setUI(new VerticalLabelUI(isRotatedClockwise));
      label.setFont(font);

      return label;
      }

   /** Creates a disabled button with the given <code>label</code>. */
   public static JButton createButton(final String label)
   {
   return createButton(label, false);
   }

   /** Creates a button with the given <code>label</code> and with an enabled state specified by <code>isEnabled</code>. */
   public static JButton createButton(final String label, final boolean isEnabled)
   {
   final JButton button = new JButton(label);
   button.setFont(GUIConstants.BUTTON_FONT);
   button.setEnabled(isEnabled);
   button.setOpaque(false);// required for Macintosh
   return button;
   }

   /** Creates a image button using the images specified by the given paths. */
   public static JButton createImageButton(final String defaultIconPath,
                                           final String disabledIconPath,
                                           final String pressedIconPath,
                                           final String rolloverIconPath)
   {
   final JButton button = new JButton();
   button.setIcon(ImageUtils.createImageIcon(defaultIconPath));
   button.setDisabledIcon(ImageUtils.createImageIcon(disabledIconPath));
   button.setPressedIcon(ImageUtils.createImageIcon(pressedIconPath));
   button.setRolloverIcon(ImageUtils.createImageIcon(rolloverIconPath));
   button.setBorder(null);
   button.setBorderPainted(false);
   button.setIconTextGap(0);
   button.setBorder(BorderFactory.createEmptyBorder());

   return button;
   }

   private SwingUtils()
      {
      // private to prevent instantiation
      }
   }
