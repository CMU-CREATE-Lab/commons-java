package edu.cmu.ri.createlab.video;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import com.lti.civil.CaptureDeviceInfo;
import com.lti.civil.CaptureException;
import com.lti.civil.CaptureObserver;
import com.lti.civil.CaptureStream;
import com.lti.civil.CaptureSystem;
import com.lti.civil.CaptureSystemFactory;
import com.lti.civil.DefaultCaptureSystemFactorySingleton;
import com.lti.civil.Image;
import com.lti.civil.VideoFormat;
import com.lti.civil.awt.AWTImageConverter;

/**
 * Provides methods for displaying and using webcam image data.
 *
 * @author Tom Lauwers (tlauwers@andrew.cmu.edu)
 */

@SuppressWarnings({"UseOfSystemOutOrSystemErr"})
public final class VideoPlayer
   {
   private CaptureStream captureStream;
   private CaptureSystem system;

   // Stores the most recent image to be received from
   // the capture observer
   private BufferedImage videoImage;

   // Instantiate a JFrame to display the video
   private JFrame jFrameVideo;

   // Video panel is a class that extends JPanel to
   // create a panel to display the videoImage
   private VideoPanel videoPanel;

   // Create a generic polygon object to draw rectangles,
   // circles, etc into the image
   private Polygon myPolygon;

   // Create a color object that holds the color of the
   // drawn polygon
   private Color polygonColor;

   // Allow the user to set whether the polygon drawn
   // is an outlined or is fully filled in
   private boolean setFill = false;

   // Used to transfer the raw image from the capture observer
   // to this class through the setImage/getImage methods
   private Image image;

   // Used to prevent a getImage when the image is in the
   // process of being set
   private final byte[] imageSynchronizationLock = new byte[0];

   /**
    *  Instantiating the video player sets up the capture stream
    *  but does not start the Capture observer.  This means that
    *  instantiation does not cause any image data to be received;
    *  that task falls to startVideoStream.
    */
   public VideoPlayer()
   {
   final CaptureSystemFactory factory = DefaultCaptureSystemFactorySingleton.instance();

   try
      {
      system = factory.createCaptureSystem();
      system.init();
      final List list = system.getCaptureDeviceInfoList();
      final CaptureDeviceInfo info = (CaptureDeviceInfo)list.get(0);
      captureStream = system.openCaptureDeviceStream(info.getDeviceID());
      }
   catch (CaptureException e)
      {
      System.out.println("Video player did not instantiate");
      e.printStackTrace();
      }
   }

   /**
    *   Starts a new capture observer.  Capture observer runs in a separate, asynchronous
    *   thread and constantly gets images from the camera.  startVideoStream also chooses
    *   format of the video to use - if a 320x240 format is available, it chooses that.  If
    *   it's not available, it chooses the first format available.
    *   It will always print out the format it is using.
    */
   public void startVideoStream()
   {
   captureStream.setObserver(new MyCaptureObserver());
   try
      {
      VideoFormat format = null;
      for (int i = 0; i < captureStream.enumVideoFormats().size(); i++)
         {
         format = captureStream.enumVideoFormats().get(i);
         if (format.getWidth() == 320)
            {
            break;
            }
         }
      if (format.getWidth() != 320)
         {
         format = captureStream.enumVideoFormats().get(0);
         }
      captureStream.setVideoFormat(format);
      System.out.println("Current video format " + videoFormatToString(captureStream.getVideoFormat()));
      captureStream.start();
      // Sleep for 5 seconds to give the camera time to start
      try
         {
         Thread.sleep(5000);
         }
      catch (InterruptedException e)
         {
         e.printStackTrace();
         }
      }
   catch (CaptureException e)
      {
      e.printStackTrace();
      }
   }

   /**
    * Stops the capture stream and the capture observer.  Use this
    * if you want to pause the thread capturing images to save CPU
    * time.
    */
   public void stopVideoStream()
   {
   try
      {
      captureStream.stop();
      }
   catch (CaptureException e)
      {
      e.printStackTrace();
      }
   }

   /**
    * Closes the video stream and capture observer and disposes it.
    * You will need to instantiate a new VideoPlayer object if you want
    * to restart video after calling this.  It is strongly recommended that you
    * call this before exiting your program.
    */
   public void closeVideoStream()
   {
   try
      {
      if (jFrameVideo != null)
         {
         closeVideo();
         }
      captureStream.dispose();
      system.dispose();
      }
   catch (CaptureException e)
      {
      e.printStackTrace();
      }
   }

   // Converts video Format object to a printable string

   private static String videoFormatToString(final VideoFormat f)
      {
      return "Type=" + formatTypeToString(f.getFormatType()) + " Width=" + f.getWidth() + " Height=" + f.getHeight() + " FPS=" + f.getFPS();
      }

   private static String formatTypeToString(final int f)
      {
      switch (f)
         {
         case VideoFormat.RGB24:
            return "RGB24";
         case VideoFormat.RGB32:
            return "RGB32";
         default:
            return "" + f + " (unknown)";
         }
      }

   /**
    * Returns the most recent image captured by the capture observer
    * as a buffered image
    * @return Most recent image from the camera
    */
   public BufferedImage getImage()
   {
   synchronized (imageSynchronizationLock)
      {
      if (image != null)
         {
         videoImage = AWTImageConverter.toBufferedImage(image);
         return videoImage;
         }
      else
         {
         return null;
         }
      }
   }

   /**
    * Gets the RGB values of a given pixel as an integer array.
    * Element 0 is the red value, 1 is green, and 2 is blue.  All values are
    * 8-bit, ranging from 0 to 255 with higher values indicating higher intensity
    * of that color.
    * @param x the x-coordinate of the pixel to get
    * @param y the y-coordinate of the pixel to get
    * @return a 3 element array holding the red, green, and blue intensity of the pixel
    */
   public int[] getPixelRGBValues(final int x, final int y)
   {
   if (videoImage != null)
      {
      if (x < getImageWidth() && x >= 0 && y < getImageHeight() && y >= 0)
         {
         final int pixelVals = videoImage.getRGB(x, y);
         final int[] vals = new int[3];
         // Converting from ARGB format
         vals[0] = 255 + ((pixelVals % 16777216) / 65536);
         vals[1] = 255 + ((pixelVals % 65536) / 256);
         vals[2] = 255 + (pixelVals % 256);
         return vals;
         }
      else
         {
         System.out.println("X and Y coordinates for getImagePixels were out of range");
         return null;
         }
      }
   else
      {
      System.out.println("Image was null");
      return null;
      }
   }

   /**
    * Gets the Color of a given pixel at the coordinate specified by x,y
    * @param x The row of the pixel
    * @param y The column of the pixel
    * @return A Color object representing the color of the pixel
    */
   public Color getPixelColor(final int x, final int y)
   {
   final int[] vals = getPixelRGBValues(x, y);

   if (vals != null)
      {
      return new Color(vals[0], vals[1], vals[2]);
      }
   else
      {
      System.out.println("Image was null");
      return null;
      }
   }

   /**
    * Gets the AVERAGE RGB values of the pixels in a portion of the image.
    * The user specifies the minimum X,Y and the maximum X,Y coordinates and
    * the method calculates the average values in the rectangle described by
    * those coordinates.
    * @param minX minimum X coordinate of rectangle
    * @param minY minimum Y coordinate of rectangle
    * @param maxX maximum X coordinate of rectangle
    * @param maxY maximum Y coordinate of rectangle
    * @return a 3 element array holding the red, green, and blue intensities of the area
    */
   public int[] getAreaRGBValues(final int minX, final int minY, final int maxX, final int maxY)
   {
   if (minX > maxX || minY > maxY)
      {
      System.out.println("Error: Minimum is greater than maximum for getAreaRGBValues");
      return null;
      }

   final int[] vals = {0, 0, 0};
   for (int j = minY; j <= maxY; j++)
      {
      for (int i = minX; i <= maxX; i++)
         {
         final int[] tempVals = getPixelRGBValues(i, j);
         vals[0] += tempVals[0]; // red
         vals[1] += tempVals[1]; // green
         vals[2] += tempVals[2]; // blue
         }
      }
   final int numPixels = (maxY - minY + 1) * (maxX - minX + 1);
   vals[0] /= numPixels;
   vals[1] /= numPixels;
   vals[2] /= numPixels;

   return vals;
   }

   /**
    * Gets the AVERAGE Color value of the pixels in a portion of the image.
    * The user specifies the minimum X,Y and the maximum X,Y coordinates and
    * the method calculates the average color in the rectangle described by
    * those coordinates.
    * @param minX minimum X coordinate of rectangle
    * @param minY minimum Y coordinate of rectangle
    * @param maxX maximum X coordinate of rectangle
    * @param maxY maximum Y coordinate of rectangle
    * @return a Color object holding the average color of the area
    */

   public Color getAreaColor(final int minX, final int minY, final int maxX, final int maxY)
   {
   final int[] vals = getAreaRGBValues(minX, minY, maxX, maxY);
   if (vals != null)
      {
      return new Color(vals[0], vals[1], vals[2]);
      }
   else
      {
      System.out.println("Image was null");
      return null;
      }
   }

   private void setImage(final Image image)
      {
      synchronized (imageSynchronizationLock)
         {
         this.image = image;
         }
      }

   /**
    * Get the image height
    * @return image height as an int
    */
   public int getImageHeight()
   {
   return videoImage.getHeight();
   }

   /**
    * Get the image width
    * @return image width as an int
    */
   public int getImageWidth()
   {
   return videoImage.getWidth();
   }

   /**
    * Creates a JFrame and sets it visible.  The JFrame starts with an image
    * that contains the most recent image from the camera.  Note that
    * it is important to continue to call updateVideo once drawVideo has
    * been called to update the image in the JFrame.
    * @param name The title of the JFrame
    */
   public void drawVideo(final String name)
   {
   getImage();
   if (videoImage != null)
      {
      SwingUtilities.invokeLater(
            new Runnable()
            {
            public void run()
               {
               // create the main frame
               jFrameVideo = new JFrame(name);

               videoPanel = new VideoPanel();
               jFrameVideo.add(videoPanel);
               videoPanel.update();

               // set various properties for the JFrame
               jFrameVideo.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
               jFrameVideo.addWindowListener(
                     new WindowAdapter()
                     {
                     @Override
                     public void windowClosing(final WindowEvent e)
                        {
                        jFrameVideo.setVisible(false);
                        jFrameVideo.dispose();
                        }
                     });
               jFrameVideo.setBackground(Color.WHITE);
               jFrameVideo.setResizable(false);
               //jFrameVideo.pack();
               jFrameVideo.setSize(getImageWidth(), getImageHeight());
               jFrameVideo.setLocation(400, 200);// center the window on the screen
               jFrameVideo.setVisible(true);
               }
            });
      }
   else
      {
      System.out.println("Image was null");
      }
   }

   /**
    * Draws a rectangle in the JFrame showing the camera image.  Note
    * that once called, the rectangle will be persistent across all calls
    * of updateVideo.  To remove it, call drawNothing.  To change its color,
    * call setPolygonColor.  To change whether the rectangle is an outline
    * or filled in, call setFillPolygon.
    * @param minX minimum X coordinate of rectangle
    * @param minY maximum X coordinate of rectangle
    * @param maxX minimum Y coordinate of rectangle
    * @param maxY maximum Y coordinate of rectangle
    */
   public void drawRectangle(final int minX, final int minY, final int maxX, final int maxY)
   {
   final int[] xpoints = {minX, minX, maxX, maxX};
   final int[] ypoints = {minY, maxY, maxY, minY};
   myPolygon = new Polygon(xpoints, ypoints, 4);
   }

   /**
    * Draws a generic polygon into the image.  Note
    * that once called, the polygon will be persistent across all calls
    * of updateVideo.  To remove it, call drawNothing.  To change its color,
    * call setPolygonColor.  To change whether the rectangle is an outline
    * or filled in, call setFillPolygon.
    * @param poly The polygon object to draw into the image
    */
   public void drawPolygon(final Polygon poly)
   {
   myPolygon = poly;
   }

   /**
    * Sets the color of any polygon, rectangle, or circle drawn into
    * the image.
    * @param polyColor The color to set the polygon to.
    */
   public void setPolygonColor(final Color polyColor)
   {
   polygonColor = polyColor;
   }

   /**
    * Sets whether the polygon is filled in or an outline.
    * @param setting true sets the polygon to be filled in, false sets it to outline
    */
   public void setFillPolygon(final boolean setting)
   {
   setFill = setting;
   }

   /**
    * Draws a circle on the camera image
    * @param radius The radius of the circle in pixels
    * @param centerX The X coordinate of the center of the circle
    * @param centerY The Y coordinate of the center of the circle
    */
   public void drawCircle(final int radius, final int centerX, final int centerY)
   {
   final int numPoints = 20;
   final int[] xpoints = new int[numPoints];
   final int[] ypoints = new int[numPoints];

   for (int i = 0; i < numPoints; i++)
      {
      xpoints[i] = (int)(radius * Math.cos((Math.PI * 2 / numPoints) * i)) + centerX;
      ypoints[i] = (int)(radius * Math.sin((Math.PI * 2 / numPoints) * i)) + centerY;
      }
   myPolygon = new Polygon(xpoints, ypoints, numPoints);
   }

   /**
    * Call this if you want to no longer display a polygon on the
    * camera image.
    */
   public void drawNothing()
   {
   myPolygon = null;
   }

   /**
    * Method for getting back calibration values for the blob detector method.
    * Draws a rectangle on the screen and holds it there for five seconds.  To calibrate on an
    * object, make sure that it is entirely within the rectangle.  Calibration occurs at
    * the end of the method, so it is only necessary to have the object positioned properly
    * at the end of the five seconds.
    *
    * @return a 3 element array of red, green, and blue color values of the blob to be tracked
    */
   public int[] blobCalibration()
   {
   if (videoImage != null)
      {
      drawRectangle(videoImage.getWidth() / 2 - 20, videoImage.getHeight() / 2 - 20, videoImage.getWidth() / 2 + 20, videoImage.getHeight() / 2 + 20);
      setPolygonColor(Color.RED);
      setFillPolygon(false);
      int i = 0;
      while (i < 50)
         {
         updateVideo();
         i++;

         try
            {
            Thread.sleep(100);
            }
         catch (InterruptedException e)
            {
            e.printStackTrace();
            }
         }
      final int[] calibrateVals = getAreaRGBValues(videoImage.getWidth() / 2 - 20, videoImage.getHeight() / 2 - 20, videoImage.getWidth() / 2 + 20, videoImage.getHeight() / 2 + 20);
      drawNothing();
      updateVideo();
      return calibrateVals;
      }
   else
      {
      System.out.println("No image displayed, can't calibrate, make sure to update video");
      return null;
      }
   }

   /**
    * The blob detector detects all of the pixels that are within a certain range of the CalibrationVals,
    * where the width of the range is determined by the value sensitivity.  What the algorithm does is:
    * 1.  For every pixel, it compares the RGB values of that pixel to the calibration values; if the pixel's
    * R, G, AND B values are within the calibration values +/- the sensitivity, then the pixel is counted.
    * 2.  Take the average of all the counted pixels' coordinates to get the center of the blob.
    * 3.  Finds the edges of the blob by traversing the rows and columns of the image and setting an edge
    * when 1/10 of the total counted pixels have been seen.  Traversal is from top to bottom and left to right
    * to find the top and left edges respectively, and from bottom to top and right to left to find the bottom
    * and right edges.
    * The detector returns an array of six ints - elements 0 and 1 are the x,y coordinates of the center of the
    * blob, elements 2 and 3 are the minimum and maximum x coordinates, while elements 4 and 5 are the min and
    * max y coordinates.
    *
    * @param calibrationVals  An array containing the RGB values of the pixel to look for
    * @param sensitivity  The sensitivity of the detector - higher values lead to more noise, while low values
    * might not pick up very much of the object being tracked.  A suggested value for a brightly colored object is 10.
    * @return An array containing the center, top left, and bottom right x,y coordinates of the blob.
    */
   public int[] blobDetector(final int[] calibrationVals, final int sensitivity)
   {

   int centerX = 0;
   int centerY = 0;
   int totalFound = 0;

   final int[] rowsFound = new int[videoImage.getWidth()];
   final int[] colsFound = new int[videoImage.getHeight()];

   // Looks for all pixels that fit within the color range of the calibrations values +/- sensitivity
   for (int j = 0; j < videoImage.getHeight(); j++)
      {
      for (int i = 0; i < videoImage.getWidth(); i++)
         {
         final int[] pixelVals = getPixelRGBValues(i, j);
         if (pixelVals[0] < (calibrationVals[0] + sensitivity) && pixelVals[0] > (calibrationVals[0] - sensitivity)
             && pixelVals[1] < (calibrationVals[1] + sensitivity) && pixelVals[1] > (calibrationVals[1] - sensitivity)
             && pixelVals[2] < (calibrationVals[2] + sensitivity) && pixelVals[2] > (calibrationVals[2] - sensitivity))
            {
            // Sum up the pixel coordinates for the center
            centerX += i;
            centerY += j;

            // Track how many pixels were found
            totalFound++;

            // Keep track of which rows and columns had pixels
            rowsFound[i]++;
            colsFound[j]++;
            }
         }
      }
   // If none were found, just return a null array
   if (totalFound == 0)
      {
      return null;
      }

   // The following four loops find the edges of the blob
   final int divisor = 10;
   int pointCounter = 0;
   int minX = 0;
   for (int i = 0; i < videoImage.getWidth(); i++)
      {
      pointCounter += rowsFound[i];
      if (pointCounter > totalFound / divisor)
         {
         minX = i;
         break;
         }
      }
   pointCounter = 0;
   int minY = 0;
   for (int i = 0; i < videoImage.getHeight(); i++)
      {
      pointCounter += colsFound[i];
      if (pointCounter > totalFound / divisor)
         {
         minY = i;
         break;
         }
      }
   pointCounter = 0;
   int maxX = 0;
   for (int i = videoImage.getWidth() - 1; i >= 0; i--)
      {
      pointCounter += rowsFound[i];
      if (pointCounter > totalFound / divisor)
         {
         maxX = i;
         break;
         }
      }
   pointCounter = 0;
   int maxY = 0;
   for (int i = videoImage.getHeight() - 1; i >= 0; i--)
      {
      pointCounter += colsFound[i];
      if (pointCounter > totalFound / divisor)
         {
         maxY = i;
         break;
         }
      }

   // Find the center by simply dividing the sum of the coordinates by the number found
   centerX /= totalFound;
   centerY /= totalFound;

   return new int[]{centerX, centerY, minX, maxX, minY, maxY};
   }

   /** Updates the video in the video frame - we suggest you call this in a loop to
    *  refresh the video frame often.  Note that you do not need to call getImage()
    *  separately if you call updateVideo().
    */
   public void updateVideo()
   {
   getImage();

   if (jFrameVideo != null)
      {
      if (videoPanel != null)
         {
         videoPanel.update();
         if (myPolygon != null)
            {
            // Create a graphics object to allow drawing polygons
            // into the videoImage
            final Graphics2D videoDrawer = videoImage.createGraphics();
            videoDrawer.setColor(polygonColor);
            if (setFill)
               {
               videoDrawer.fill(myPolygon);
               }
            else
               {
               videoDrawer.draw(myPolygon);
               }
            }
         }
      }
   }

   /** Closes and disposes of the video frame */
   public void closeVideo()
   {
   if (jFrameVideo != null)
      {
      jFrameVideo.setVisible(false);
      jFrameVideo.dispose();
      }
   else
      {
      System.out.println("Tried to close video but it was already closed (or was never opened).");
      }
   }

   // The video panel class is used to paint the video image and rectangles, polygons, etc
   // inside the video viewer JFrame.

   class VideoPanel extends JPanel
      {

      public void update()
         {
         repaint();
         }

      public void paint(final Graphics g)
         {
         g.drawImage(videoImage, 0, 0, null);
         }
      }

   // This class is a separate thread that is constantly pulling the most recent
   // image from the camera.  It then transfers that image to the VideoPlayer class
   // by calling the setImage() method.

   class MyCaptureObserver implements CaptureObserver
      {
      public void onError(final CaptureStream sender, final CaptureException e)
         {
         System.err.println("onError " + sender);
         e.printStackTrace();
         }

      public void onNewImage(final CaptureStream sender, final Image image)
         {
         setImage(image);
         }
      }
   }
