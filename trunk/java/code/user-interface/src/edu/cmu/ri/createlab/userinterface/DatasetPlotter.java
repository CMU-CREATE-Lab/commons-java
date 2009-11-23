package edu.cmu.ri.createlab.userinterface;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JComponent;
import javax.swing.JPanel;
import edu.cmu.ri.createlab.collections.Dataset;

/**
 * @author Chris Bartley (bartley@cmu.edu)
 */
public final class DatasetPlotter<T extends Number>
   {
   public static final int DEFAULT_WIDTH = 256;
   public static final int DEFAULT_HEIGHT = 256;
   public static final Color DEFAULT_BACKGROUND_COLOR = Color.BLACK;
   public static final Color DEFAULT_PLOTTING_COLOR = Color.WHITE;
   public static final int REFRESH_PERIOD_MILLISECONDS = 50;

   private final Plot plot;
   private Color defaultPlotColor = DEFAULT_PLOTTING_COLOR;

   private final Map<Integer, Dataset<T>> datasetMap = new HashMap<Integer, Dataset<T>>();
   private final Map<Integer, Color> colorMap = new HashMap<Integer, Color>();
   private final Map<Integer, T> latestValues = new HashMap<Integer, T>();
   private final byte[] lock = new byte[0];
   private final int historyLength;
   private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

   /**
    * Creates a <code>DatasetPlotter</code> with a width of {@link #DEFAULT_WIDTH} and a height of
    * {@link #DEFAULT_HEIGHT}.  The given <code>yMin</code> and <code>yMax</code> specify the minimum and maximum values
    * for the plot's coordinate system.  It is the caller's responsibility to ensure that <code>yMin</code> is less than
    * <code>yMax</code>.  The plot refreshes at the default rate of {@link #REFRESH_PERIOD_MILLISECONDS} milliseconds.
    */
   public DatasetPlotter(final T yMin, final T yMax)
      {
      this(yMin, yMax, DEFAULT_WIDTH, DEFAULT_HEIGHT);
      }

   /**
    * Creates a <code>DatasetPlotter</code> having the given <code>width</code> and <code>height</code>.  The given
    * <code>yMin</code> and <code>yMax</code> specify the minimum and maximum values for the plot's coordinate system.
    * It is the caller's responsibility to ensure that <code>yMin</code> is less than <code>yMax</code>.  The plot
    * refreshes at the default rate of {@link #REFRESH_PERIOD_MILLISECONDS} milliseconds.
    */
   public DatasetPlotter(final T yMin, final T yMax, final int width, final int height)
      {
      this(yMin, yMax, width, height, REFRESH_PERIOD_MILLISECONDS, TimeUnit.MILLISECONDS);
      }

   /**
    * Creates a <code>DatasetPlotter</code> having the given <code>width</code> and <code>height</code>.  The given
    * <code>yMin</code> and <code>yMax</code> specify the minimum and maximum values for the plot's coordinate system.
    * It is the caller's responsibility to ensure that <code>yMin</code> is less than <code>yMax</code>.  The plot
    * refreshes at the rate specified by the given <code>refreshPeriod</code> and <code>timeUnit</code> values.
    */
   public DatasetPlotter(final T yMin, final T yMax, final int width, final int height, final long refreshPeriod, final TimeUnit timeUnit)
      {
      if (yMin == null || yMax == null)
         {
         throw new IllegalArgumentException("yMin and yMax must be non-null");
         }
      plot = new Plot(yMin, yMax, width, height);
      historyLength = width;

      executorService.scheduleAtFixedRate(
            new Runnable()
            {
            public void run()
               {
               copyLatestValuesToDatasets();
               }
            },
            0,
            refreshPeriod,
            timeUnit);
      }

   private void copyLatestValuesToDatasets()
      {
      synchronized (lock)
         {
         if (datasetMap.size() > 0)
            {
            for (final Integer index : datasetMap.keySet())
               {
               final Dataset<T> dataset = datasetMap.get(index);
               dataset.append(latestValues.get(index));
               }
            plot.repaint();
            }
         }
      }

   public JComponent getComponent()
      {
      return plot;
      }

   /**
    * Creates a dataset for plotting.  The dataset will be rendered with the color specified by a call to
    * {@link #setDefaultPlottingColor(Color)} or with {@link #DEFAULT_PLOTTING_COLOR} if that method is not called.
    *
    * @see #setDefaultPlottingColor(Color)
    */
   public int addDataset()
      {
      return addDataset(null);
      }

   /**
    * Creates a dataset for plotting.  The dataset will be rendered with the given <code>color</code>.  If
    * <code>color</code> is <code>null</code>, the default plotting color is used.
    *
    * @see #setDefaultPlottingColor(Color)
    */
   public int addDataset(final Color color)
      {
      final Dataset<T> dataset = new Dataset<T>(historyLength);

      return addDataset(dataset, color);
      }

   private int addDataset(final Dataset<T> dataset, final Color color)
      {
      if (dataset == null)
         {
         throw new IllegalArgumentException("dataset must be non-null.");
         }

      synchronized (lock)
         {
         final int index = datasetMap.size();
         datasetMap.put(index, dataset);
         colorMap.put(index, color);
         latestValues.put(index, null);

         return index;
         }
      }

   /**
    * Appends the given value(s) to the dataset(s).  This method uses the order of the given values to match them with
    * corresponding datasets.  That is, the first argument will be appended to the first dataset, the second argument
    * will be appended to the second dataset, etc.
    */
   public void setCurrentValues(final T... values)
      {
      if (values != null && values.length > 0)
         {
         synchronized (lock)
            {
            for (int index = 0; index < values.length; index++)
               {
               latestValues.put(index, values[index]);
               }
            }
         }
      }

   /**
    * Sets the default plotting color to the given <code>color</code>.  This color will be used when plotting any
    * dataset which was added without specifying a color (i.e. by calling {@link #addDataset()}).  This method
    * may be called at any time, even after the addition of datasets.
    *
    * If this method is never called, the default plotting color is {@link #DEFAULT_PLOTTING_COLOR}
    */
   public void setDefaultPlottingColor(final Color color)
      {
      synchronized (lock)
         {
         if (color != null)
            {
            defaultPlotColor = color;
            }
         }
      }

   /**
    * Sets the plot's background color to the given <code>color</code>.  Does nothing if <code>color</code> is
    * <code>null</code>.
    */
   public void setBackgroundColor(final Color color)
      {
      if (color != null)
         {
         plot.setBackground(color);
         }
      }

   private Color getDatasetColor(final Integer index)
      {
      if (index != null)
         {
         final Color color = colorMap.get(index);
         if (color != null)
            {
            return color;
            }
         }
      return defaultPlotColor;
      }

   private final class Plot extends JPanel
      {
      private final int width;
      private final Dimension size;

      private final double yMax;

      private final double multiplier;

      private Plot(final T yMin, final T yMax, final int width, final int height)
         {
         super(true);
         this.width = width;
         size = new Dimension(width, height);
         this.yMax = yMax.doubleValue();

         multiplier = (double)height / (yMax.doubleValue() - yMin.doubleValue());

         setBackground(DEFAULT_BACKGROUND_COLOR);
         }

      public Dimension getPreferredSize()
         {
         return size;
         }

      public Dimension getMaximumSize()
         {
         return size;
         }

      public Dimension getMinimumSize()
         {
         return size;
         }

      @SuppressWarnings({"NonPrivateFieldAccessedInSynchronizedContext"})
      protected void paintComponent(final Graphics g)
         {
         super.paintComponent(g);

         synchronized (lock)
            {
            if (datasetMap.size() > 0)
               {
               for (final Integer index : datasetMap.keySet())
                  {
                  final Dataset<T> dataset = datasetMap.get(index);
                  final List<T> data = dataset.getData();

                  g.setColor(getDatasetColor(index));

                  Point previousPoint = null;
                  int x = width - 1;
                  for (int i = 0; i < data.size(); i++)
                     {
                     final int y = (int)((yMax - data.get(i).doubleValue()) * multiplier);

                     if (previousPoint != null)
                        {
                        g.drawLine(x, y, previousPoint.x, previousPoint.y);
                        }
                     else
                        {
                        previousPoint = new Point(x, y);
                        g.fillRect(x, y, 1, 1);
                        }
                     previousPoint.x = x;
                     previousPoint.y = y;

                     x--;
                     }
                  }
               }
            }
         }
      }
   }