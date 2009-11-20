package edu.cmu.ri.createlab.userinterface;

import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.plaf.basic.BasicLabelUI;

/**
 * <p>
 * <code>VerticalLabelUI</code> enables JLabels to render their text vertically, rotated either clockwise or counter-
 * clockwise.
 * </p>
 * <p>
 * Code from <a href="http://www.codeguru.com/java/articles/199.shtml">http://www.codeguru.com/java/articles/199.shtml</a>
 * </p>
 *
 * @author Zafir Anjum
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class VerticalLabelUI extends BasicLabelUI
   {
   static
      {
      labelUI = new VerticalLabelUI(false);
      }

   private boolean clockwise;

   public VerticalLabelUI(final boolean clockwise)
      {
      super();
      this.clockwise = clockwise;
      }

   @SuppressWarnings({"SuspiciousNameCombination"})
   public Dimension getPreferredSize(final JComponent c)
      {
      final Dimension dim = super.getPreferredSize(c);

      // width and height are swapped on purpose here!!!
      return new Dimension(dim.height, dim.width);
      }

   public void paint(final Graphics g, final JComponent c)
      {
      final Rectangle paintIconR = new Rectangle();
      final Rectangle paintTextR = new Rectangle();
      final Rectangle paintViewR = new Rectangle();

      final JLabel label = (JLabel)c;
      final String text = label.getText();
      final Icon icon = (label.isEnabled()) ? label.getIcon() : label.getDisabledIcon();

      if ((icon == null) && (text == null))
         {
         return;
         }

      final FontMetrics fm = g.getFontMetrics();
      final Insets paintViewInsets = c.getInsets(new Insets(0, 0, 0, 0));

      paintViewR.x = paintViewInsets.left;
      paintViewR.y = paintViewInsets.top;

      // Use inverted height & width
      paintViewR.height = c.getWidth() - (paintViewInsets.left + paintViewInsets.right);
      paintViewR.width = c.getHeight() - (paintViewInsets.top + paintViewInsets.bottom);

      paintIconR.x = paintIconR.y = paintIconR.width = paintIconR.height = 0;
      paintTextR.x = paintTextR.y = paintTextR.width = paintTextR.height = 0;

      final String clippedText = layoutCL(label, fm, text, icon, paintViewR, paintIconR, paintTextR);

      final Graphics2D g2 = (Graphics2D)g;
      final AffineTransform tr = g2.getTransform();
      if (clockwise)
         {
         g2.rotate(Math.PI / 2);
         g2.translate(0, -c.getWidth());
         }
      else
         {
         g2.rotate(-Math.PI / 2);
         g2.translate(-c.getHeight(), 0);
         }

      if (icon != null)
         {
         icon.paintIcon(c, g, paintIconR.x, paintIconR.y);
         }

      if (text != null)
         {
         final int textX = paintTextR.x;
         final int textY = paintTextR.y + fm.getAscent();

         if (label.isEnabled())
            {
            paintEnabledText(label, g, clippedText, textX, textY);
            }
         else
            {
            paintDisabledText(label, g, clippedText, textX, textY);
            }
         }

      g2.setTransform(tr);
      }
   }
