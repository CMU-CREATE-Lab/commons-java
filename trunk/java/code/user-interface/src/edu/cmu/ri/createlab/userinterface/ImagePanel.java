package edu.cmu.ri.createlab.userinterface;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * <code>ImagePanel</code> is a JPanel that allows you to set a background image.  The JPanel is sized to be the same
 * size as the image.
 * </p>
 * <p>
 * Code from: http://www.java2s.com/Code/Java/Swing-JFC/Panelwithbackgroundimage.htm
 * </p>
 */
public final class ImagePanel extends JPanel
   {
   private static final Log LOG = LogFactory.getLog(ImagePanel.class);

   private Image img;

   public ImagePanel(final String path)
      {
      this(ImageUtils.createImage(path));
      }

   public ImagePanel(final Image img)
      {
      this.img = img;
      final Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
      setPreferredSize(size);
      setMinimumSize(size);
      setMaximumSize(size);
      setSize(size);
      setLayout(null);
      setBorder(BorderFactory.createEmptyBorder());
      if (LOG.isTraceEnabled())
         {
         LOG.trace("ImagePanel.ImagePanel(" + size + ")");
         }
      }

   public void paintComponent(final Graphics g)
      {
      g.drawImage(img, 0, 0, null);
      }

   public void addAtXY(final Component component, final int x, final int y)
      {
      add(component);
      if (LOG.isTraceEnabled())
         {
         LOG.trace("ImagePanel.addAtXY(" + x + "," + y + "," + this.getInsets().left + "" + this.getInsets().top + ")");
         }
      final Insets insets = this.getInsets();
      final Dimension size = component.getPreferredSize();
      component.setBounds(x + insets.left,
                          y + insets.top,
                          size.width,
                          size.height);
      }
   }

