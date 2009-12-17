package edu.cmu.ri.createlab.userinterface.util;

import java.awt.Component;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import edu.cmu.ri.createlab.userinterface.ImageFormat;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * <p>
 * <code>ImageUtils</code> provides methods for dealing with images.
 * </p>
 *
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class ImageUtils
   {
   private static final Log LOG = LogFactory.getLog(ImageUtils.class);

   /**
    * <p>
    * Returns an {@link Image}, or <code>null</code> if the path was invalid.
    * </p>
    */
   public static Image createImage(final String path)
      {
      final ImageIcon imageIcon = createImageIcon(path);
      if (imageIcon != null)
         {
         return imageIcon.getImage();
         }
      return null;
      }

   /**
    * <p>
    * Returns an {@link ImageIcon}, or <code>null</code> if the path was invalid.
    * </p>
    * <p>
    * Code taken from <a href="http://java.sun.com/docs/books/tutorial/uiswing/misc/icon.html">http://java.sun.com/docs/books/tutorial/uiswing/misc/icon.html</a>.
    * </p>
    */
   public static ImageIcon createImageIcon(final String path)
      {
      final URL imgURL = ImageUtils.class.getResource(path);

      if (imgURL != null)
         {
         return new ImageIcon(imgURL);
         }
      else
         {
         if (LOG.isErrorEnabled())
            {
            LOG.error("Couldn't find image file: " + path);
            }
         return null;
         }
      }

   /**
    * Saves the given {@link Component} to the given {@link File}, using the given {@link ImageFormat}.  Returns
    * <code>true</code> upon success, <code>false</code> otherwise.
    *
    * @throws IllegalArgumentException if any parameter is null.
    * @throws IOException if an error occurs during writing.
    */
   public static boolean saveComponentAsImage(final Component imageobject, final File targetFile, final ImageFormat imageFormat) throws IOException
      {
      if ((imageobject == null) || (targetFile == null) || (imageFormat == null))
         {
         throw new IllegalArgumentException("null argument(s) given to saveComponentAsImage()");
         }

      // get the picture
      final BufferedImage bimg = new BufferedImage(imageobject.getWidth(), imageobject.getHeight(), BufferedImage.TYPE_INT_RGB);//create 8-bit RGB buffer
      imageobject.printAll(bimg.createGraphics());

      // save image to a file
      File file = targetFile;
      if (!targetFile.toString().endsWith(imageFormat.getExtension()))
         {
         // append extension
         file = new File(targetFile.toString() + imageFormat.getExtension());
         }

      return ImageIO.write(bimg, imageFormat.getName(), file);
      }

   /**
    * <p>
    * Loads and returns the {@link Image} specified by the given {@link URL}.  Returns <code>null</code> if the given
    * {@link URL} is <code>null</code> or if an error occurred while obtaining the image.
    * </p>
    * <p>
    * Based on code at http://forum.java.sun.com/thread.jspa?threadID=431904&messageID=3692773
    * </p>
    */
   public static Image loadImageFromURL(final URL url)
      {
      if (url != null)
         {
         try
            {
            final URLConnection conn = url.openConnection();
            conn.connect();

            final BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
            final ByteArrayOutputStream out = new ByteArrayOutputStream();

            int c;
            while ((c = in.read()) != -1)
               {
               out.write(c);
               }

            out.close();
            in.close();

            return Toolkit.getDefaultToolkit().createImage(out.toByteArray());
            }
         catch (IOException e)
            {
            LOG.error("IOException while trying to load image from URL [" + url + "]", e);
            }
         }
      return null;
      }

   private ImageUtils()
      {
      // private to prevent instantiation
      }
   }
